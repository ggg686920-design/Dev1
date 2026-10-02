package com.example.presentation.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.common.Resource
import com.example.core.di.AppContainer
import com.example.data.models.ConversationMember
import com.example.data.models.MemberRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GroupInfoUiState(
    val conversationId: String,
    val members: List<ConversationMember> = emptyList(),
    val currentUserId: String? = null,
    val myRole: MemberRole = MemberRole.MEMBER,
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasLeftGroup: Boolean = false
)

class GroupInfoViewModel(
    private val conversationId: String,
    private val container: AppContainer
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        GroupInfoUiState(
            conversationId = conversationId,
            currentUserId = container.authRepository.getCurrentUserId()
        )
    )
    val uiState: StateFlow<GroupInfoUiState> = _uiState.asStateFlow()

    init {
        loadMembers()
    }

    fun loadMembers() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            when (val res = container.chatRepository.getGroupMembers(conversationId)) {
                is Resource.Success -> {
                    val myUid = _uiState.value.currentUserId
                    val myMember = res.data.find { it.userId == myUid }
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        members = res.data,
                        myRole = myMember?.role ?: MemberRole.MEMBER
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = res.message)
                }
                else -> Unit
            }
        }
    }

    fun updateRole(userId: String, newRole: MemberRole) {
        viewModelScope.launch {
            when (container.chatRepository.updateMemberRole(conversationId, userId, newRole)) {
                is Resource.Success -> loadMembers()
                else -> Unit
            }
        }
    }

    fun removeMember(userId: String) {
        viewModelScope.launch {
            when (container.chatRepository.removeMember(conversationId, userId)) {
                is Resource.Success -> {
                    if (userId == _uiState.value.currentUserId) {
                        _uiState.value = _uiState.value.copy(hasLeftGroup = true)
                    } else {
                        loadMembers()
                    }
                }
                else -> Unit
            }
        }
    }
}
