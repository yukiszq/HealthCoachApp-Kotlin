package com.example.assignment.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "health_coach_prefs"
        private const val KEY_USER_ID = "logged_in_user_id"
    }

    fun saveSession(userId: Int) {
        prefs.edit().putInt(KEY_USER_ID, userId).apply()
    }

    fun getSession(): Int {
        return prefs.getInt(KEY_USER_ID, -1)
    }

    fun clearSession() {
        prefs.edit().remove(KEY_USER_ID).apply()
    }

    fun isLoggedIn(): Boolean {
        return getSession() != -1
    }
}