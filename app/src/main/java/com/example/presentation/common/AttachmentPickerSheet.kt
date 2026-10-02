package com.example.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttachmentPickerSheet(
    onPickGallery: () -> Unit,
    onPickCamera: () -> Unit,
    onPickDocument: () -> Unit,
    onPickAudio: () -> Unit,
    onPickContact: () -> Unit,
    onPickLocation: () -> Unit,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .navigationBarsPadding()
        ) {
            Text(
                text = "مشاركة المحتوى",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                AttachmentOptionItem(
                    icon = Icons.Default.PhotoLibrary,
                    label = "المعرض",
                    bgColor = Color(0xFF6366F1),
                    onClick = {
                        onDismissRequest()
                        onPickGallery()
                    }
                )
                AttachmentOptionItem(
                    icon = Icons.Default.CameraAlt,
                    label = "الكاميرا",
                    bgColor = Color(0xFFEC4899),
                    onClick = {
                        onDismissRequest()
                        onPickCamera()
                    }
                )
                AttachmentOptionItem(
                    icon = Icons.Default.InsertDriveFile,
                    label = "مستند",
                    bgColor = Color(0xFF3B82F6),
                    onClick = {
                        onDismissRequest()
                        onPickDocument()
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                AttachmentOptionItem(
                    icon = Icons.Default.Headphones,
                    label = "صوتيات",
                    bgColor = Color(0xFFF59E0B),
                    onClick = {
                        onDismissRequest()
                        onPickAudio()
                    }
                )
                AttachmentOptionItem(
                    icon = Icons.Default.Person,
                    label = "جهة اتصال",
                    bgColor = Color(0xFF10B981),
                    onClick = {
                        onDismissRequest()
                        onPickContact()
                    }
                )
                AttachmentOptionItem(
                    icon = Icons.Default.LocationOn,
                    label = "الموقع",
                    bgColor = Color(0xFFEF4444),
                    onClick = {
                        onDismissRequest()
                        onPickLocation()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AttachmentOptionItem(
    icon: ImageVector,
    label: String,
    bgColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp
        )
    }
}
