package com.example.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.presentation.profile.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySettingsScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToBlockedUsers: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var lastSeen by remember { mutableStateOf(uiState.userProfile?.lastSeenVisibility ?: "EVERYONE") }
    var photo by remember { mutableStateOf(uiState.userProfile?.photoVisibility ?: "EVERYONE") }
    var readReceipts by remember { mutableStateOf(uiState.userProfile?.readReceiptsEnabled ?: true) }

    LaunchedEffect(uiState.userProfile) {
        uiState.userProfile?.let {
            lastSeen = it.lastSeenVisibility
            photo = it.photoVisibility
            readReceipts = it.readReceiptsEnabled
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_privacy)) },
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
                .padding(16.dp)
        ) {
            // Read Receipts Switch
            ListItem(
                headlineContent = { Text(stringResource(R.string.privacy_read_receipts)) },
                supportingContent = { Text("Display blue double checkmarks when messages are read") },
                trailingContent = {
                    Switch(
                        checked = readReceipts,
                        onCheckedChange = {
                            readReceipts = it
                            viewModel.updatePrivacySettings(lastSeen, photo, it)
                        }
                    )
                }
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Last Seen Visibility
            Text(
                text = stringResource(R.string.privacy_last_seen),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                FilterChip(
                    selected = lastSeen == "EVERYONE",
                    onClick = {
                        lastSeen = "EVERYONE"
                        viewModel.updatePrivacySettings("EVERYONE", photo, readReceipts)
                    },
                    label = { Text(stringResource(R.string.visibility_everyone)) },
                    leadingIcon = if (lastSeen == "EVERYONE") {
                        { Icon(Icons.Default.Done, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                FilterChip(
                    selected = lastSeen == "NOBODY",
                    onClick = {
                        lastSeen = "NOBODY"
                        viewModel.updatePrivacySettings("NOBODY", photo, readReceipts)
                    },
                    label = { Text(stringResource(R.string.visibility_nobody)) },
                    leadingIcon = if (lastSeen == "NOBODY") {
                        { Icon(Icons.Default.Done, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null
                )
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // Photo Visibility
            Text(
                text = stringResource(R.string.privacy_profile_photo),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                FilterChip(
                    selected = photo == "EVERYONE",
                    onClick = {
                        photo = "EVERYONE"
                        viewModel.updatePrivacySettings(lastSeen, "EVERYONE", readReceipts)
                    },
                    label = { Text(stringResource(R.string.visibility_everyone)) },
                    leadingIcon = if (photo == "EVERYONE") {
                        { Icon(Icons.Default.Done, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                FilterChip(
                    selected = photo == "NOBODY",
                    onClick = {
                        photo = "NOBODY"
                        viewModel.updatePrivacySettings(lastSeen, "NOBODY", readReceipts)
                    },
                    label = { Text(stringResource(R.string.visibility_nobody)) },
                    leadingIcon = if (photo == "NOBODY") {
                        { Icon(Icons.Default.Done, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null
                )
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp))

            // Blocked Contacts
            ListItem(
                headlineContent = { Text(stringResource(R.string.privacy_blocked_users)) },
                leadingContent = { Icon(Icons.Default.Block, contentDescription = null) },
                trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null) },
                modifier = Modifier
                    .clickable(onClick = onNavigateToBlockedUsers)
                    .testTag("item_blocked_users")
            )
        }
    }
}
