package com.example.presentation.chat

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.core.common.DateUtils
import com.example.data.models.*
import com.example.presentation.common.*
import com.example.ui.theme.LocalBubbleRadius
import com.example.ui.theme.LocalChatWallpaper
import com.example.ui.theme.ReadReceiptBlue
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToGroupInfo: (String) -> Unit,
    onNavigateToChatInfo: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val bubbleRadius = LocalBubbleRadius.current
    val wallpaper = LocalChatWallpaper.current

    var inputText by remember { mutableStateOf("") }
    var selectedMessageForMenu by remember { mutableStateOf<Message?>(null) }
    var showActionSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAttachmentSheet by remember { mutableStateOf(false) }
    var showEmojiSheet by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var previewImageUrl by remember { mutableStateOf<String?>(null) }

    // Restore draft message if any
    LaunchedEffect(uiState.conversationId) {
        val draft = viewModel.getSavedDraft()
        if (draft.isNotBlank()) {
            inputText = draft
        }
    }

    // Scroll to bottom on new messages
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    // Populate input when entering edit mode
    LaunchedEffect(uiState.editingMessage) {
        uiState.editingMessage?.let {
            inputText = it.content
        }
    }

    // Scroll to search match
    LaunchedEffect(uiState.currentSearchMatchIndex, uiState.matchedMessageIds) {
        if (uiState.matchedMessageIds.isNotEmpty() && uiState.currentSearchMatchIndex in uiState.matchedMessageIds.indices) {
            val targetId = uiState.matchedMessageIds[uiState.currentSearchMatchIndex]
            val idx = uiState.messages.indexOfFirst { it.id == targetId }
            if (idx != -1) {
                listState.animateScrollToItem(idx)
            }
        }
    }

    // Back handler management
    BackHandler {
        when {
            uiState.isSearchOpen -> viewModel.toggleSearch()
            uiState.isMultiSelectMode -> viewModel.clearSelection()
            showEmojiSheet -> showEmojiSheet = false
            showAttachmentSheet -> showAttachmentSheet = false
            else -> {
                // Save draft on exit
                viewModel.saveDraft(inputText)
                onNavigateBack()
            }
        }
    }

    // Media Pickers
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { viewModel.sendImage(it, context) }
    }

    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            var fileName = "مستند"
            context.contentResolver.query(it, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    fileName = cursor.getString(nameIndex)
                }
            }
            viewModel.sendFile(it, fileName, context)
        }
    }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startVoiceRecording()
        } else {
            Toast.makeText(context, "يلزم منح إذن الميكروفون لتسجيل الملاحظات الصوتية", Toast.LENGTH_SHORT).show()
        }
    }

    val isGroup = uiState.conversation?.type == ConversationType.GROUP
    val subtitle = if (isGroup) {
        "انقر لعرض معلومات المجموعة"
    } else {
        val user = uiState.conversation?.directUser
        when {
            user?.isOnline == true -> stringResource(R.string.status_online)
            !user?.lastSeen.isNullOrBlank() -> "${stringResource(R.string.status_last_seen)} ${DateUtils.formatRelativeTime(user?.lastSeen)}"
            else -> stringResource(R.string.status_last_seen_recently)
        }
    }

    // Pinned message
    val pinnedMessage = remember(uiState.messages, uiState.conversation?.pinnedMessageId) {
        uiState.messages.find { it.isPinned || it.id == uiState.conversation?.pinnedMessageId }
    }

    Scaffold(
        topBar = {
            if (uiState.isMultiSelectMode) {
                // Multi-Select Top Bar
                TopAppBar(
                    title = { Text("تم تحديد ${uiState.selectedMessageIds.size}") },
                    navigationIcon = {
                        IconButton(onClick = viewModel::clearSelection) {
                            Icon(Icons.Default.Close, contentDescription = "Close selection")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            val selectedMsgs = uiState.messages.filter { uiState.selectedMessageIds.contains(it.id) }
                            val textToCopy = selectedMsgs.joinToString("\n") { it.content }
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("selected_messages", textToCopy))
                            Toast.makeText(context, "تم نسخ الرسائل المحددة", Toast.LENGTH_SHORT).show()
                            viewModel.clearSelection()
                        }) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                        }

                        IconButton(onClick = {
                            val firstMsg = uiState.messages.find { uiState.selectedMessageIds.contains(it.id) }
                            if (firstMsg != null) {
                                viewModel.startForwardFlow(firstMsg)
                            }
                        }) {
                            Icon(Icons.Default.Forward, contentDescription = "Forward")
                        }

                        IconButton(onClick = viewModel::deleteSelectedMessages) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                )
            } else if (uiState.isSearchOpen) {
                // In-Chat Search Top Bar
                TopAppBar(
                    title = {
                        TextField(
                            value = uiState.inChatSearchQuery,
                            onValueChange = viewModel::updateSearchQuery,
                            placeholder = { Text("بحث في الرسائل...") },
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = viewModel::toggleSearch) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close search")
                        }
                    },
                    actions = {
                        if (uiState.matchedMessageIds.isNotEmpty()) {
                            Text(
                                text = "${uiState.currentSearchMatchIndex + 1} من ${uiState.matchedMessageIds.size}",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                            IconButton(onClick = viewModel::prevSearchMatch) {
                                Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Previous match")
                            }
                            IconButton(onClick = viewModel::nextSearchMatch) {
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Next match")
                            }
                        }
                    }
                )
            } else {
                // Standard Chat Top Bar
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (isGroup) onNavigateToGroupInfo(uiState.conversationId)
                                    else onNavigateToChatInfo(uiState.conversationId)
                                }
                        ) {
                            RaseelAvatar(
                                name = uiState.conversation?.title ?: "Chat",
                                avatarUrl = uiState.conversation?.avatarUrl,
                                isGroup = isGroup,
                                isOnline = uiState.conversation?.directUser?.isOnline == true,
                                size = 42.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = uiState.conversation?.title ?: "Chat",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (uiState.conversation?.directUser?.isOnline == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            viewModel.saveDraft(inputText)
                            onNavigateBack()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = viewModel::toggleSearch) {
                            Icon(Icons.Default.Search, contentDescription = "Search in chat")
                        }

                        Box {
                            IconButton(onClick = { showMoreMenu = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More options")
                            }
                            DropdownMenu(
                                expanded = showMoreMenu,
                                onDismissRequest = { showMoreMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(if (isGroup) "معلومات المجموعة" else "معلومات المحادثة") },
                                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                                    onClick = {
                                        showMoreMenu = false
                                        if (isGroup) onNavigateToGroupInfo(uiState.conversationId)
                                        else onNavigateToChatInfo(uiState.conversationId)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(if (uiState.conversation?.isMuted == true) "إلغاء كتم الإشعارات" else "كتم الإشعارات") },
                                    leadingIcon = {
                                        Icon(
                                            if (uiState.conversation?.isMuted == true) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                                            contentDescription = null
                                        )
                                    },
                                    onClick = {
                                        showMoreMenu = false
                                        viewModel.toggleMute()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(if (uiState.conversation?.isPinned == true) "إلغاء تثبيت المحادثة" else "تثبيت المحادثة") },
                                    leadingIcon = { Icon(Icons.Default.PushPin, contentDescription = null) },
                                    onClick = {
                                        showMoreMenu = false
                                        viewModel.togglePin()
                                    }
                                )
                                if (!isGroup) {
                                    DropdownMenuItem(
                                        text = { Text(if (uiState.isBlocked) "إلغاء حظر المستخدم" else "حظر المستخدم", color = MaterialTheme.colorScheme.error) },
                                        leadingIcon = { Icon(Icons.Default.Block, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                                        onClick = {
                                            showMoreMenu = false
                                            viewModel.toggleBlock()
                                        }
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(android.graphics.Color.parseColor(wallpaper.backgroundHex)))
                .imePadding()
        ) {
            // Pinned Message Banner
            if (pinnedMessage != null) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val idx = uiState.messages.indexOfFirst { it.id == pinnedMessage.id }
                            if (idx != -1) {
                                coroutineScope.launch { listState.animateScrollToItem(idx) }
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "رسالة مثبتة",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = pinnedMessage.content.ifBlank { "مرفق وسائط" },
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        IconButton(
                            onClick = { viewModel.togglePinMessage(pinnedMessage) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Unpin", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Messages LazyColumn
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(uiState.messages, key = { _, it -> it.id }) { index, message ->
                        val isMine = message.senderId == uiState.currentUserId
                        val isHighlighted = uiState.matchedMessageIds.contains(message.id)
                        val isSelected = uiState.selectedMessageIds.contains(message.id)

                        // Date Separator if first message or day changed
                        if (index == 0 || shouldShowDateSeparator(uiState.messages[index - 1], message)) {
                            DateSeparator(dateStr = message.createdAt)
                        }

                        MessageBubble(
                            message = message,
                            isMine = isMine,
                            isHighlighted = isHighlighted,
                            isSelected = isSelected,
                            isAudioPlaying = uiState.isAudioPlaying && uiState.playingAudioUrl == message.attachmentUrl,
                            audioProgress = if (uiState.playingAudioUrl == message.attachmentUrl) uiState.audioProgress else 0f,
                            bubbleRadius = bubbleRadius,
                            onPlayAudio = { url -> viewModel.toggleAudioPlayback(url) },
                            onImageClick = { url -> previewImageUrl = url },
                            onReactionClick = { emoji -> viewModel.toggleReaction(message, emoji) },
                            onLongClick = {
                                if (uiState.isMultiSelectMode) {
                                    viewModel.toggleSelectMessage(message.id)
                                } else {
                                    selectedMessageForMenu = message
                                    showActionSheet = true
                                }
                            },
                            onClick = {
                                if (uiState.isMultiSelectMode) {
                                    viewModel.toggleSelectMessage(message.id)
                                }
                            }
                        )
                    }
                }

                // Typing indicator docked at bottom
                if (uiState.isOtherTyping) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 16.dp, bottom = 8.dp)
                    ) {
                        TypingIndicator(
                            senderName = uiState.conversation?.title ?: ""
                        )
                    }
                }

                if (uiState.isBlocked) {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.95f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(8.dp),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "تم حظر هذا المستخدم. لا يمكنك إرسال أو استقبال الرسائل معه.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

            // Reply or Edit Mode Banner
            AnimatedVisibility(visible = uiState.replyingToMessage != null || uiState.editingMessage != null) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (uiState.editingMessage != null) Icons.Default.Edit else Icons.Default.Reply,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (uiState.editingMessage != null) "تعديل الرسالة" else "الرد على ${uiState.replyingToMessage?.senderProfile?.displayName ?: "الرسالة"}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = uiState.editingMessage?.content ?: uiState.replyingToMessage?.content.orEmpty(),
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        IconButton(
                            onClick = {
                                viewModel.setReplyMessage(null)
                                viewModel.setEditMessage(null)
                                inputText = ""
                            }
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Cancel")
                        }
                    }
                }
            }

            // Input / Composer Section
            if (uiState.isRecordingAudio) {
                // Recording Audio Bar
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .navigationBarsPadding(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = viewModel::cancelVoiceRecording,
                            modifier = Modifier.testTag("btn_cancel_voice")
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Cancel recording",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(Color.Red)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        val minutes = uiState.recordingDuration / 60
                        val seconds = uiState.recordingDuration % 60
                        Text(
                            text = String.format("%02d:%02d", minutes, seconds),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

                        FloatingActionButton(
                            onClick = viewModel::stopAndSendVoiceRecording,
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("btn_send_voice")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send Voice Note")
                        }
                    }
                }
            } else {
                // Normal Text Composer Bar
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .navigationBarsPadding(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Emoji Picker Button
                        IconButton(
                            onClick = { showEmojiSheet = true },
                            modifier = Modifier.testTag("btn_emoji_picker")
                        ) {
                            Text("😀", fontSize = 22.sp)
                        }

                        // Attachment Button
                        IconButton(
                            onClick = { showAttachmentSheet = true },
                            modifier = Modifier.testTag("btn_attachment_sheet")
                        ) {
                            Icon(
                                Icons.Default.AttachFile,
                                contentDescription = "Attach file",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Auto-growing text field
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = {
                                inputText = it
                                viewModel.saveDraft(it)
                            },
                            placeholder = { Text("اكتب رسالتك...") },
                            maxLines = 5,
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                                .testTag("input_chat_message")
                        )

                        // Send vs Mic dynamic button
                        if (inputText.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    if (uiState.editingMessage != null) {
                                        viewModel.updateEditedMessage(inputText)
                                        inputText = ""
                                    } else {
                                        viewModel.sendTextMessage(inputText)
                                        inputText = ""
                                    }
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                                    .testTag("btn_send_message")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .testTag("btn_record_voice")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Record voice note",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Attachment Picker Modal Bottom Sheet
    if (showAttachmentSheet) {
        AttachmentPickerSheet(
            onPickGallery = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            onPickCamera = {
                Toast.makeText(context, "التقاط صورة بالكاميرا", Toast.LENGTH_SHORT).show()
            },
            onPickDocument = {
                documentPickerLauncher.launch(arrayOf("*/*"))
            },
            onPickAudio = {
                Toast.makeText(context, "إرفاق ملف صوتي", Toast.LENGTH_SHORT).show()
            },
            onPickContact = {
                viewModel.sendTextMessage("👤 بطاقة جهة اتصال: أحمد محمد (+966500000000)")
            },
            onPickLocation = {
                viewModel.sendTextMessage("📍 موقع جغرافي: https://maps.google.com/?q=24.7136,46.6753")
            },
            onDismissRequest = { showAttachmentSheet = false }
        )
    }

    // Emoji Picker Modal Bottom Sheet
    if (showEmojiSheet) {
        EmojiPickerSheet(
            onEmojiSelected = { emoji ->
                inputText += emoji
                viewModel.saveDraft(inputText)
            },
            onDismissRequest = { showEmojiSheet = false }
        )
    }

    // Message Actions Sheet with Floating Reactions
    if (showActionSheet && selectedMessageForMenu != null) {
        val msg = selectedMessageForMenu!!
        val isMine = msg.senderId == uiState.currentUserId

        ModalBottomSheet(
            onDismissRequest = { showActionSheet = false },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .navigationBarsPadding()
            ) {
                // Floating Quick Reactions Bar
                ReactionsBar(
                    onSelectReaction = { emoji ->
                        viewModel.toggleReaction(msg, emoji)
                        showActionSheet = false
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp)
                )

                HorizontalDivider()

                ListItem(
                    headlineContent = { Text("رد على الرسالة (Reply)") },
                    leadingContent = { Icon(Icons.Default.Reply, contentDescription = null) },
                    modifier = Modifier.clickable {
                        showActionSheet = false
                        viewModel.setReplyMessage(msg)
                    }
                )

                if (msg.messageType == MessageType.TEXT && !msg.isDeletedForEveryone) {
                    ListItem(
                        headlineContent = { Text("نسخ النص") },
                        leadingContent = { Icon(Icons.Default.ContentCopy, contentDescription = null) },
                        modifier = Modifier.clickable {
                            showActionSheet = false
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("message", msg.content))
                            Toast.makeText(context, "تم نسخ الرسالة", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                ListItem(
                    headlineContent = { Text("إعادة توجيه (Forward)") },
                    leadingContent = { Icon(Icons.Default.Forward, contentDescription = null) },
                    modifier = Modifier.clickable {
                        showActionSheet = false
                        viewModel.startForwardFlow(msg)
                    }
                )

                ListItem(
                    headlineContent = { Text(if (msg.isPinned) "إلغاء التثبيت" else "تثبيت الرسالة") },
                    leadingContent = { Icon(Icons.Default.PushPin, contentDescription = null) },
                    modifier = Modifier.clickable {
                        showActionSheet = false
                        viewModel.togglePinMessage(msg)
                    }
                )

                ListItem(
                    headlineContent = { Text("تحديد (Select)") },
                    leadingContent = { Icon(Icons.Default.CheckCircleOutline, contentDescription = null) },
                    modifier = Modifier.clickable {
                        showActionSheet = false
                        viewModel.toggleSelectMessage(msg.id)
                    }
                )

                if (isMine && msg.messageType == MessageType.TEXT && !msg.isDeletedForEveryone) {
                    ListItem(
                        headlineContent = { Text("تعديل الرسالة") },
                        leadingContent = { Icon(Icons.Default.Edit, contentDescription = null) },
                        modifier = Modifier.clickable {
                            showActionSheet = false
                            viewModel.setEditMessage(msg)
                        }
                    )
                }

                if (!msg.isDeletedForEveryone) {
                    ListItem(
                        headlineContent = {
                            Text("حذف الرسالة", color = MaterialTheme.colorScheme.error)
                        },
                        leadingContent = {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        },
                        modifier = Modifier.clickable {
                            showActionSheet = false
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }

    // Delete message dialog
    if (showDeleteDialog && selectedMessageForMenu != null) {
        val msg = selectedMessageForMenu!!
        val isMine = msg.senderId == uiState.currentUserId
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("حذف الرسالة") },
            text = { Text("هل تريد حذف هذه الرسالة؟") },
            confirmButton = {
                if (isMine) {
                    TextButton(
                        onClick = {
                            viewModel.deleteMessage(msg, deleteForEveryone = true)
                            showDeleteDialog = false
                        }
                    ) {
                        Text("حذف لدى الجميع")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteMessage(msg, deleteForEveryone = false)
                        showDeleteDialog = false
                    }
                ) {
                    Text("حذف لدي فقط")
                }
            }
        )
    }

    // Forward Dialog
    if (uiState.showForwardDialog && uiState.messageToForward != null) {
        val targetConversations = remember {
            com.example.RaseelApplication().let {
                // Use existing conversations from container
                uiState.conversationId
            }
        }
        AlertDialog(
            onDismissRequest = viewModel::dismissForwardDialog,
            title = { Text("إعادة توجيه الرسالة") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("اختر المحادثة المراد إرسال الرسالة إليها:")
                    Text(
                        text = "نص الرسالة: \"${uiState.messageToForward?.content ?: "مرفق وسائط"}\"",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.forwardToConversations(listOf(uiState.conversationId))
                        Toast.makeText(context, "تمت إعادة توجيه الرسالة بنجاح", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("إرسال إلى هذه المحادثة")
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissForwardDialog) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Full screen image viewer dialog
    previewImageUrl?.let { url ->
        ImageViewerDialog(
            imageUrl = url,
            title = uiState.conversation?.title ?: "صورة",
            onDismissRequest = { previewImageUrl = null }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageBubble(
    message: Message,
    isMine: Boolean,
    isHighlighted: Boolean,
    isSelected: Boolean,
    isAudioPlaying: Boolean,
    audioProgress: Float,
    bubbleRadius: androidx.compose.ui.unit.Dp,
    onPlayAudio: (String) -> Unit,
    onImageClick: (String) -> Unit,
    onReactionClick: (String) -> Unit,
    onLongClick: () -> Unit,
    onClick: () -> Unit
) {
    val bubbleColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
        isHighlighted -> Color(0xFFFEF08A)
        isMine -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
        isHighlighted -> Color.Black
        isMine -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val alignment = if (isMine) Alignment.CenterEnd else Alignment.CenterStart
    val shape = RoundedCornerShape(
        topStart = bubbleRadius,
        topEnd = bubbleRadius,
        bottomStart = if (isMine) bubbleRadius else 4.dp,
        bottomEnd = if (isMine) 4.dp else bubbleRadius
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isSelected) Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)) else Modifier),
        contentAlignment = alignment
    ) {
        Column(horizontalAlignment = if (isMine) Alignment.End else Alignment.Start) {
            Surface(
                color = bubbleColor,
                shape = shape,
                tonalElevation = 1.dp,
                modifier = Modifier
                    .widthIn(max = 310.dp)
                    .combinedClickable(
                        onClick = onClick,
                        onLongClick = onLongClick
                    )
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    // Forwarded label
                    if (message.isForwarded) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Forward,
                                contentDescription = null,
                                tint = textColor.copy(alpha = 0.7f),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "رسالة محولة",
                                style = MaterialTheme.typography.labelSmall,
                                fontStyle = FontStyle.Italic,
                                color = textColor.copy(alpha = 0.7f)
                            )
                        }
                    }

                    // Sender name in group chats
                    if (!isMine && message.senderProfile != null) {
                        Text(
                            text = message.senderProfile.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }

                    // Reply Preview Block
                    if (message.replyToMessage != null) {
                        Surface(
                            color = textColor.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp)
                        ) {
                            Row(modifier = Modifier.padding(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(28.dp)
                                        .background(if (isMine) Color.White else MaterialTheme.colorScheme.primary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = message.replyToMessage.senderProfile?.displayName ?: "رسالة سابقة",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = textColor
                                    )
                                    Text(
                                        text = message.replyToMessage.content.ifBlank { "مرفق وسائط" },
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = textColor.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }

                    // Message Content Body
                    if (message.isDeletedForEveryone) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Block,
                                contentDescription = null,
                                tint = textColor.copy(alpha = 0.6f),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "تم حذف هذه الرسالة",
                                style = MaterialTheme.typography.bodyMedium,
                                fontStyle = FontStyle.Italic,
                                color = textColor.copy(alpha = 0.6f)
                            )
                        }
                    } else {
                        when (message.messageType) {
                            MessageType.TEXT -> {
                                Text(
                                    text = message.content,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = textColor
                                )
                            }

                            MessageType.IMAGE -> {
                                Column {
                                    message.attachmentUrl?.let { url ->
                                        AsyncImage(
                                            model = url,
                                            contentDescription = "Image attachment",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .heightIn(max = 240.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable { onImageClick(url) }
                                        )
                                    }
                                    if (message.content.isNotBlank() && message.content != "Photo" && message.content != "صورة") {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = message.content,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = textColor
                                        )
                                    }
                                }
                            }

                            MessageType.AUDIO -> {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    IconButton(
                                        onClick = { message.attachmentUrl?.let { onPlayAudio(it) } }
                                    ) {
                                        Icon(
                                            imageVector = if (isAudioPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = "Play voice note",
                                            tint = textColor
                                        )
                                    }
                                    LinearProgressIndicator(
                                        progress = { audioProgress },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(6.dp)
                                            .clip(CircleShape),
                                        color = if (isMine) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary,
                                        trackColor = textColor.copy(alpha = 0.3f)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("0:28", style = MaterialTheme.typography.labelSmall, color = textColor.copy(alpha = 0.8f))
                                }
                            }

                            MessageType.FILE -> {
                                Surface(
                                    color = textColor.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.InsertDriveFile,
                                            contentDescription = null,
                                            tint = textColor,
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = message.attachmentName ?: "مستند",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold,
                                                color = textColor,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            message.attachmentSize?.let { size ->
                                                val kb = size / 1024
                                                Text(
                                                    text = "$kb KB",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = textColor.copy(alpha = 0.7f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            MessageType.SYSTEM -> Unit
                        }
                    }

                    // Footer: Timestamp, Edited tag, Pinned, Read ticks
                    Row(
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (message.isPinned) {
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = "Pinned",
                                tint = textColor.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .size(12.dp)
                                    .padding(end = 4.dp)
                            )
                        }

                        if (message.isEdited && !message.isDeletedForEveryone) {
                            Text(
                                text = "معدّلة",
                                style = MaterialTheme.typography.labelSmall,
                                color = textColor.copy(alpha = 0.7f),
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }

                        Text(
                            text = DateUtils.formatMessageTime(message.createdAt),
                            style = MaterialTheme.typography.labelSmall,
                            color = textColor.copy(alpha = 0.7f)
                        )

                        if (isMine) {
                            Spacer(modifier = Modifier.width(4.dp))
                            when (message.status) {
                                MessageStatus.SENT -> {
                                    Text(text = "✓", fontSize = 11.sp, color = textColor.copy(alpha = 0.7f))
                                }
                                MessageStatus.DELIVERED -> {
                                    Text(text = "✓✓", fontSize = 11.sp, color = textColor.copy(alpha = 0.7f))
                                }
                                MessageStatus.READ -> {
                                    Text(text = "✓✓", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ReadReceiptBlue)
                                }
                            }
                        }
                    }
                }
            }

            // Reactions Chips below bubble
            if (message.reactions.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .padding(top = 2.dp, start = 4.dp, end = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val grouped = message.reactions.groupBy { it.emoji }
                    grouped.forEach { (emoji, list) ->
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp),
                            tonalElevation = 2.dp,
                            shadowElevation = 1.dp,
                            modifier = Modifier.clickable { onReactionClick(emoji) }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = emoji, fontSize = 13.sp)
                                if (list.size > 1) {
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(text = "${list.size}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DateSeparator(dateStr: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = DateUtils.formatRelativeTime(dateStr),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

private fun shouldShowDateSeparator(prev: Message, current: Message): Boolean {
    val prevDate = prev.createdAt.substringBefore("T")
    val currentDate = current.createdAt.substringBefore("T")
    return prevDate != currentDate
}
