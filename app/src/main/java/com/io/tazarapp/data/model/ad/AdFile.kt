package com.io.tazarapp.data.model.ad

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.Serializable

data class AdFile(var file: File? = null) : Serializable {

    fun getFileAsMultipart(): MultipartBody.Part? {
        return file?.asRequestBody("image/*".toMediaTypeOrNull())?.let {
            MultipartBody.Part.createFormData(
                "image",
                file?.name,
                it
            )
        }
    }

    fun clearData() {
        file = null
    }
}