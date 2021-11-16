package com.io.tazarapp.data.repository

import com.io.tazarapp.data.api.AuthApi
import com.io.tazarapp.data.model.Version
import com.io.tazarapp.data.model.auth.AuthResponseModel
import com.io.tazarapp.data.model.auth.CheckUserModel
import com.io.tazarapp.data.model.auth.CityModel
import com.io.tazarapp.utils.SharedPrefModule
import okhttp3.ResponseBody
import retrofit2.Response

class VersionCheckRepository(private var api: AuthApi) {

    suspend fun getVersion(): Response<Version>? {
        return try {
            api.getVersion()
        } catch (e: Exception) {
            null
        }
    }
}