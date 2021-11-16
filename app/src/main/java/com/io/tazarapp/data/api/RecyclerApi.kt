package com.io.tazarapp.data.api

import com.io.tazarapp.data.model.Paging
import com.io.tazarapp.data.model.advertisement.user_advertisement.NewAdsForPut
import com.io.tazarapp.data.model.advertisement.user_advertisement.OwnedScoreModel
import com.io.tazarapp.data.model.advertisement.user_advertisement.UserAdvertisementModel
import com.io.tazarapp.data.model.citizen_info.CitizenInfoModel
import com.io.tazarapp.data.model.citizen_info.HandbookModel
import com.io.tazarapp.data.model.history.HistoryModel
import com.io.tazarapp.data.model.map.Ad
import com.io.tazarapp.data.model.partners.PartnersDetModel
import com.io.tazarapp.data.model.partners.PartnersModel
import com.io.tazarapp.data.model.profile.CollectionMap
import com.io.tazarapp.data.model.profile.ProfileMainModel
import com.io.tazarapp.data.model.profile.ProfileRecyclerModel
import com.io.tazarapp.data.model.profile.ProfileSendRecyclerModel
import com.io.tazarapp.data.model.profile.ScheduleModel
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface RecyclerApi {

    @GET("partners/")
    suspend fun getPartners(): Response<List<PartnersModel>>

    @GET("partners/{id}")
    suspend fun getPartnersDetail(
        @Path("id") id: Int
    ): Response<PartnersDetModel>

    @GET("info/about/")
    suspend fun getAboutInfo(): Response<CitizenInfoModel>

    @GET("advertisements/")
    suspend fun getAds(): Response<ArrayList<Ad>>

    @GET("advertisements/{id}")
    suspend fun getAdDetail(@Path("id") id: Int): Response<Ad>

    @GET("info/guide/")
    suspend fun getHandbook(): Response<ArrayList<HandbookModel>>

    @GET("info/guide/{id}")
    suspend fun getHandbookDetail(
        @Path("id") id: Int
    ): Response<HandbookModel>

    @GET("users/profile/")
    suspend fun getMainProfile(): Response<ProfileMainModel>

    @PATCH("users/{id}/")
    suspend fun updateSchedule(
        @Body schedule: ScheduleModel,
        @Path("id") id: Int?
    ): Response<ResponseBody>

    @GET("advertisements/user_ads/")
    suspend fun getCitizenAdsByQr(@Header("QR") qrToken: String): Response<ArrayList<UserAdvertisementModel>>

    @PUT("advertisements/user_ads/{id}/")
    suspend fun putSubcategoryAds(
        @Path("id") adsId: Int,
        @Body newAds: NewAdsForPut
    ): Response<OwnedScoreModel>

    @GET("users/profile/")
    suspend fun getRecyclerProfile(): Response<ProfileRecyclerModel>

    @GET("advertisements/processor-ads/")
    suspend fun getHistory(@Query("page") page: Int): Response<Paging<HistoryModel>>

    @Multipart
    @POST("users/set_image_user/")
    suspend fun updateRecyclerProfileImage(
        @Part image: MultipartBody.Part
    ): Response<ResponseBody>

    @PATCH("users/{id}/")
    suspend fun updateRecyclerProfile(
        @Path("id") id: Int,
        @Body profile: ProfileSendRecyclerModel?
    ): Response<ResponseBody>


    @DELETE("users/device/{registration_id}/")
    suspend fun deleteRegistrationId(@Path("registration_id") registrationId: String?): Response<ResponseBody>
}