package com.io.tazarapp.data.repository.recycler

import android.util.Log
import com.io.tazarapp.data.api.RecyclerApi
import com.io.tazarapp.data.api.SharedApi
import com.io.tazarapp.data.model.Moderation
import com.io.tazarapp.data.model.advertisement.user_advertisement.NewAdsForPut
import com.io.tazarapp.data.model.advertisement.user_advertisement.UserAdvertisementModel
import okhttp3.ResponseBody
import retrofit2.Response

class ModerationCheckRepository(private val api: SharedApi) {
    suspend fun getModerationStatus(): Response<Moderation>? {
        return try {
            api.getModerationStatus()
        } catch (e: Exception) {
            null
        }
    }
}