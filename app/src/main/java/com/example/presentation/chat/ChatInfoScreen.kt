package com.example.presentation.chat

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.core.di.AppContainer
import com.example.data.models.ConversationType
import com.example.data.models.MessageType
import com.example.presentation.common.ImageViewerDialog
import com.example.presentation.common.RaseelAvatar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInfoScreen(
    conversationId: String,
    container: AppContainer,
    onNavigateBack: () -> Unit,
    onOpenMedia: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var conversation by remember {
        mutableStateOf(container.demoDataManager.conversations.find { it.id == conversationId })
    }
    val messages = remember {
        container.demoDataManager.conversationMessages[conversationId] ?: emptyList()
    }

    val mediaMessages = remember(messages) {
        messages.filter { it.messageType == MessageType.IMAGE && !it.attachmentUrl.isNullOrBlank() }
    }
    val fileMessages = remember(messages) {
        messages.filter { it.messageType == MessageType.FILE }
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    var showClearDialog by remember { mutableStateOf(false) }
    var previewImageUrl by remember { mutableStateOf<String?>(null) }

    val isGroup = conversation?.type == ConversationType.GROUP
    val directUser = conversation?.directUser

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isGroup) "معلومات المجموعة" else "معلومات المحادثة", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Profile Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RaseelAvatar(
                    name = conversation?.title ?: "Chat",
                    avatarUrl = conversation?.avatarUrl,
                    isGroup = isGroup,
                    isOnline = directUser?.isOnline == true,
                    size = 96.dp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = conversation?.title ?: "",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isGroup) "مجموعة • ${container.demoDataManager.conversationMembers[conversationId]?.size ?: 0} أعضاء"
                    else if (directUser?.isOnline == true) "متصل الآن" else "غير متصل",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (directUser?.isOnline == true) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (!directUser?.bio.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = directUser?.bio.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Action Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    ChatActionChip(
                        icon = if (conversation?.isMuted == true) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                        label = if (conversation?.isMuted == true) "إلغاء الكتم" else "كتم",
                        onClick = {
                            conversation?.let { conv ->
                                coroutineScope.launch {
                                    val res = container.chatRepository.toggleMute(conv.id, conv.isMuted)
                                    conversation = conv.copy(isMuted = !conv.isMuted)
                                }
                            }
                        }
                    )

                    ChatActionChip(
                        icon = Icons.Default.PushPin,
                        label = if (conversation?.isPinned == true) "إلغاء التثبيت" else "تثبيت",
                        onClick = {
                            conversation?.let { conv ->
                                coroutineScope.launch {
                                    container.chatRepository.togglePin(conv.id, conv.isPinned)
                                    conversation = conv.copy(isPinned = !conv.isPinned)
                                }
                            }
                        }
                    )

                    ChatActionChip(
                        icon = Icons.Default.DeleteSweep,
                        label = "مسح المحادثة",
                        onClick = { showClearDialog = true }
                    )
                }
            }

            HorizontalDivider()

            // Media & Files Tabs
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("الوسائط (${mediaMessages.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("الملفات (${fileMessages.size})") }
                )
            }

            when (selectedTab) {
                0 -> {
                    if (mediaMessages.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("لا توجد صور مشتركة بعد", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            gridItems(mediaMessages) { msg ->
                                AsyncImage(
                                    model = msg.attachmentUrl,
                                    contentDescription = "Media",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { previewImageUrl = msg.attachmentUrl }
                                )
                            }
                        }
                    }
                }

                1 -> {
                    if (fileMessages.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("لا توجد ملفات مشتركة بعد", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(fileMessages) { fileMsg ->
                                ListItem(
                                    headlineContent = { Text(fileMsg.attachmentName ?: "مستند", fontWeight = FontWeight.SemiBold) },
                                    supportingContent = {
                                        val kb = (fileMsg.attachmentSize ?: 0) / 1024
                                        Text("$kb KB • ${fileMsg.createdAt.substringBefore("T")}")
                                    },
                                    leadingContent = {
                                        Icon(
                                            Icons.Default.InsertDriveFile,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    },
                                    trailingContent = {
                                        IconButton(onClick = {
                                            Toast.makeText(context, "جارٍ فتح الملف: ${fileMsg.attachmentName}", Toast.LENGTH_SHORT).show()
                                        }) {
                                            Icon(Icons.Default.FileOpen, contentDescription = "Open")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("مسح سجل المحادثة") },
            text = { Text("هل أنت متأكد من مسح جميع الرسائل في هذه المحادثة؟ لا يمكن التراجع عن هذا الإجراء.") },
            confirmButton = {
                Button(
                    onClick = {
                        container.demoDataManager.conversationMessages[conversationId]?.clear()
                        showClearDialog = false
                        Toast.makeText(context, "تم مسح سجل المحادثة", Toast.LENGTH_SHORT).show()
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("مسح")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Full screen image preview dialog
    previewImageUrl?.let { url ->
        ImageViewerDialog(
            imageUrl = url,
            title = conversation?.title ?: "صورة مشتركة",
            onDismissRequest = { previewImageUrl = null }
        )
    }
}

@Composable
private fun ChatActionChip(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}
