package com.example.presentation.home

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.core.common.DateUtils
import com.example.core.customization.*
import com.example.data.models.Conversation
import com.example.data.models.ConversationType
import com.example.data.models.MessageType
import com.example.presentation.common.OfflineBanner
import com.example.presentation.common.RaseelAvatar
import com.example.presentation.common.RaseelEmptyState
import com.example.presentation.contacts.ContactsScreen
import com.example.presentation.contacts.ContactsViewModel
import com.example.presentation.profile.ProfileViewModel
import com.example.presentation.search.SearchScreen
import com.example.presentation.search.SearchViewModel
import com.example.presentation.settings.SettingsScreen
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    searchViewModel: SearchViewModel,
    profileViewModel: ProfileViewModel,
    contactsViewModel: ContactsViewModel,
    isDarkTheme: Boolean,
    onToggleDarkTheme: (Boolean) -> Unit,
    isArabic: Boolean,
    onToggleLanguage: (Boolean) -> Unit,
    onOpenConversation: (String) -> Unit,
    onNavigateToCreateGroup: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToCustomizationStudio: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToAppearance: () -> Unit,
    onNavigateToStorage: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToBackendConfig: () -> Unit,
    onSignedOut: () -> Unit
) {
    val uiState by homeViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }

    var showFabSheet by remember { mutableStateOf(false) }
    var showAddContactDialog by remember { mutableStateOf(false) }
    var selectedConversationForMenu by remember { mutableStateOf<Conversation?>(null) }
    var showDeleteConvDialog by remember { mutableStateOf(false) }

    var newContactName by remember { mutableStateOf("") }
    var newContactUsername by remember { mutableStateOf("") }
    var newContactPhone by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        homeViewModel.loadData()
    }

    val filteredConversations = remember(uiState.conversations, uiState.currentFilter) {
        val nonArchived = uiState.conversations.filter { !it.isArchived }
        when (uiState.currentFilter) {
            HomeFilter.ALL -> nonArchived
            HomeFilter.UNREAD -> nonArchived.filter { it.unreadCount > 0 }
            HomeFilter.GROUPS -> nonArchived.filter { it.type == ConversationType.GROUP }
            HomeFilter.PINNED -> nonArchived.filter { it.isPinned }
        }
    }

    Scaffold(
        topBar = {
            if (selectedTab == 0) {
                Column {
                    TopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = stringResource(R.string.app_name),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "v2.0",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = onNavigateToCustomizationStudio,
                                modifier = Modifier.testTag("btn_top_customization")
                            ) {
                                Icon(Icons.Default.Palette, contentDescription = "Customize", tint = MaterialTheme.colorScheme.primary)
                            }
                            IconButton(
                                onClick = { selectedTab = 2 },
                                modifier = Modifier.testTag("btn_top_search")
                            ) {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            }
                            IconButton(
                                onClick = { selectedTab = 3 },
                                modifier = Modifier.testTag("btn_top_profile")
                            ) {
                                RaseelAvatar(
                                    name = uiState.currentUserProfile?.displayName ?: "Me",
                                    avatarUrl = uiState.currentUserProfile?.avatarUrl,
                                    size = 34.dp
                                )
                            }
                        }
                    )

                    // Offline banner
                    OfflineBanner(
                        isOffline = uiState.isOffline,
                        onRetry = { homeViewModel.loadData() }
                    )

                    // Filter Chips Row
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = uiState.currentFilter == HomeFilter.ALL,
                                onClick = { homeViewModel.setFilter(HomeFilter.ALL) },
                                label = { Text("الكل") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = uiState.currentFilter == HomeFilter.UNREAD,
                                onClick = { homeViewModel.setFilter(HomeFilter.UNREAD) },
                                label = { Text("غير مقروءة") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = uiState.currentFilter == HomeFilter.GROUPS,
                                onClick = { homeViewModel.setFilter(HomeFilter.GROUPS) },
                                label = { Text("المجموعات") }
                            )
                        }
                        item {
                            FilterChip(
                                selected = uiState.currentFilter == HomeFilter.PINNED,
                                onClick = { homeViewModel.setFilter(HomeFilter.PINNED) },
                                label = { Text("المثبتة 📌") }
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            val iconStyle = LocalIconStyle.current
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("home_bottom_nav")
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { ThemedIcon(AppIconAction.CHATS, iconStyle, size = 22.dp) },
                    label = { Text("المحادثات") },
                    modifier = Modifier.testTag("nav_item_chats")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { ThemedIcon(AppIconAction.CONTACTS, iconStyle, size = 22.dp) },
                    label = { Text("جهات الاتصال") },
                    modifier = Modifier.testTag("nav_item_contacts")
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { ThemedIcon(AppIconAction.SEARCH, iconStyle, size = 22.dp) },
                    label = { Text(stringResource(R.string.nav_search)) },
                    modifier = Modifier.testTag("nav_item_search")
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { ThemedIcon(AppIconAction.SETTINGS, iconStyle, size = 22.dp) },
                    label = { Text(stringResource(R.string.nav_settings)) },
                    modifier = Modifier.testTag("nav_item_settings")
                )
            }
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { showFabSheet = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("fab_new_action")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "New Action")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> {
                    // CHATS LIST TAB
                    if (uiState.isLoading && uiState.conversations.isEmpty()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    } else if (filteredConversations.isEmpty()) {
                        RaseelEmptyState(
                            icon = Icons.Default.ChatBubbleOutline,
                            title = "لا توجد محادثات هنا",
                            description = "ابدأ محادثة جديدة مع أحد جهات الاتصال الآن",
                            actionLabel = "محادثة جديدة",
                            onActionClick = { selectedTab = 1 },
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(filteredConversations, key = { it.id }) { conv ->
                                ConversationRowItem(
                                    conversation = conv,
                                    onClick = { onOpenConversation(conv.id) },
                                    onLongClick = {
                                        selectedConversationForMenu = conv
                                    }
                                )
                            }
                        }
                    }
                }

                1 -> {
                    // CONTACTS TAB
                    ContactsScreen(
                        viewModel = contactsViewModel,
                        onNavigateBack = { selectedTab = 0 },
                        onOpenConversation = onOpenConversation
                    )
                }

                2 -> {
                    // GLOBAL SEARCH TAB
                    SearchScreen(
                        viewModel = searchViewModel,
                        onNavigateBack = { selectedTab = 0 },
                        onOpenConversation = onOpenConversation
                    )
                }

                3 -> {
                    // SETTINGS TAB
                    SettingsScreen(
                        profileViewModel = profileViewModel,
                        isDarkTheme = isDarkTheme,
                        onToggleDarkTheme = onToggleDarkTheme,
                        isArabic = isArabic,
                        onToggleLanguage = onToggleLanguage,
                        onNavigateBack = { selectedTab = 0 },
                        onNavigateToEditProfile = onNavigateToEditProfile,
                        onNavigateToCustomizationStudio = onNavigateToCustomizationStudio,
                        onNavigateToPrivacy = onNavigateToPrivacy,
                        onNavigateToAppearance = onNavigateToAppearance,
                        onNavigateToStorage = onNavigateToStorage,
                        onNavigateToNotifications = onNavigateToNotifications,
                        onNavigateToAbout = onNavigateToAbout,
                        onNavigateToBackendConfig = onNavigateToBackendConfig,
                        onSignedOut = onSignedOut
                    )
                }
            }
        }
    }

    // Modern FAB Action Bottom Sheet
    if (showFabSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFabSheet = false },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = "بدء إجراء جديد",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                ListItem(
                    headlineContent = { Text("محادثة جديدة") },
                    supportingContent = { Text("اختر من جهات الاتصال لبدء التراسل") },
                    leadingContent = {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
                    },
                    modifier = Modifier.clickable {
                        showFabSheet = false
                        selectedTab = 1
                    }
                )

                ListItem(
                    headlineContent = { Text("إنشاء مجموعة جديدة") },
                    supportingContent = { Text("تواصل مع عدة أعضاء في وقت واحد") },
                    leadingContent = {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.GroupAdd, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        }
                    },
                    modifier = Modifier.clickable {
                        showFabSheet = false
                        onNavigateToCreateGroup()
                    }
                )

                ListItem(
                    headlineContent = { Text("إضافة جهة اتصال") },
                    supportingContent = { Text("أضف شخصاً جديداً بالاسم ورقم الهاتف") },
                    leadingContent = {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.tertiaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                        }
                    },
                    modifier = Modifier.clickable {
                        showFabSheet = false
                        showAddContactDialog = true
                    }
                )
            }
        }
    }

    // Long-press Context Sheet on a Conversation
    selectedConversationForMenu?.let { conv ->
        ModalBottomSheet(
            onDismissRequest = { selectedConversationForMenu = null },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = conv.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                ListItem(
                    headlineContent = { Text(if (conv.isPinned) "إلغاء التثبيت" else "تثبيت المحادثة") },
                    leadingContent = { Icon(Icons.Default.PushPin, contentDescription = null) },
                    modifier = Modifier.clickable {
                        homeViewModel.togglePin(conv)
                        selectedConversationForMenu = null
                    }
                )

                ListItem(
                    headlineContent = { Text(if (conv.isMuted) "إلغاء الكتم" else "كتم الإشعارات") },
                    leadingContent = {
                        Icon(
                            if (conv.isMuted) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.clickable {
                        homeViewModel.toggleMute(conv)
                        selectedConversationForMenu = null
                    }
                )

                ListItem(
                    headlineContent = { Text(if (conv.unreadCount > 0) "تعيين كمقروء" else "تعيين كغير مقروء") },
                    leadingContent = {
                        Icon(
                            if (conv.unreadCount > 0) Icons.Default.MarkChatRead else Icons.Default.MarkChatUnread,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.clickable {
                        if (conv.unreadCount > 0) homeViewModel.markAsRead(conv)
                        else homeViewModel.markAsUnread(conv)
                        selectedConversationForMenu = null
                    }
                )

                ListItem(
                    headlineContent = { Text(if (conv.isArchived) "إلغاء الأرشفة" else "أرشفة المحادثة") },
                    leadingContent = { Icon(Icons.Default.Archive, contentDescription = null) },
                    modifier = Modifier.clickable {
                        homeViewModel.toggleArchive(conv)
                        selectedConversationForMenu = null
                    }
                )

                ListItem(
                    headlineContent = { Text("حذف المحادثة", color = MaterialTheme.colorScheme.error) },
                    leadingContent = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                    modifier = Modifier.clickable {
                        showDeleteConvDialog = true
                    }
                )
            }
        }
    }

    // Delete conversation confirmation dialog
    if (showDeleteConvDialog && selectedConversationForMenu != null) {
        val conv = selectedConversationForMenu!!
        AlertDialog(
            onDismissRequest = {
                showDeleteConvDialog = false
                selectedConversationForMenu = null
            },
            title = { Text("حذف المحادثة") },
            text = { Text("هل أنت متأكد من حذف محادثة \"${conv.title}\"؟ سيتم مسح جميع الرسائل المحلية.") },
            confirmButton = {
                Button(
                    onClick = {
                        homeViewModel.deleteConversation(conv)
                        showDeleteConvDialog = false
                        selectedConversationForMenu = null
                        Toast.makeText(context, "تم حذف المحادثة", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("حذف")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteConvDialog = false
                    selectedConversationForMenu = null
                }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Add Contact Dialog
    if (showAddContactDialog) {
        AlertDialog(
            onDismissRequest = { showAddContactDialog = false },
            title = { Text("إضافة جهة اتصال جديدة") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newContactName,
                        onValueChange = { newContactName = it },
                        label = { Text("الاسم") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newContactUsername,
                        onValueChange = { newContactUsername = it },
                        label = { Text("اسم المستخدم (Username)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newContactPhone,
                        onValueChange = { newContactPhone = it },
                        label = { Text("رقم الهاتف") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newContactName.isNotBlank()) {
                            homeViewModel.addContact(newContactName.trim(), newContactUsername.trim(), newContactPhone.trim())
                            showAddContactDialog = false
                            newContactName = ""
                            newContactUsername = ""
                            newContactPhone = ""
                            Toast.makeText(context, "تمت إضافة جهة الاتصال", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("إضافة")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddContactDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ConversationRowItem(
    conversation: Conversation,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val cardRadius = LocalThemeConfig.current.cardRadiusDp.dp
    Card(
        shape = RoundedCornerShape(cardRadius),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 3.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .testTag("conversation_row_${conversation.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
        RaseelAvatar(
            name = conversation.title,
            avatarUrl = conversation.avatarUrl,
            isGroup = conversation.type == ConversationType.GROUP,
            isOnline = conversation.directUser?.isOnline == true,
            size = 52.dp
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = conversation.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (conversation.unreadCount > 0) FontWeight.Bold else FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )

                if (conversation.lastMessageAt.isNotBlank()) {
                    Text(
                        text = DateUtils.formatRelativeTime(conversation.lastMessageAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (conversation.unreadCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Snippet with draft support
                if (conversation.draft.isNotBlank()) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "[مسودة] ",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = conversation.draft,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    val snippet = conversation.lastMessageText.ifBlank { "لا توجد رسائل بعد" }
                    Text(
                        text = snippet,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (conversation.unreadCount > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (conversation.unreadCount > 0) FontWeight.Medium else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (conversation.isMuted) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeOff,
                            contentDescription = "Muted",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(end = 4.dp)
                        )
                    }
                    if (conversation.isPinned) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(end = 4.dp)
                        )
                    }
                    if (conversation.unreadCount > 0) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${conversation.unreadCount}",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
}
