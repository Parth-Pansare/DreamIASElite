package com.app.dreamiaselite.ui.screen.screens.auth

import android.app.Application
import android.net.Uri
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.dreamiaselite.data.AuthPreferences
import com.app.dreamiaselite.data.AuthRepository
import com.app.dreamiaselite.data.local.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

data class AuthUiState(
    val isAuthenticated: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val currentUserEmail: String? = null,
    val currentUserName: String? = null,
    val targetYear: Int? = null,
    val createdAt: Long? = null,
    val avatarUrl: String? = null,
    val isProfileSaving: Boolean = false,
    val profileMessage: String? = null,
    val profileMessageIsError: Boolean = false
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val authPrefs = AuthPreferences(application)
    private val repository by lazy {
        val db = AppDatabase.getInstance(application)
        AuthRepository(
            userDao = db.userDao(),
            prefs = authPrefs
        )
    }

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    init {
        viewModelScope.launch {
            repository.currentUserEmail.collectLatest { email ->
                val user = email?.let { repository.getUserByEmail(it) }
                val prefAvatar = email?.let { repository.getStoredAvatar(it) }
                if (email != null && user == null) {
                    // Session refers to a user that no longer exists in DB; reset session but keep avatar for display.
                    authPrefs.clearCurrentUser()
                    _uiState.update {
                        it.copy(
                            isAuthenticated = false,
                            currentUserEmail = null,
                            currentUserName = null,
                            targetYear = null,
                            createdAt = null,
                            avatarUrl = prefAvatar,
                            isLoading = false,
                            errorMessage = "Session expired, please sign in again",
                            isProfileSaving = false,
                            profileMessage = null,
                            profileMessageIsError = false
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isAuthenticated = !email.isNullOrEmpty(),
                            currentUserEmail = email,
                            currentUserName = user?.username,
                            targetYear = user?.targetYear,
                            createdAt = user?.createdAt,
                            avatarUrl = user?.avatarUrl ?: prefAvatar,
                            isLoading = false,
                            errorMessage = null,
                            isProfileSaving = false,
                            profileMessage = null,
                            profileMessageIsError = false
                        )
                    }
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
                    onSuccess = {
                        state.copy(
                            isAuthenticated = true,
                            isLoading = false,
                            errorMessage = null,
                            isProfileSaving = false,
                            profileMessage = null,
                            profileMessageIsError = false
                        )
                    },
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
                    onSuccess = {
                        state.copy(
                            isAuthenticated = true,
                            isLoading = false,
                            errorMessage = null,
                            isProfileSaving = false,
                            profileMessage = null,
                            profileMessageIsError = false
                        )
                    },
                    onFailure = { state.copy(isLoading = false, errorMessage = it.message ?: "Registration failed") }
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _uiState.update {
                it.copy(
                    isAuthenticated = false,
                    currentUserEmail = null,
                    currentUserName = null,
                    targetYear = null,
                    createdAt = null,
                    isProfileSaving = false,
                    profileMessage = null,
                    profileMessageIsError = false
                )
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun clearProfileMessage() {
        _uiState.update { it.copy(profileMessage = null, profileMessageIsError = false) }
    }

    fun updateProfile(name: String, targetYearInput: String, avatarUri: Uri?) {
        val trimmedName = name.trim()
        if (trimmedName.isBlank()) {
            _uiState.update { it.copy(profileMessage = "Please enter your name", profileMessageIsError = true) }
            return
        }

        val fallbackYear = if (avatarUri != null) 2024 else null
        val targetYear = targetYearInput.toIntOrNull() ?: _uiState.value.targetYear ?: fallbackYear
        if (targetYear == null || targetYear !in 2024..2100) {
            _uiState.update { it.copy(profileMessage = "Enter a valid target year (2024-2100)", profileMessageIsError = true) }
            return
        }

        val email = _uiState.value.currentUserEmail
        if (email.isNullOrBlank()) {
            _uiState.update { it.copy(profileMessage = "You need to be signed in to edit the profile", profileMessageIsError = true) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isProfileSaving = true, profileMessage = null, profileMessageIsError = false) }
            val avatarUriToPersist = withContext(Dispatchers.IO) {
                avatarUri?.let { saveAvatarToInternalStorage(email, it) } ?: _uiState.value.avatarUrl?.let { Uri.parse(it) }
            }

            val result = repository.updateProfile(email, trimmedName, targetYear, avatarUriToPersist)
            _uiState.update { state ->
                result.fold(
                    onSuccess = {
                        val refreshedUser = runCatching { repository.getUserByEmail(email) }.getOrNull()
                        state.copy(
                            currentUserName = refreshedUser?.username ?: trimmedName,
                            targetYear = refreshedUser?.targetYear ?: targetYear,
                            avatarUrl = refreshedUser?.avatarUrl ?: avatarUriToPersist?.toString() ?: state.avatarUrl,
                            isProfileSaving = false,
                            profileMessage = "Profile updated",
                            profileMessageIsError = false
                        )
                    },
                    onFailure = {
                        // Even if DB missing, prefer keeping the avatar from prefs and show message.
                        state.copy(
                            isProfileSaving = false,
                            avatarUrl = avatarUriToPersist?.toString() ?: state.avatarUrl,
                            profileMessage = it.message ?: "Unable to update profile",
                            profileMessageIsError = true
                        )
                    }
                )
            }
        }
    }

    private fun setError(message: String) {
        _uiState.update { it.copy(errorMessage = message, isLoading = false) }
    }

    private fun saveAvatarToInternalStorage(email: String, sourceUri: Uri): Uri? {
        return runCatching {
            val app = getApplication<Application>()
            val avatarsDir = File(app.filesDir, "avatars").apply { mkdirs() }
            val safeName = email.replace(Regex("[^A-Za-z0-9._-]"), "_")
            val file = File(avatarsDir, "avatar_${safeName}.jpg")

            app.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(file, false).use { output ->
                    input.copyTo(output)
                }
            } ?: return null

            Uri.fromFile(file)
        }.getOrNull()
    }
}
