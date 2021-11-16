package com.io.tazarapp.data.api

import com.io.tazarapp.data.model.map.mapQuestModel.MapClassModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MapApi {

    @GET("route")
    suspend fun getTestMapData(
        @Query("key") key: String,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("outFormat") outFormat: String,
        @Query("ambiguities") ambiguities: String,
        @Query("routeType") routeType: String,
        @Query("doReverseGeocode") doReverseGeocode: Boolean,
        @Query("enhancedNarrative") enhancedNarrative: Boolean,
        @Query("avoidTimedConditions") avoidTimedConditions: Boolean,
        @Query("fullShape") fullShape: Boolean
    ): Response<MapClassModel>
}