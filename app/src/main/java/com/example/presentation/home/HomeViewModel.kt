package com.example.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.common.Resource
import com.example.core.di.AppContainer
import com.example.data.models.Conversation
import com.example.data.models.ConversationType
import com.example.data.models.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class HomeFilter {
    ALL,
    UNREAD,
    GROUPS,
    PINNED
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val conversations: List<Conversation> = emptyList(),
    val currentUserProfile: UserProfile? = null,
    val currentFilter: HomeFilter = HomeFilter.ALL,
    val isOffline: Boolean = false,
    val error: String? = null
)

class HomeViewModel(private val container: AppContainer) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
        observeRealtimeChanges()
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            container.settingsManager.settings.collect { settings ->
                _uiState.value = _uiState.value.copy(isOffline = settings.isSimulatedOffline)
            }
        }
    }

    fun loadData() {
        val currentUserId = container.authRepository.getCurrentUserId() ?: return
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            when (val pRes = container.profileRepository.getProfile(currentUserId)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(currentUserProfile = pRes.data)
                }
                else -> Unit
            }

            loadConversationsQuietly()
        }
    }

    private fun observeRealtimeChanges() {
        viewModelScope.launch {
            container.messageRepository.observeRealtimeMessages().collect {
                loadConversationsQuietly()
            }
        }
    }

    fun loadConversationsQuietly() {
        viewModelScope.launch {
            when (val cRes = container.chatRepository.getConversations()) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        conversations = cRes.data
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = cRes.message
                    )
                }
                else -> Unit
            }
        }
    }

    fun setFilter(filter: HomeFilter) {
        _uiState.value = _uiState.value.copy(currentFilter = filter)
    }

    fun togglePin(conversation: Conversation) {
        viewModelScope.launch {
            when (container.chatRepository.togglePin(conversation.id, conversation.isPinned)) {
                is Resource.Success -> loadConversationsQuietly()
                else -> Unit
            }
        }
    }

    fun toggleMute(conversation: Conversation) {
        viewModelScope.launch {
            when (container.chatRepository.toggleMute(conversation.id, conversation.isMuted)) {
                is Resource.Success -> loadConversationsQuietly()
                else -> Unit
            }
        }
    }

    fun toggleArchive(conversation: Conversation) {
        viewModelScope.launch {
            when (container.chatRepository.toggleArchive(conversation.id, conversation.isArchived)) {
                is Resource.Success -> loadConversationsQuietly()
                else -> Unit
            }
        }
    }

    fun deleteConversation(conversation: Conversation) {
        viewModelScope.launch {
            when (container.chatRepository.deleteConversation(conversation.id)) {
                is Resource.Success -> loadConversationsQuietly()
                else -> Unit
            }
        }
    }

    fun markAsRead(conversation: Conversation) {
        viewModelScope.launch {
            container.messageRepository.markAsRead(conversation.id)
            loadConversationsQuietly()
        }
    }

    fun markAsUnread(conversation: Conversation) {
        viewModelScope.launch {
            val idx = container.demoDataManager.conversations.indexOfFirst { it.id == conversation.id }
            if (idx != -1) {
                container.demoDataManager.conversations[idx] = container.demoDataManager.conversations[idx].copy(unreadCount = 1)
            }
            loadConversationsQuietly()
        }
    }

    fun addContact(name: String, username: String, phone: String) {
        viewModelScope.launch {
            container.chatRepository.addContact(name, username, phone)
        }
    }
}
