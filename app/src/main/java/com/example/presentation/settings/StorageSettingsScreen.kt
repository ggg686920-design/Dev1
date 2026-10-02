package com.example.presentation.settings

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.settings.AppSettingsManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageSettingsScreen(
    settingsManager: AppSettingsManager,
    onNavigateBack: () -> Unit
) {
    val settings by settingsManager.settings.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showClearDialog by remember { mutableStateOf(false) }

    val cachedMb = remember(settings.cachedStorageBytes) {
        String.format("%.1f", settings.cachedStorageBytes / 1_000_000.0)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("البيانات والتخزين", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Storage Usage Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Storage,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("استهلاك التخزين المحلي", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        Text("$cachedMb MB", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LinearProgressIndicator(
                        progress = { 0.35f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StorageItemTag(label = "الصور: 24.2 MB", color = Color(0xFF6366F1))
                        StorageItemTag(label = "الصوتيات: 12.1 MB", color = Color(0xFFF59E0B))
                        StorageItemTag(label = "الملفات: 8.5 MB", color = Color(0xFF10B981))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showClearDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CleaningServices, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("مسح الذاكرة المؤقتة (Clear Cache)")
                    }
                }
            }

            // Auto-Download over Wi-Fi
            Text("التنزيل التلقائي للوسائط (Wi-Fi)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Card(shape = RoundedCornerShape(12.dp)) {
                Column {
                    ListItem(
                        headlineContent = { Text("الصور") },
                        trailingContent = {
                            Checkbox(
                                checked = settings.dataUsage.autoDownloadWifiPhotos,
                                onCheckedChange = {
                                    settingsManager.updateDataUsage(settings.dataUsage.copy(autoDownloadWifiPhotos = it))
                                }
                            )
                        }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("الرسائل الصوتية") },
                        trailingContent = {
                            Checkbox(
                                checked = settings.dataUsage.autoDownloadWifiAudio,
                                onCheckedChange = {
                                    settingsManager.updateDataUsage(settings.dataUsage.copy(autoDownloadWifiAudio = it))
                                }
                            )
                        }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("المستندات والملفات") },
                        trailingContent = {
                            Checkbox(
                                checked = settings.dataUsage.autoDownloadWifiFiles,
                                onCheckedChange = {
                                    settingsManager.updateDataUsage(settings.dataUsage.copy(autoDownloadWifiFiles = it))
                                }
                            )
                        }
                    )
                }
            }

            // Auto-Download over Mobile Data
            Text("التنزيل التلقائي عبر بيانات الهاتف", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Card(shape = RoundedCornerShape(12.dp)) {
                Column {
                    ListItem(
                        headlineContent = { Text("الصور") },
                        trailingContent = {
                            Checkbox(
                                checked = settings.dataUsage.autoDownloadMobilePhotos,
                                onCheckedChange = {
                                    settingsManager.updateDataUsage(settings.dataUsage.copy(autoDownloadMobilePhotos = it))
                                }
                            )
                        }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("الرسائل الصوتية") },
                        trailingContent = {
                            Checkbox(
                                checked = settings.dataUsage.autoDownloadMobileAudio,
                                onCheckedChange = {
                                    settingsManager.updateDataUsage(settings.dataUsage.copy(autoDownloadMobileAudio = it))
                                }
                            )
                        }
                    )
                    HorizontalDivider()
                    ListItem(
                        headlineContent = { Text("المستندات والملفات") },
                        trailingContent = {
                            Checkbox(
                                checked = settings.dataUsage.autoDownloadMobileFiles,
                                onCheckedChange = {
                                    settingsManager.updateDataUsage(settings.dataUsage.copy(autoDownloadMobileFiles = it))
                                }
                            )
                        }
                    )
                }
            }
        }
    }

    // Clear Cache confirmation dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("مسح الذاكرة المؤقتة") },
            text = { Text("هل أنت متأكد من تفريغ الذاكرة المؤقتة؟ لن يؤثر هذا على رسائلك النصية أو حسابك.") },
            confirmButton = {
                Button(
                    onClick = {
                        val freed = settingsManager.clearCache()
                        showClearDialog = false
                        Toast.makeText(context, "تم تفريغ الذاكرة المؤقتة بنجاح", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("مسح الآن")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
private fun StorageItemTag(label: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 11.sp
        )
    }
}
