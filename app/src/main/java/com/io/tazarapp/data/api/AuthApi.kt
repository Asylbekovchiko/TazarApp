package com.io.tazarapp.data.api

import com.io.tazarapp.data.model.Version
import com.io.tazarapp.data.model.auth.AuthResponseModel
import com.io.tazarapp.data.model.auth.CheckUserModel
import com.io.tazarapp.data.model.auth.CityModel
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {

    @FormUrlEncoded
    @POST("users/")
    suspend fun postSignIn(
        @Field("phone") phone: String,
        @Field("user_type") userType: String,
        @Field("city") city: Int,
        @Field("firebase_token") idToken: String
    ): Response<AuthResponseModel>

    @FormUrlEncoded
    @POST("users/login/")
    suspend fun getSignIn(
        @Field("phone") phone: String,
        @Field("firebase_token") firebase_token: String
    ): Response<AuthResponseModel>

    @FormUrlEncoded
    @POST("users/check_user/")
    suspend fun checkUser(
        @Field("phone") phone: String
    ): Response<CheckUserModel>


    @GET("logistics/city/")
    suspend fun getCity(): Response<ArrayList<CityModel>>

    @FormUrlEncoded
    @PUT("users/update_user_phone/")
    suspend fun changeNumber(
        @Field("phone") phone: String,
        @Field("firebase_token") firebase_token: String
    ): Response<AuthResponseModel>

    @GET("version/")
    suspend fun getVersion(): Response<Version>

    @FormUrlEncoded
    @POST("users/devices/")
    suspend fun saveDeviceId(
        @Field("registration_id") tokenFromPrefs: String?,
        @Field("type") type: String
    ): Response<ResponseBody>
}