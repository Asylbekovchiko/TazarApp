package com.io.tazarapp.data.repository.citizen

import com.io.tazarapp.data.api.CitizenApi
import com.io.tazarapp.data.model.Paging
import com.io.tazarapp.data.model.advertisement.my_advertisement.MyAdvertisementModel
import com.io.tazarapp.data.model.advertisement.my_advertisement.ScoringHistoryModel
import okhttp3.ResponseBody
import retrofit2.Response

class MyAdvertisementRepository (private val api : CitizenApi) {

    suspend fun getAllUserAds(page: Int): Response<Paging<MyAdvertisementModel>>? {
        return try {
            api.getAllUserAds(page)
        }catch (e : Exception) {
            null
        }
    }

    suspend fun getAllUserScoringHistory(page: Int): Response<Paging<ScoringHistoryModel>>? {
        return try {
            api.getAllUserScoringHistory(page)
        }catch (e : Exception) {
            null
        }
    }

    suspend fun deleteByID (id : Int) : Response<ResponseBody>? {
        return try {
            api.deleteByID(id)
        }catch (e : Exception) {
            null
        }
    }
}