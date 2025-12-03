package com.app.dreamiaselite.ui.screen.screens.auth

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.dreamiaselite.data.AuthPreferences
import com.app.dreamiaselite.data.AuthRepository
import com.app.dreamiaselite.data.local.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentUserEmail: String? = null,
    val currentUserName: String? = null
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository by lazy {
        val db = AppDatabase.getInstance(application)
        AuthRepository(
            userDao = db.userDao(),
            prefs = AuthPreferences(application)
        )
    }

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    init {
        viewModelScope.launch {
            repository.currentUserEmail.collectLatest { email ->
                val user = email?.let { repository.getUserByEmail(it) }
                _uiState.update {
                    it.copy(
                        isAuthenticated = !email.isNullOrEmpty(),
                        currentUserEmail = email,
                        currentUserName = user?.username,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            setError("Please enter a valid email")
            return
        }
        if (password.isBlank()) {
            setError("Password cannot be empty")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.login(email.trim(), password)
            _uiState.update { state ->
                result.fold(
                    onSuccess = { state.copy(isAuthenticated = true, isLoading = false, errorMessage = null) },
                    onFailure = { state.copy(isLoading = false, errorMessage = it.message ?: "Login failed") }
                )
            }
        }
    }

    fun register(email: String, username: String, targetYearInput: String, password: String) {
        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            setError("Please enter a valid email")
            return
        }
        if (username.isBlank()) {
            setError("Please enter your name")
            return
        }
        val targetYear = targetYearInput.toIntOrNull()
        if (targetYear == null || targetYear !in 2024..2100) {
            setError("Please enter a valid target year")
            return
        }
        if (password.length < 6) {
            setError("Password should be at least 6 characters")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.register(
                email = email.trim(),
                username = username.trim(),
                targetYear = targetYear,
                password = password
            )
            _uiState.update { state ->
                result.fold(
                    onSuccess = { state.copy(isAuthenticated = true, isLoading = false, errorMessage = null) },
                    onFailure = { state.copy(isLoading = false, errorMessage = it.message ?: "Registration failed") }
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _uiState.update { it.copy(isAuthenticated = false, currentUserEmail = null, currentUserName = null) }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    private fun setError(message: String) {
        _uiState.update { it.copy(errorMessage = message, isLoading = false) }
    }
}
