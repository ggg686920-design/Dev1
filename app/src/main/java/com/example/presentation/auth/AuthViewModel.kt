package com.example.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.common.Resource
import com.example.core.di.AppContainer
import com.example.data.models.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successUser: UserProfile? = null,
    val isConfigured: Boolean = false
)

class AuthViewModel(private val container: AppContainer) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState(isConfigured = container.config.isConfigured()))
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signIn(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please fill in all fields")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            when (val res = container.authRepository.signIn(email, pass)) {
                is Resource.Success -> {
                    container.realtime.connect()
                    _uiState.value = _uiState.value.copy(isLoading = false, successUser = res.data)
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = res.message)
                }
                else -> Unit
            }
        }
    }

    fun signUp(email: String, pass: String, username: String, displayName: String) {
        if (email.isBlank() || pass.isBlank() || username.isBlank() || displayName.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Please fill in all fields")
            return
        }

        if (pass.length < 6) {
            _uiState.value = _uiState.value.copy(error = "Password must be at least 6 characters")
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            when (val res = container.authRepository.signUp(email, pass, username, displayName)) {
                is Resource.Success -> {
                    container.realtime.connect()
                    _uiState.value = _uiState.value.copy(isLoading = false, successUser = res.data)
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = res.message)
                }
                else -> Unit
            }
        }
    }

    fun saveBackendConfig(url: String, anonKey: String) {
        container.config.updateConfig(url, anonKey)
        _uiState.value = _uiState.value.copy(isConfigured = container.config.isConfigured(), error = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun resetState() {
        _uiState.value = AuthUiState(isConfigured = container.config.isConfigured())
    }
}
