package com.app.dreamiaselite.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class AuthPreferences(private val context: Context) {

    val currentUserEmail: Flow<String?> =
        context.authDataStore.data.map { prefs -> prefs[Keys.CURRENT_USER_EMAIL] }

    suspend fun setCurrentUser(email: String) {
        context.authDataStore.edit { prefs ->
            prefs[Keys.CURRENT_USER_EMAIL] = email
        }
    }

    suspend fun clearCurrentUser() {
        context.authDataStore.edit { prefs ->
            prefs.remove(Keys.CURRENT_USER_EMAIL)
        }
    }

    suspend fun setAvatarUrl(email: String, avatarUrl: String?) {
        val key = avatarKey(email)
        context.authDataStore.edit { prefs ->
            if (avatarUrl == null) {
                prefs.remove(key)
            } else {
                prefs[key] = avatarUrl
            }
        }
    }

    suspend fun getAvatarUrl(email: String): String? {
        val key = avatarKey(email)
        val prefs = context.authDataStore.data.first()
        return prefs[key]
    }

    private object Keys {
        val CURRENT_USER_EMAIL = stringPreferencesKey("current_user_email")
    }

    private fun avatarKey(email: String): Preferences.Key<String> {
        val safe = email.replace(Regex("[^A-Za-z0-9._-]"), "_")
        return stringPreferencesKey("avatar_$safe")
    }
}
