package com.io.tazarapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.io.tazarapp.data.model.auth.AuthResponseModel


class SharedPrefModule(context: Context) {

    private var PRIVATE_MODE = 0
    private val KEY = "token"
    private val FIRST = "first"
    private val DEF_VALUE = "empty"
    private val DEVICE_ID = "device_id"
    private val sharedPref: SharedPreferences = context.getSharedPreferences(KEY, PRIVATE_MODE)

    inner class TokenModule {

        fun getToken(): AuthResponseModel? {
            val temp = sharedPref.getString(KEY, DEF_VALUE)!!

            return if (temp == DEF_VALUE) {
                null
            } else {
                Gson().fromJson(temp, AuthResponseModel::class.java)
            }
        }

        fun saveToken(token: AuthResponseModel?) {
            val temp = Gson().toJson(token)
            val editor = sharedPref.edit()
            editor.putString(KEY, temp)
            editor.apply()
        }

        fun saveDeviceID() {

        }

        fun deleteToken() {
            val editor = sharedPref.edit()
            editor.remove(KEY)
            editor.apply()
        }

    }

    inner class FirstTimeModule {

        fun isFirstTime(): Boolean {
            return sharedPref.getBoolean(FIRST, true)
        }

        fun saveFirstTime(bool: Boolean) {
            val editor = sharedPref.edit()
            editor.putBoolean(FIRST, bool)
            editor.apply()
        }
    }
}