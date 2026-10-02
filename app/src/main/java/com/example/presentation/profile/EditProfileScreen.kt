package com.example.presentation.profile

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.presentation.common.RaseelAvatar
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var username by remember { mutableStateOf(uiState.userProfile?.username.orEmpty()) }
    var displayName by remember { mutableStateOf(uiState.userProfile?.displayName.orEmpty()) }
    var bio by remember { mutableStateOf(uiState.userProfile?.bio.orEmpty()) }
    var avatarUrl by remember { mutableStateOf(uiState.userProfile?.avatarUrl.orEmpty()) }
    var showPhotoOptionsSheet by remember { mutableStateOf(false) }

    // Reset save state upon entering screen to fix instant ejection bug
    LaunchedEffect(Unit) {
        viewModel.resetSaveState()
        viewModel.loadProfile()
    }

    LaunchedEffect(uiState.userProfile) {
        uiState.userProfile?.let {
            username = it.username
            displayName = it.displayName
            bio = it.bio
            avatarUrl = it.avatarUrl
        }
    }

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            viewModel.resetSaveState()
            Toast.makeText(context, "تم حفظ بيانات الحساب بنجاح", Toast.LENGTH_SHORT).show()
            onNavigateBack()
        }
    }

    // Gallery Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.uploadAvatar(it, context) { newUrl ->
                avatarUrl = newUrl
            }
        }
    }

    // Camera Capture
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            val stream = ByteArrayOutputStream()
            it.compress(Bitmap.CompressFormat.JPEG, 90, stream)
            val bytes = stream.toByteArray()
            viewModel.uploadAvatarBytes(bytes, context) { newUrl ->
                avatarUrl = newUrl
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "يلزم منح إذن الكاميرا لالتقاط صورة شخصية", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.edit_profile), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.updateProfile(username = username, displayName = displayName, bio = bio, avatarUrl = avatarUrl) },
                        enabled = displayName.isNotBlank() && username.isNotBlank() && !uiState.isSaving,
                        modifier = Modifier.testTag("btn_save_profile")
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text(stringResource(R.string.save), fontWeight = FontWeight.Bold)
                        }
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
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .clickable { showPhotoOptionsSheet = true }
                    .testTag("btn_change_avatar")
            ) {
                RaseelAvatar(
                    name = displayName.ifBlank { "User" },
                    avatarUrl = avatarUrl,
                    size = 100.dp
                )
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Change Photo",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(7.dp)
                    )
                }
            }

            TextButton(
                onClick = { showPhotoOptionsSheet = true }
            ) {
                Text(
                    text = if (avatarUrl.isBlank()) "إضافة صورة شخصية" else "تغيير الصورة الشخصية",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Username Field (@username)
            OutlinedTextField(
                value = username,
                onValueChange = { input ->
                    val filtered = input.filter { it.isLetterOrDigit() || it == '_' }.lowercase()
                    if (filtered.length <= 30) username = filtered
                },
                label = { Text("اسم المستخدم (اليوزر)") },
                supportingText = {
                    Text(
                        text = if (username.isBlank()) "مطلوب: يمكن للآخرين البحث عن حسابك به" else "يمكن البحث عنك عبر: @$username",
                        color = if (username.isBlank()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                leadingIcon = {
                    Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_edit_username")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Display Name Field
            OutlinedTextField(
                value = displayName,
                onValueChange = { if (it.length <= 50) displayName = it },
                label = { Text(stringResource(R.string.display_name_label)) },
                supportingText = { Text("${displayName.length} / 50") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_edit_display_name")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Bio Field
            OutlinedTextField(
                value = bio,
                onValueChange = { if (it.length <= 160) bio = it },
                label = { Text(stringResource(R.string.bio_label)) },
                supportingText = { Text("${bio.length} / 160") },
                maxLines = 4,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_edit_bio")
            )

            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = uiState.error.orEmpty(),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }

    // Modal Bottom Sheet for Avatar Selection
    if (showPhotoOptionsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPhotoOptionsSheet = false },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .navigationBarsPadding()
            ) {
                Text(
                    text = "صورة الحساب الشخصي",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                ListItem(
                    headlineContent = { Text("اختيار من المعرض") },
                    leadingContent = {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    modifier = Modifier.clickable {
                        showPhotoOptionsSheet = false
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )

                ListItem(
                    headlineContent = { Text("التقاط صورة بالكاميرا") },
                    leadingContent = {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    },
                    modifier = Modifier.clickable {
                        showPhotoOptionsSheet = false
                        val hasCam = context.checkSelfPermission(android.Manifest.permission.CAMERA) == android.content.pm.PackageManager.PERMISSION_GRANTED
                        if (hasCam) {
                            cameraLauncher.launch(null)
                        } else {
                            cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                        }
                    }
                )

                if (avatarUrl.isNotBlank()) {
                    ListItem(
                        headlineContent = {
                            Text("حذف الصورة الحالية", color = MaterialTheme.colorScheme.error)
                        },
                        leadingContent = {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        },
                        modifier = Modifier.clickable {
                            showPhotoOptionsSheet = false
                            avatarUrl = ""
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
