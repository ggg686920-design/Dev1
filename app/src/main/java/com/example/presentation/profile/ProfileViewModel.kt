package com.example.presentation.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.common.Resource
import com.example.core.di.AppContainer
import com.example.data.models.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false,
    val blockedUsers: List<UserProfile> = emptyList()
)

class ProfileViewModel(private val container: AppContainer) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        val uid = container.authRepository.getCurrentUserId() ?: return
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            when (val res = container.profileRepository.getProfile(uid)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, userProfile = res.data)
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = res.message)
                }
                else -> Unit
            }
        }
    }

    fun resetSaveState() {
        _uiState.value = _uiState.value.copy(saveSuccess = false, error = null)
    }

    fun updateProfile(username: String = "", displayName: String, bio: String, avatarUrl: String) {
        _uiState.value = _uiState.value.copy(isSaving = true, error = null, saveSuccess = false)
        viewModelScope.launch {
            val current = _uiState.value.userProfile
            when (val res = container.profileRepository.updateProfile(
                username = username,
                displayName = displayName,
                bio = bio,
                avatarUrl = avatarUrl,
                lastSeenVisibility = current?.lastSeenVisibility ?: "EVERYONE",
                photoVisibility = current?.photoVisibility ?: "EVERYONE",
                readReceipts = current?.readReceiptsEnabled ?: true
            )) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(isSaving = false, userProfile = res.data, saveSuccess = true)
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isSaving = false, error = res.message)
                }
                else -> Unit
            }
        }
    }

    fun uploadAvatar(uri: Uri, context: Context, onUploaded: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val input = context.contentResolver.openInputStream(uri) ?: return@launch
                val bytes = input.readBytes()
                input.close()
                val mime = context.contentResolver.getType(uri) ?: "image/jpeg"
                val name = "avatar_${System.currentTimeMillis()}.jpg"

                // Always write to local storage as fallback/immediate cache
                val avatarDir = java.io.File(context.filesDir, "avatars").apply { mkdirs() }
                val localFile = java.io.File(avatarDir, name)
                localFile.writeBytes(bytes)
                val localUri = localFile.toURI().toString()

                when (val res = container.storageRepository.uploadMedia("avatars", name, bytes, mime, container.authRepository.getCurrentUserId())) {
                    is Resource.Success -> onUploaded(res.data)
                    else -> onUploaded(localUri)
                }
            } catch (_: Exception) {}
        }
    }

    fun uploadAvatarBytes(bytes: ByteArray, context: Context, onUploaded: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val name = "avatar_${System.currentTimeMillis()}.jpg"
                val avatarDir = java.io.File(context.filesDir, "avatars").apply { mkdirs() }
                val localFile = java.io.File(avatarDir, name)
                localFile.writeBytes(bytes)
                val localUri = localFile.toURI().toString()

                when (val res = container.storageRepository.uploadMedia("avatars", name, bytes, "image/jpeg", container.authRepository.getCurrentUserId())) {
                    is Resource.Success -> onUploaded(res.data)
                    else -> onUploaded(localUri)
                }
            } catch (_: Exception) {}
        }
    }

    fun updatePrivacySettings(lastSeen: String, photo: String, readReceipts: Boolean) {
        val prof = _uiState.value.userProfile ?: return
        viewModelScope.launch {
            container.profileRepository.updateProfile(
                displayName = prof.displayName,
                bio = prof.bio,
                avatarUrl = prof.avatarUrl,
                lastSeenVisibility = lastSeen,
                photoVisibility = photo,
                readReceipts = readReceipts
            )
            loadProfile()
        }
    }

    fun loadBlockedUsers() {
        viewModelScope.launch {
            when (val res = container.blockRepository.getBlockedUserIds()) {
                is Resource.Success -> {
                    val users = mutableListOf<UserProfile>()
                    for (id in res.data) {
                        when (val uRes = container.profileRepository.getProfile(id)) {
                            is Resource.Success -> users.add(uRes.data)
                            else -> Unit
                        }
                    }
                    _uiState.value = _uiState.value.copy(blockedUsers = users)
                }
                else -> Unit
            }
        }
    }

    fun unblockUser(userId: String) {
        viewModelScope.launch {
            when (container.blockRepository.unblockUser(userId)) {
                is Resource.Success -> loadBlockedUsers()
                else -> Unit
            }
        }
    }

    fun signOut(onSignedOut: () -> Unit) {
        viewModelScope.launch {
            container.authRepository.signOut()
            container.realtime.disconnect()
            _uiState.value = ProfileUiState()
            onSignedOut()
        }
    }
}
