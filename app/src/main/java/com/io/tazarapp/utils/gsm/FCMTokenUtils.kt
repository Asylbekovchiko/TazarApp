package com.io.tazarapp.utils.gsm

import android.preference.PreferenceManager
import com.google.firebase.iid.FirebaseInstanceId
import com.io.tazarapp.App.Companion.context

object FCMTokenUtils {
    val preferences = PreferenceManager.getDefaultSharedPreferences(context)


    fun checkTokenFromPref () : Boolean {
        val regId = preferences.getString("registration_id", null)
        return !(regId == null || regId.isEmpty())
    }

    fun getTokenFromPrefs(): String? {
        val regId = preferences.getString("registration_id", null)

        return if (regId == null || regId.isEmpty()) {
            FirebaseInstanceId.getInstance().token.toString()
        } else {
            regId
        }

    }

    fun saveToken(newDeviceId: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()

        // Save to SharedPreferences
        editor.putString("registration_id", newDeviceId)
        editor.apply()
    }
}