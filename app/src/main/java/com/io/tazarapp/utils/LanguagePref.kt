package com.io.tazarapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class LanguagePref {

    companion object {
        private var PRIVATE_MODE = 0
        private val KEY = "token"
        private val USER_INFO = "user"
        private val LANGUAGE = "lang"
        private val FIRST = "first"
        private val DEF_VALUE = "empty"

    }
    class LanguageModule {

        fun getLanguage(context : Context): String {
            val sharedPref: SharedPreferences = context.getSharedPreferences(KEY, PRIVATE_MODE)

            return sharedPref.getString(LANGUAGE, DEF_VALUE)!!
        }

        fun saveLanguage(context: Context,string: String) {
            val sharedPref: SharedPreferences = context.getSharedPreferences(KEY, PRIVATE_MODE)
            val editor = sharedPref.edit()
            editor.putString(LANGUAGE, string)
            editor.apply()
        }
    }

    class FirstTimeModule {

        fun isFirstTime(context: Context): Boolean {
            val sharedPref: SharedPreferences = context.getSharedPreferences(KEY, PRIVATE_MODE)

            return sharedPref.getBoolean(FIRST, true)
        }

        fun saveFirstTime(context: Context,bool: Boolean) {
            val sharedPref: SharedPreferences = context.getSharedPreferences(KEY, PRIVATE_MODE)

            val editor = sharedPref.edit()
            editor.putBoolean(FIRST, bool)
            editor.apply()
        }
    }

}
