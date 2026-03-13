package com.develazquez.bibliocloud.data.local

import android.content.SharedPreferences
import javax.inject.Inject

class TokenManager @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    companion object {
        private const val KEY_TOKEN = "bibliocloud_auth_token"
        private const val KEY_USER_ID = "bibliocloud_user_id"
    }

    fun saveToken(token: String) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    fun saveUserId(userId: String) {
        sharedPreferences.edit().putString(KEY_USER_ID, userId).apply()
    }

    fun getUserId(): String? {
        return sharedPreferences.getString(KEY_USER_ID, null)
    }

    fun clearToken() {
        sharedPreferences.edit().remove(KEY_TOKEN).remove(KEY_USER_ID).apply()
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
}