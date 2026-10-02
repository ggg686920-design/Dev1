package com.example.presentation.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.common.Resource
import com.example.core.di.AppContainer
import com.example.data.models.ContactItem
import com.example.data.models.Conversation
import com.example.data.models.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ContactsUiState(
    val contacts: List<ContactItem> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val showAddDialog: Boolean = false,
    val addError: String? = null
)

class ContactsViewModel(
    private val container: AppContainer
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContactsUiState())
    val uiState: StateFlow<ContactsUiState> = _uiState.asStateFlow()

    init {
        loadContacts()
    }

    fun loadContacts() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        viewModelScope.launch {
            when (val res = container.chatRepository.getContacts()) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        contacts = res.data.sortedBy { it.displayName }
                    )
                }
                else -> {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun setShowAddDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showAddDialog = show, addError = null)
    }

    fun addNewContact(name: String, username: String, phone: String) {
        if (name.isBlank()) {
            _uiState.value = _uiState.value.copy(addError = "يرجى إدخال اسم جهة الاتصال")
            return
        }
        if (username.isBlank() && phone.isBlank()) {
            _uiState.value = _uiState.value.copy(addError = "يرجى إدخال اسم المستخدم أو رقم الهاتف")
            return
        }

        viewModelScope.launch {
            container.chatRepository.addContact(name.trim(), username.trim(), phone.trim())
            setShowAddDialog(false)
            loadContacts()
        }
    }

    fun startChatWithContact(contact: ContactItem, onChatOpened: (String) -> Unit) {
        val userProfile = UserProfile(
            id = contact.userId,
            username = contact.username,
            displayName = contact.displayName,
            avatarUrl = contact.avatarUrl,
            isOnline = contact.isOnline,
            bio = contact.bio
        )
        val conv = container.demoDataManager.createOrGetDirectConversation(userProfile)
        onChatOpened(conv.id)
    }
}
