package com.studymate.sm.cid.util

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("studymate_prefs", Context.MODE_PRIVATE)

    var namaPengguna: String
        get() = prefs.getString(KEY_NAMA, "") ?: ""
        set(value) = prefs.edit().putString(KEY_NAMA, value).apply()

    var geminiApiKey: String
        get() = prefs.getString(KEY_GEMINI_API, "") ?: ""
        set(value) = prefs.edit().putString(KEY_GEMINI_API, value).apply()

    var temaGelap: Boolean
        get() = prefs.getBoolean(KEY_TEMA_GELAP, false)
        set(value) = prefs.edit().putBoolean(KEY_TEMA_GELAP, value).apply()

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_NAMA = "nama_pengguna"
        private const val KEY_GEMINI_API = "gemini_api_key"
        private const val KEY_TEMA_GELAP = "tema_gelap"
    }
}
