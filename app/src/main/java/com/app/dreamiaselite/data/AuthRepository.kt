package com.app.dreamiaselite.data

import android.net.Uri
import android.util.Base64
import com.app.dreamiaselite.data.local.UserDao
import com.app.dreamiaselite.data.local.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.UUID

class AuthRepository(
    private val userDao: UserDao,
    private val prefs: AuthPreferences
) {

    val currentUserEmail: Flow<String?> = prefs.currentUserEmail

    suspend fun register(
        email: String,
        username: String,
        targetYear: Int,
        password: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val existing = userDao.getUserByEmail(email)
            if (existing != null) {
                return@withContext Result.failure(IllegalStateException("Account already exists for this email"))
            }

            val salt = UUID.randomUUID().toString()
            val passwordHash = hashPassword(password, salt)
            val user = UserEntity(
                email = email,
                username = username,
                targetYear = targetYear,
                passwordHash = passwordHash,
                salt = salt
            )

            userDao.insertUser(user)
            prefs.setCurrentUser(email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val user = userDao.getUserByEmail(email)
                ?: return@withContext Result.failure(IllegalStateException("No account found for this email"))

            val hash = hashPassword(password, user.salt)
            if (hash != user.passwordHash) {
                return@withContext Result.failure(IllegalArgumentException("Incorrect password"))
            }

            prefs.setCurrentUser(user.email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout() {
        prefs.clearCurrentUser()
    }

    suspend fun getUserByEmail(email: String): UserEntity? = withContext(Dispatchers.IO) {
        userDao.getUserByEmail(email)
    }

    suspend fun updateProfile(
        email: String,
        username: String,
        targetYear: Int,
        avatarUri: Uri?
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val existing = userDao.getUserByEmail(email)
                ?: return@withContext Result.failure(IllegalStateException("No account found"))

            userDao.updateProfile(email = email, username = username, targetYear = targetYear, avatarUrl = avatarUri?.toString())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun hashPassword(password: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val input = "$salt:$password"
        val hashBytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(hashBytes, Base64.NO_WRAP)
    }
}
