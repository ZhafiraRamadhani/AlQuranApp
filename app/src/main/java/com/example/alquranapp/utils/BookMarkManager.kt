package com.example.alquranapp.utils

import android.content.Context

object BookmarkManager {
    private const val PREF_NAME = "alquran_prefs"
    private const val KEY_BOOKMARK = "bookmark"

    fun saveBookmark(context: Context, surahId: Int, ayatNumber: Int) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().putString(KEY_BOOKMARK, "$surahId:$ayatNumber").apply()
    }

    fun getBookmark(context: Context): Pair<Int, Int>? {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val bookmark = sharedPref.getString(KEY_BOOKMARK, null)
        return bookmark?.split(":")?.mapNotNull { it.toIntOrNull() }?.let {
            if (it.size == 2) it[0] to it[1] else null
        }
    }
}