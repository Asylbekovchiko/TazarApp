package com.io.tazarapp.data.api

import com.io.tazarapp.data.model.City
import com.io.tazarapp.data.model.Moderation
import com.io.tazarapp.data.model.Paging
import com.io.tazarapp.data.model.filter.FilterCategoryDetailModel
import com.io.tazarapp.data.model.filter.FilterCategoryModel
import com.io.tazarapp.data.model.collectionplace.CollectionPointModel
import com.io.tazarapp.data.model.cp.CpImageURl
import com.io.tazarapp.data.model.news.NewsDetailModel
import com.io.tazarapp.data.model.news.NewsModel
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface SharedApi {

    @GET("logistics/city/")
    suspend fun getCitiesList(): Response<ArrayList<City>>

    @GET("logistics/category/")
    suspend fun getFilterCategoryList(): Response<ArrayList<FilterCategoryModel>>

    @GET("logistics/category/{id}/")
    suspend fun getFilterCategoryDetail(@Path("id") id: Int): Response<FilterCategoryDetailModel>

    @POST("logistics/collection_point/")
    suspend fun createCP(@Body body: CollectionPointModel): Response<CollectionPointModel>

    @Multipart
    @POST("logistics/image/")
    suspend fun sendImage(@Part file: MultipartBody.Part?) : Response<CpImageURl>

    @FormUrlEncoded
    @PATCH("users/{id}/")
    suspend fun postUserType(
        @Path("id") id: Int,
        @Field("subcategory") subcategory: ArrayList<Int>?
    ): Response<ResponseBody>

    @GET("users/check_user_activate/")
    suspend fun getModerationStatus(): Response<Moderation>

    @GET("info/news/")
    suspend fun getNewsList(@Query("page") page: Int): Response<Paging<NewsModel>>

    @GET("info/news/{id}/")
    suspend fun getNewsDetail(@Path("id") id : Int) : Response<NewsDetailModel>
}