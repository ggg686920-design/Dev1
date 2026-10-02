package com.example.presentation.groups

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Shield
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.models.ConversationMember
import com.example.data.models.MemberRole
import com.example.presentation.common.RaseelAvatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupInfoScreen(
    viewModel: GroupInfoViewModel,
    onNavigateBack: () -> Unit,
    onLeftGroup: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.hasLeftGroup) {
        if (uiState.hasLeftGroup) {
            onLeftGroup()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.group_info)) },
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
            var showPermissionsDialog by remember { mutableStateOf(false) }
            var canSendMessages by remember { mutableStateOf(true) }
            var canSendMedia by remember { mutableStateOf(true) }
            var canAddMembers by remember { mutableStateOf(true) }
            var canPinMessages by remember { mutableStateOf(false) }

            // Group Permissions Action Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { showPermissionsDialog = true }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("صلاحيات وإعدادات المجموعة", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Text("التحكم في إرسال الرسائل، الوسائط، وإضافة الأعضاء", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text("عرض", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }

            if (showPermissionsDialog) {
                AlertDialog(
                    onDismissRequest = { showPermissionsDialog = false },
                    title = { Text("صلاحيات أعضاء المجموعة") },
                    text = {
                        Column {
                            ListItem(
                                headlineContent = { Text("إرسال الرسائل") },
                                trailingContent = {
                                    Switch(checked = canSendMessages, onCheckedChange = { canSendMessages = it })
                                }
                            )
                            ListItem(
                                headlineContent = { Text("إرسال الوسائط والصور") },
                                trailingContent = {
                                    Switch(checked = canSendMedia, onCheckedChange = { canSendMedia = it })
                                }
                            )
                            ListItem(
                                headlineContent = { Text("إضافة أعضاء جدد") },
                                trailingContent = {
                                    Switch(checked = canAddMembers, onCheckedChange = { canAddMembers = it })
                                }
                            )
                            ListItem(
                                headlineContent = { Text("تثبيت الرسائل") },
                                trailingContent = {
                                    Switch(checked = canPinMessages, onCheckedChange = { canPinMessages = it })
                                }
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = { showPermissionsDialog = false }) {
                            Text("تم الحفظ")
                        }
                    }
                )
            }

            val context = LocalContext.current
            var showAddMemberDialog by remember { mutableStateOf(false) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "الأعضاء (${uiState.members.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                FilledTonalButton(
                    onClick = { showAddMemberDialog = true },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_add_group_member")
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("إضافة عضو")
                }
            }

            if (showAddMemberDialog) {
                val candidates = remember(uiState.members) { viewModel.getCandidateUsers() }
                AlertDialog(
                    onDismissRequest = { showAddMemberDialog = false },
                    title = { Text("إضافة عضو إلى المجموعة") },
                    text = {
                        if (candidates.isEmpty()) {
                            Text("جميع جهات الاتصال والأصدقاء مضافون بالفعل إلى هذه المجموعة.")
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(max = 300.dp)
                            ) {
                                items(candidates, key = { it.id }) { candidate ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RaseelAvatar(
                                            name = candidate.displayName,
                                            avatarUrl = candidate.avatarUrl,
                                            size = 38.dp
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = candidate.displayName,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = "@${candidate.username}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Button(
                                            onClick = {
                                                viewModel.addMember(candidate.id)
                                                Toast.makeText(context, "تمت إضافة ${candidate.displayName} بنجاح", Toast.LENGTH_SHORT).show()
                                                showAddMemberDialog = false
                                            },
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.height(34.dp)
                                        ) {
                                            Text("إضافة", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showAddMemberDialog = false }) {
                            Text("إغلاق")
                        }
                    }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(uiState.members, key = { it.id }) { member ->
                    MemberRow(
                        member = member,
                        myRole = uiState.myRole,
                        currentUserId = uiState.currentUserId,
                        onMakeAdmin = { viewModel.updateRole(member.userId, MemberRole.ADMIN) },
                        onRemoveAdmin = { viewModel.updateRole(member.userId, MemberRole.MEMBER) },
                        onRemoveMember = { viewModel.removeMember(member.userId) }
                    )
                }
            }

            // Leave Group button
            Surface(
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        uiState.currentUserId?.let { viewModel.removeMember(it) }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .testTag("btn_leave_group")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.leave_group),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun MemberRow(
    member: ConversationMember,
    myRole: MemberRole,
    currentUserId: String?,
    onMakeAdmin: () -> Unit,
    onRemoveAdmin: () -> Unit,
    onRemoveMember: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val isMe = member.userId == currentUserId
    val name = member.profile?.displayName ?: "User"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RaseelAvatar(
            name = name,
            avatarUrl = member.profile?.avatarUrl,
            size = 44.dp
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isMe) "$name (You)" else name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                if (member.role == MemberRole.OWNER || member.role == MemberRole.ADMIN) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.extraSmall
                    ) {
                        Text(
                            text = member.role.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Text(
                text = "@${member.profile?.username ?: ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Options menu for Owner / Admin
        val canManage = !isMe && (myRole == MemberRole.OWNER || (myRole == MemberRole.ADMIN && member.role == MemberRole.MEMBER))
        if (canManage) {
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Member options")
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    if (myRole == MemberRole.OWNER) {
                        if (member.role == MemberRole.MEMBER) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.make_admin)) },
                                leadingIcon = { Icon(Icons.Default.Shield, contentDescription = null) },
                                onClick = {
                                    menuExpanded = false
                                    onMakeAdmin()
                                }
                            )
                        } else if (member.role == MemberRole.ADMIN) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.remove_admin)) },
                                onClick = {
                                    menuExpanded = false
                                    onRemoveAdmin()
                                }
                            )
                        }
                    }
                    DropdownMenuItem(
                        text = {
                            Text(
                                stringResource(R.string.remove_from_group),
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onRemoveMember()
                        }
                    )
                }
            }
        }
    }
}
