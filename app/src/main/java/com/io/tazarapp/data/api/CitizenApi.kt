package com.io.tazarapp.data.api

import com.io.tazarapp.data.model.Paging
import com.io.tazarapp.data.model.ad.AdModel
import com.io.tazarapp.data.model.advertisement.my_advertisement.MyAdvertisementModel
import com.io.tazarapp.data.model.advertisement.my_advertisement.ScoringHistoryModel
import com.io.tazarapp.data.model.cp.CP
import com.io.tazarapp.data.model.filter.FilterCategoryModel
import com.io.tazarapp.data.model.filter.FilterSubcategoryModel
import com.io.tazarapp.data.model.profile.ProfileMainModel
import com.io.tazarapp.data.model.qr_prize.QrPrizeModel
import com.io.tazarapp.data.model.qr_prize.QrScoreModel
import com.io.tazarapp.data.model.statistics.StatisticData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface CitizenApi {

    @Multipart
    @POST("users/set_image_user/")
    suspend fun updateProfileImage(
        @Part image: MultipartBody.Part?
    ): Response<ResponseBody>

    @FormUrlEncoded
    @PATCH("users/{id}/")
    suspend fun updateProfileInfo(
        @Path("id") id: Int,
        @Field("city") city: Int?,
        @Field("name") name: String?
    ): Response<ResponseBody>

    @POST("advertisements/")
    suspend fun createAd(@Body body: AdModel): Response<AdModel>

    @Multipart
    @POST("advertisements/set_advertisements_image/")
    suspend fun sendFile(
        @Part file: MultipartBody.Part?,
        @Part("id") id: RequestBody,
        @Part("action") action: RequestBody
    ): Response<ResponseBody>

    @GET("advertisements/{id}/")
    suspend fun getAd(@Path("id") id: Int): Response<AdModel>

    @PUT("advertisements/{id}/")
    suspend fun updateAd(
        @Path("id") id: Int,
        @Body body: AdModel
    ): Response<AdModel>

    @GET("advertisements/all_user_ads/")
    suspend fun getAllUserAds(@Query("page") page: Int): Response<Paging<MyAdvertisementModel>>

    @GET("advertisements/scoring-history/")
    suspend fun getAllUserScoringHistory(@Query("page") page: Int): Response<Paging<ScoringHistoryModel>>

    @DELETE("advertisements/{id}/")
    suspend fun deleteByID(@Path("id") id : Int) : Response<ResponseBody>

    @GET("users/statistic/")
    suspend fun getStatistics(): Response<ArrayList<StatisticData>>

    @GET("users/profile/")
    suspend fun getMainProfile(): Response<ProfileMainModel>

    @GET("logistics/")
    suspend fun getCp(
        @Query("search") search:String?,
        @Query("city") city:Int?,
        @Query("subcategory") subcategory:String?
    ): Response<ArrayList<CP>>

    @GET("logistics/")
    suspend fun getCp(
        @Query("search") search:String?
    ): Response<ArrayList<CP>>

    @GET("partners/prize/")
    suspend fun getPartnersPrize(
        @Header("QR") QR: String
    ): Response<QrPrizeModel>

    @FormUrlEncoded
    @POST("partners/prize/")
    suspend fun postPartnersPrize(
        @Header("QR") QR: String,
        @Field("id") id: Int
    ): Response<QrScoreModel>

    @GET("logistics/subcategory/")
    suspend fun getFilterCategoryList(): Response<ArrayList<FilterSubcategoryModel>>

    @GET("logistics/{id}/")
    suspend fun getCPDetail(@Path("id") id:Int): Response<CP>

    @DELETE("users/device/{registration_id}/")
    suspend fun deleteRegistrationId(@Path("registration_id") registrationId: String): Response<ResponseBody>
}