package com.prinkal.quiz.utils

import android.content.Context

object PrefUtil {
    private const val PREF_NAME = "PQuiz"
    fun saveToken(context: Context, token: String) {
        val editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
        editor.putString("fcmToken", token)
        editor.apply()
    }

    fun getToken(context: Context): String? {
        val sPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sPref.getString("fcmToken", null)
    }
}