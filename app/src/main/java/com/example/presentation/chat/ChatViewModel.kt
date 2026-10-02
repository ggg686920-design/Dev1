package com.example.presentation.chat

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.common.Resource
import com.example.core.di.AppContainer
import com.example.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

data class ChatUiState(
    val conversationId: String,
    val conversation: Conversation? = null,
    val currentUserId: String? = null,
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val replyingToMessage: Message? = null,
    val editingMessage: Message? = null,
    val isBlocked: Boolean = false,
    val isRecordingAudio: Boolean = false,
    val recordingDuration: Int = 0,
    val playingAudioUrl: String? = null,
    val isAudioPlaying: Boolean = false,
    val audioProgress: Float = 0f,
    // Search in chat
    val isSearchOpen: Boolean = false,
    val inChatSearchQuery: String = "",
    val matchedMessageIds: List<String> = emptyList(),
    val currentSearchMatchIndex: Int = 0,
    // Multi-select
    val isMultiSelectMode: Boolean = false,
    val selectedMessageIds: Set<String> = emptySet(),
    // Typing
    val isOtherTyping: Boolean = false,
    // Forwarding
    val showForwardDialog: Boolean = false,
    val messageToForward: Message? = null
)

class ChatViewModel(
    private val conversationId: String,
    private val container: AppContainer
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ChatUiState(
            conversationId = conversationId,
            currentUserId = container.authRepository.getCurrentUserId()
        )
    )
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        loadConversationDetails()
        loadMessages()
        observeRealtimeMessages()
        observeAudioStates()
        observeTypingStatus()
        markAsRead()
    }

    private fun loadConversationDetails() {
        viewModelScope.launch {
            when (val convsRes = container.chatRepository.getConversations()) {
                is Resource.Success -> {
                    val conv = convsRes.data.find { it.id == conversationId }
                    _uiState.value = _uiState.value.copy(conversation = conv)
                    conv?.directUser?.let { other ->
                        checkBlockStatus(other.id)
                    }
                }
                else -> Unit
            }
        }
    }

    private fun checkBlockStatus(otherUserId: String) {
        viewModelScope.launch {
            when (val blocked = container.blockRepository.getBlockedUserIds()) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(isBlocked = blocked.data.contains(otherUserId))
                }
                else -> Unit
            }
        }
    }

    fun loadMessages() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            when (val res = container.messageRepository.getMessages(conversationId)) {
                is Resource.Success -> {
                    val visible = res.data.filter { !it.deletedForMe }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        messages = visible
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = res.message
                    )
                }
                else -> Unit
            }
        }
    }

    private fun observeRealtimeMessages() {
        viewModelScope.launch {
            container.messageRepository.observeRealtimeMessages().collect { change ->
                val record = change.record ?: return@collect
                val cId = record.optString("conversation_id")
                if (cId == conversationId) {
                    when (change.eventType) {
                        "INSERT" -> {
                            val newMsg = Message.fromJson(record)
                            val current = _uiState.value.messages.toMutableList()
                            if (current.none { it.id == newMsg.id }) {
                                current.add(newMsg)
                                _uiState.value = _uiState.value.copy(messages = current)
                                markAsRead()
                            }
                        }
                        "UPDATE" -> {
                            val updatedMsg = Message.fromJson(record)
                            val current = _uiState.value.messages.map {
                                if (it.id == updatedMsg.id) updatedMsg else it
                            }
                            _uiState.value = _uiState.value.copy(messages = current)
                        }
                        "DELETE" -> {
                            val delId = change.oldRecord?.optString("id") ?: record.optString("id")
                            val current = _uiState.value.messages.filter { it.id != delId }
                            _uiState.value = _uiState.value.copy(messages = current)
                        }
                    }
                }
            }
        }
    }

    private fun observeAudioStates() {
        viewModelScope.launch {
            container.audioRecorder.isRecording.collect { isRec ->
                _uiState.value = _uiState.value.copy(isRecordingAudio = isRec)
            }
        }
        viewModelScope.launch {
            container.audioRecorder.recordingDurationSeconds.collect { dur ->
                _uiState.value = _uiState.value.copy(recordingDuration = dur)
            }
        }
        viewModelScope.launch {
            container.audioPlayer.currentPlayingUrl.collect { url ->
                _uiState.value = _uiState.value.copy(playingAudioUrl = url)
            }
        }
        viewModelScope.launch {
            container.audioPlayer.isPlaying.collect { isPlay ->
                _uiState.value = _uiState.value.copy(isAudioPlaying = isPlay)
            }
        }
        viewModelScope.launch {
            container.audioPlayer.playbackProgress.collect { prog ->
                _uiState.value = _uiState.value.copy(audioProgress = prog)
            }
        }
    }

    private fun observeTypingStatus() {
        viewModelScope.launch {
            container.messageRepository.getTypingStatus().collect { statusMap ->
                val isTyping = statusMap[conversationId] == true
                _uiState.value = _uiState.value.copy(isOtherTyping = isTyping)
            }
        }
    }

    fun markAsRead() {
        viewModelScope.launch {
            container.messageRepository.markAsRead(conversationId)
        }
    }

    fun getSavedDraft(): String {
        return container.chatRepository.getDraft(conversationId)
    }

    fun saveDraft(text: String) {
        container.chatRepository.saveDraft(conversationId, text)
    }

    fun sendTextMessage(content: String) {
        val trimmed = content.trim()
        if (trimmed.isBlank()) return

        val replyId = _uiState.value.replyingToMessage?.id
        _uiState.value = _uiState.value.copy(replyingToMessage = null)
        saveDraft("")

        viewModelScope.launch {
            when (val res = container.messageRepository.sendMessage(
                conversationId = conversationId,
                content = trimmed,
                messageType = MessageType.TEXT,
                replyToMessageId = replyId
            )) {
                is Resource.Success -> {
                    val current = _uiState.value.messages.toMutableList()
                    if (current.none { it.id == res.data.id }) {
                        current.add(res.data)
                        _uiState.value = _uiState.value.copy(messages = current)
                    }
                    // Trigger friendly auto-reply in direct chats
                    container.messageRepository.triggerAutoReply(conversationId)
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(error = res.message)
                }
                else -> Unit
            }
        }
    }

    fun startVoiceRecording() {
        container.audioRecorder.startRecording()
    }

    fun stopAndSendVoiceRecording() {
        val file = container.audioRecorder.stopRecording() ?: return
        if (file.exists() && file.length() > 0) {
            viewModelScope.launch {
                val bytes = file.readBytes()
                when (val uploadRes = container.storageRepository.uploadMedia("voice-messages", file.name, bytes, "audio/m4a", conversationId)) {
                    is Resource.Success -> {
                        container.messageRepository.sendMessage(
                            conversationId = conversationId,
                            content = "رسالة صوتية",
                            messageType = MessageType.AUDIO,
                            attachmentUrl = uploadRes.data,
                            attachmentName = file.name,
                            attachmentSize = file.length()
                        )
                        loadMessages()
                    }
                    else -> Unit
                }
                file.delete()
            }
        }
    }

    fun cancelVoiceRecording() {
        container.audioRecorder.cancelRecording()
    }

    fun sendImage(uri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                val inputStream = context.contentResolver.openInputStream(uri) ?: return@launch
                val bytes = inputStream.readBytes()
                inputStream.close()
                val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
                val name = "image_${System.currentTimeMillis()}.jpg"

                when (val uploadRes = container.storageRepository.uploadMedia("chat-media", name, bytes, mimeType, conversationId)) {
                    is Resource.Success -> {
                        container.messageRepository.sendMessage(
                            conversationId = conversationId,
                            content = "صورة",
                            messageType = MessageType.IMAGE,
                            attachmentUrl = uploadRes.data,
                            attachmentName = name,
                            attachmentSize = bytes.size.toLong()
                        )
                        loadMessages()
                    }
                    else -> Unit
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "فشل إرسال الصورة: ${e.message}")
            }
        }
    }

    fun sendFile(uri: Uri, fileName: String, context: Context) {
        viewModelScope.launch {
            try {
                val inputStream = context.contentResolver.openInputStream(uri) ?: return@launch
                val bytes = inputStream.readBytes()
                inputStream.close()
                val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"

                when (val uploadRes = container.storageRepository.uploadMedia("attachments", fileName, bytes, mimeType, conversationId)) {
                    is Resource.Success -> {
                        container.messageRepository.sendMessage(
                            conversationId = conversationId,
                            content = fileName,
                            messageType = MessageType.FILE,
                            attachmentUrl = uploadRes.data,
                            attachmentName = fileName,
                            attachmentSize = bytes.size.toLong()
                        )
                        loadMessages()
                    }
                    else -> Unit
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "فشل إرسال الملف: ${e.message}")
            }
        }
    }

    fun toggleAudioPlayback(url: String) {
        container.audioPlayer.playAudio(url)
    }

    fun toggleReaction(message: Message, emoji: String) {
        val currentUserId = container.authRepository.getCurrentUserId() ?: return
        val currentUserName = container.demoDataManager.currentUserProfile.displayName
        viewModelScope.launch {
            container.messageRepository.toggleReaction(message.id, emoji, currentUserId, currentUserName)
            loadMessages()
        }
    }

    fun togglePinMessage(message: Message) {
        viewModelScope.launch {
            container.messageRepository.togglePinMessage(conversationId, message.id)
            loadMessages()
            loadConversationDetails()
        }
    }

    fun setReplyMessage(message: Message?) {
        _uiState.value = _uiState.value.copy(replyingToMessage = message, editingMessage = null)
    }

    fun setEditMessage(message: Message?) {
        _uiState.value = _uiState.value.copy(editingMessage = message, replyingToMessage = null)
    }

    fun updateEditedMessage(newContent: String) {
        val msg = _uiState.value.editingMessage ?: return
        viewModelScope.launch {
            when (container.messageRepository.editMessage(msg.id, newContent)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(editingMessage = null)
                    loadMessages()
                }
                else -> Unit
            }
        }
    }

    fun deleteMessage(message: Message, deleteForEveryone: Boolean) {
        viewModelScope.launch {
            when (container.messageRepository.deleteMessage(message.id, deleteForEveryone)) {
                is Resource.Success -> loadMessages()
                else -> Unit
            }
        }
    }

    fun startForwardFlow(message: Message) {
        _uiState.value = _uiState.value.copy(showForwardDialog = true, messageToForward = message)
    }

    fun dismissForwardDialog() {
        _uiState.value = _uiState.value.copy(showForwardDialog = false, messageToForward = null)
    }

    fun forwardToConversations(targetConversationIds: List<String>) {
        val msg = _uiState.value.messageToForward ?: return
        viewModelScope.launch {
            container.messageRepository.forwardMessage(targetConversationIds, msg)
            dismissForwardDialog()
            loadMessages()
        }
    }

    // Multi-Select
    fun toggleSelectMessage(messageId: String) {
        val current = _uiState.value.selectedMessageIds.toMutableSet()
        if (current.contains(messageId)) {
            current.remove(messageId)
        } else {
            current.add(messageId)
        }
        _uiState.value = _uiState.value.copy(
            selectedMessageIds = current,
            isMultiSelectMode = current.isNotEmpty()
        )
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(
            isMultiSelectMode = false,
            selectedMessageIds = emptySet()
        )
    }

    fun deleteSelectedMessages() {
        viewModelScope.launch {
            _uiState.value.selectedMessageIds.forEach { id ->
                val msg = _uiState.value.messages.find { it.id == id }
                if (msg != null) {
                    container.messageRepository.deleteMessage(msg.id, false)
                }
            }
            clearSelection()
            loadMessages()
        }
    }

    // In-Chat Search
    fun toggleSearch() {
        val next = !_uiState.value.isSearchOpen
        _uiState.value = _uiState.value.copy(
            isSearchOpen = next,
            inChatSearchQuery = if (!next) "" else _uiState.value.inChatSearchQuery,
            matchedMessageIds = if (!next) emptyList() else _uiState.value.matchedMessageIds
        )
    }

    fun updateSearchQuery(query: String) {
        val matches = if (query.isBlank()) {
            emptyList()
        } else {
            val q = query.trim().lowercase()
            _uiState.value.messages.filter { it.content.lowercase().contains(q) }.map { it.id }
        }
        _uiState.value = _uiState.value.copy(
            inChatSearchQuery = query,
            matchedMessageIds = matches,
            currentSearchMatchIndex = if (matches.isNotEmpty()) 0 else -1
        )
    }

    fun nextSearchMatch() {
        val total = _uiState.value.matchedMessageIds.size
        if (total == 0) return
        val current = _uiState.value.currentSearchMatchIndex
        val next = (current + 1) % total
        _uiState.value = _uiState.value.copy(currentSearchMatchIndex = next)
    }

    fun prevSearchMatch() {
        val total = _uiState.value.matchedMessageIds.size
        if (total == 0) return
        val current = _uiState.value.currentSearchMatchIndex
        val prev = if (current <= 0) total - 1 else current - 1
        _uiState.value = _uiState.value.copy(currentSearchMatchIndex = prev)
    }

    fun toggleBlock() {
        val direct = _uiState.value.conversation?.directUser ?: return
        val currentlyBlocked = _uiState.value.isBlocked
        viewModelScope.launch {
            if (currentlyBlocked) {
                container.blockRepository.unblockUser(direct.id)
                _uiState.value = _uiState.value.copy(isBlocked = false)
            } else {
                container.blockRepository.blockUser(direct.id)
                _uiState.value = _uiState.value.copy(isBlocked = true)
            }
        }
    }

    fun togglePin() {
        val conv = _uiState.value.conversation ?: return
        viewModelScope.launch {
            when (val res = container.chatRepository.togglePin(conv.id, conv.isPinned)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(conversation = conv.copy(isPinned = res.data))
                }
                else -> Unit
            }
        }
    }

    fun toggleMute() {
        val conv = _uiState.value.conversation ?: return
        viewModelScope.launch {
            when (val res = container.chatRepository.toggleMute(conv.id, conv.isMuted)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(conversation = conv.copy(isMuted = res.data))
                }
                else -> Unit
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        container.audioPlayer.stop()
    }
}
