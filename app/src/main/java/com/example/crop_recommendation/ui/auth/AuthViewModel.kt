package com.example.crop_recommendation.ui.auth

import androidx.lifecycle.ViewModel
import com.example.crop_recommendation.data.FarmRepository
import com.example.crop_recommendation.model.User
import com.example.crop_recommendation.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data class Success(val user: User) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel(private val repository: FarmRepository) : ViewModel() {
    val currentUser = repository.currentUser

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    val locations = repository.indiaLocations

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.value = AuthUiState.Error("Email and password cannot be empty.")
            return
        }
        _uiState.value = AuthUiState.Loading
        val result = repository.login(email, pass)
        if (result.isSuccess) {
            _uiState.value = AuthUiState.Success(result.getOrThrow())
        } else {
            _uiState.value = AuthUiState.Error(result.exceptionOrNull()?.message ?: "Login failed")
        }
    }

    fun signUp(name: String, email: String, pass: String, role: UserRole, location: String, phone: String) {
        if (name.isBlank() || email.isBlank() || pass.isBlank() || phone.isBlank()) {
            _uiState.value = AuthUiState.Error("Please fill in all fields.")
            return
        }
        _uiState.value = AuthUiState.Loading
        val result = repository.signUp(name, email, pass, role, location, phone)
        if (result.isSuccess) {
            _uiState.value = AuthUiState.Success(result.getOrThrow())
        } else {
            _uiState.value = AuthUiState.Error(result.exceptionOrNull()?.message ?: "Sign up failed")
        }
    }

    fun logout() {
        repository.logout()
        _uiState.value = AuthUiState.Idle
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}
