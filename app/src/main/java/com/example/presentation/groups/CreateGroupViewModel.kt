package com.example.presentation.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.common.Resource
import com.example.core.di.AppContainer
import com.example.data.models.Conversation
import com.example.data.models.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CreateGroupUiState(
    val title: String = "",
    val avatarUrl: String = "",
    val selectedUsers: List<UserProfile> = emptyList(),
    val searchResults: List<UserProfile> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val createdConversation: Conversation? = null
)

class CreateGroupViewModel(private val container: AppContainer) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateGroupUiState())
    val uiState: StateFlow<CreateGroupUiState> = _uiState.asStateFlow()

    fun updateTitle(newTitle: String) {
        _uiState.value = _uiState.value.copy(title = newTitle)
    }

    fun searchUsers(query: String) {
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(searchResults = emptyList())
            return
        }
        viewModelScope.launch {
            when (val res = container.profileRepository.searchUsers(query)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(searchResults = res.data)
                }
                else -> Unit
            }
        }
    }

    fun toggleUserSelection(user: UserProfile) {
        val current = _uiState.value.selectedUsers.toMutableList()
        if (current.any { it.id == user.id }) {
            current.removeAll { it.id == user.id }
        } else {
            current.add(user)
        }
        _uiState.value = _uiState.value.copy(selectedUsers = current)
    }

    fun createGroup() {
        val title = _uiState.value.title.trim()
        if (title.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please enter a group name")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val memberIds = _uiState.value.selectedUsers.map { it.id }
            when (val res = container.chatRepository.createGroupConversation(
                title = title,
                avatarUrl = _uiState.value.avatarUrl,
                memberUserIds = memberIds
            )) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, createdConversation = res.data)
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = res.message)
                }
                else -> Unit
            }
        }
    }
}
