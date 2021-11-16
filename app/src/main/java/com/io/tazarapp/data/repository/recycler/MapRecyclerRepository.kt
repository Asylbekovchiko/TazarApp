package com.io.tazarapp.data.repository.recycler


import com.io.tazarapp.data.api.RecyclerApi
import com.io.tazarapp.data.model.auth.AuthResponseModel
import com.io.tazarapp.data.model.map.Ad
import com.io.tazarapp.data.model.rating.Makala
import retrofit2.Response

class MapRecyclerRepository(private var api: RecyclerApi) {

    suspend fun getAds(): Response<ArrayList<Ad>>? {
        return try {
            api.getAds()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getAdDetail(id:Int): Response<Ad>? {
        return try {
            api.getAdDetail(id)
        } catch (e: Exception) {
            null
        }
    }

}