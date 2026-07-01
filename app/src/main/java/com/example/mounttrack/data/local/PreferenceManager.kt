package com.example.mounttrack.data.local

import android.content.Context
import android.content.SharedPreferences

/**
 * Pengelola SharedPreferences untuk menyimpan preferensi pengguna secara lokal.
 * Menyimpan data checklist perlengkapan, pengaturan suhu, tema, dan profil.
 */
class PreferenceManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "mounttrack_preferences"
        private const val KEY_IS_CELSIUS = "is_celsius"
        private const val KEY_IS_DARK_MODE = "is_dark_mode"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_AVATAR = "user_avatar"
        private const val KEY_CHECKED_GEARS = "checked_gears"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_PASSWORD = "user_password"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    // --- Pengaturan Satuan Suhu ---
    var isCelsius: Boolean
        get() = prefs.getBoolean(KEY_IS_CELSIUS, true)
        set(value) = prefs.edit().putBoolean(KEY_IS_CELSIUS, value).apply()

    // --- Pengaturan Tema ---
    var isDarkMode: Boolean
        get() = prefs.getBoolean(KEY_IS_DARK_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_DARK_MODE, value).apply()

    // --- Data Profil ---
    var userName: String
        get() = prefs.getString(KEY_USER_NAME, "Pendaki Gunung") ?: "Pendaki Gunung"
        set(value) = prefs.edit().putString(KEY_USER_NAME, value).apply()

    var userAvatar: String
        get() = prefs.getString(KEY_USER_AVATAR, "") ?: ""
        set(value) = prefs.edit().putString(KEY_USER_AVATAR, value).apply()

    var userEmail: String
        get() = prefs.getString(KEY_USER_EMAIL, "") ?: ""
        set(value) = prefs.edit().putString(KEY_USER_EMAIL, value).apply()

    var userPassword: String
        get() = prefs.getString(KEY_USER_PASSWORD, "") ?: ""
        set(value) = prefs.edit().putString(KEY_USER_PASSWORD, value).apply()

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()

    // --- Checklist Perlengkapan ---
    fun getCheckedGears(): Set<String> {
        return prefs.getStringSet(KEY_CHECKED_GEARS, emptySet()) ?: emptySet()
    }

    fun setCheckedGears(gears: Set<String>) {
        prefs.edit().putStringSet(KEY_CHECKED_GEARS, gears).apply()
    }

    fun clearCheckedGears() {
        prefs.edit().remove(KEY_CHECKED_GEARS).apply()
    }
}
