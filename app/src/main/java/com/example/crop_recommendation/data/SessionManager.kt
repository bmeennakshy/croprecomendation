package com.example.crop_recommendation.data

import android.content.Context
import android.content.SharedPreferences
import com.example.crop_recommendation.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("farm2market_session", Context.MODE_PRIVATE)
    val json = Json { ignoreUnknownKeys = true }

    private val _currentUser = MutableStateFlow<User?>(loadUserFromPrefs())
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun saveUser(user: User) {
        val jsonString = json.encodeToString(user)
        prefs.edit().putString(KEY_USER, jsonString).apply()
        _currentUser.value = user
    }

    fun clearSession() {
        prefs.edit().remove(KEY_USER).apply()
        _currentUser.value = null
    }

    private fun loadUserFromPrefs(): User? {
        val jsonString = prefs.getString(KEY_USER, null) ?: return null
        return try {
            json.decodeFromString<User>(jsonString)
        } catch (e: Exception) {
            null
        }
    }

    companion object {
        private const val KEY_USER = "logged_in_user"
    }
}
