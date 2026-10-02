package com.example.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.core.settings.AppSettingsManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    settingsManager: AppSettingsManager,
    onNavigateBack: () -> Unit
) {
    val settings by settingsManager.settings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("إعدادات الإشعارات", fontWeight = FontWeight.Bold) },
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(shape = RoundedCornerShape(12.dp)) {
                Column {
                    ListItem(
                        headlineContent = { Text("تفعيل الإشعارات العامة", fontWeight = FontWeight.SemiBold) },
                        supportingContent = { Text("استلام تنبيهات عند ورود رسائل جديدة") },
                        trailingContent = {
                            Switch(
                                checked = settings.notifications.enabled,
                                onCheckedChange = {
                                    settingsManager.updateNotifications(settings.notifications.copy(enabled = it))
                                }
                            )
                        }
                    )
                }
            }

            Text("تنبيهات المحادثات", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Card(shape = RoundedCornerShape(12.dp)) {
                Column {
                    ListItem(
                        headlineContent = { Text("إشعارات المحادثات الفردية") },
                        trailingContent = {
                            Switch(
                                checked = settings.notifications.messageNotifications && settings.notifications.enabled,
                                enabled = settings.notifications.enabled,
                                onCheckedChange = {
                                    settingsManager.updateNotifications(settings.notifications.copy(messageNotifications = it))
                                }
                            )
                        }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("إشعارات المجموعات") },
                        trailingContent = {
                            Switch(
                                checked = settings.notifications.groupNotifications && settings.notifications.enabled,
                                enabled = settings.notifications.enabled,
                                onCheckedChange = {
                                    settingsManager.updateNotifications(settings.notifications.copy(groupNotifications = it))
                                }
                            )
                        }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("معاينة نص الرسالة في الإشعار") },
                        trailingContent = {
                            Switch(
                                checked = settings.notifications.showPreview && settings.notifications.enabled,
                                enabled = settings.notifications.enabled,
                                onCheckedChange = {
                                    settingsManager.updateNotifications(settings.notifications.copy(showPreview = it))
                                }
                            )
                        }
                    )
                }
            }

            Text("الصوت والاهتزاز", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Card(shape = RoundedCornerShape(12.dp)) {
                Column {
                    ListItem(
                        headlineContent = { Text("نغمة التنبيه الصوتية") },
                        trailingContent = {
                            Switch(
                                checked = settings.notifications.sound && settings.notifications.enabled,
                                enabled = settings.notifications.enabled,
                                onCheckedChange = {
                                    settingsManager.updateNotifications(settings.notifications.copy(sound = it))
                                }
                            )
                        }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("الاهتزاز عند استلام رسالة") },
                        trailingContent = {
                            Switch(
                                checked = settings.notifications.vibration && settings.notifications.enabled,
                                enabled = settings.notifications.enabled,
                                onCheckedChange = {
                                    settingsManager.updateNotifications(settings.notifications.copy(vibration = it))
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}
