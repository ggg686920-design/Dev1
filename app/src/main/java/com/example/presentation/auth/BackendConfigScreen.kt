package com.example.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.core.di.AppContainer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackendConfigScreen(
    container: AppContainer,
    onNavigateBack: () -> Unit
) {
    var url by remember { mutableStateOf(container.config.getUrl()) }
    var anonKey by remember { mutableStateOf(container.config.getAnonKey()) }
    var savedNotice by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.backend_config_title)) },
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
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Supabase backend provides Authentication, PostgreSQL Database, Storage buckets, and Realtime WebSocket updates.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = url,
                onValueChange = {
                    url = it
                    savedNotice = false
                },
                label = { Text(stringResource(R.string.supabase_url_label)) },
                leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_supabase_url")
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = anonKey,
                onValueChange = {
                    anonKey = it
                    savedNotice = false
                },
                label = { Text(stringResource(R.string.supabase_key_label)) },
                leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
                maxLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_supabase_key")
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (savedNotice) {
                Surface(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Configuration saved successfully!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }

            Button(
                onClick = {
                    container.config.updateConfig(url, anonKey)
                    if (container.config.isConfigured() && container.authRepository.isLoggedIn()) {
                        container.realtime.connect()
                    }
                    savedNotice = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_save_backend_config")
            ) {
                Text(
                    text = stringResource(R.string.save_config),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
