package com.io.tazarapp.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody

open class ErrorHandler<T> {
    fun handleErrorBody(error: String): T {
        val type = object : TypeToken<T>() {}.type
        return Gson().fromJson(error, type)
    }
}