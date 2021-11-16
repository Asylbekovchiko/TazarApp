package com.io.tazarapp.data.repository

import android.util.Log
import com.io.tazarapp.data.api.AuthApi
import com.io.tazarapp.data.model.auth.AuthResponseModel
import com.io.tazarapp.data.model.auth.CheckUserModel
import com.io.tazarapp.data.model.auth.CityModel
import okhttp3.ResponseBody
import retrofit2.Response

class LoginRepository(private var api: AuthApi) {

    suspend fun postAuthUser(
        phoneNumber: String,
        userType: String,
        city: Int,
        idToken: String
    ): Response<AuthResponseModel>? {
        return try {
            api.postSignIn(phoneNumber, userType, city, idToken)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getAuthUser(phone: String, idToken: String): Response<AuthResponseModel>? {
        return try {
            api.getSignIn(phone, idToken)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun checkUser(phone: String): Response<CheckUserModel>? {
        return try {
            api.checkUser(phone)
        } catch (e: Exception) {
            null
        }
    }


    suspend fun getCity(): Response<ArrayList<CityModel>>? {
        return try {
            api.getCity()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun changeNumber(phone: String, idToken: String): Response<AuthResponseModel>? {
        return try {
            api.changeNumber(phone, idToken)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveDeviceId(tokenFromPrefs: String?, type: String): Response<ResponseBody>? {
        return try {
            api.saveDeviceId(tokenFromPrefs, type)
        } catch (e : Exception) {
            null
        }
    }


}