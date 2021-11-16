package com.io.tazarapp.data.repository.recycler

import android.util.Log
import com.io.tazarapp.data.api.RecyclerApi
import com.io.tazarapp.data.model.advertisement.user_advertisement.NewAdsForPut
import com.io.tazarapp.data.model.advertisement.user_advertisement.OwnedScoreModel
import com.io.tazarapp.data.model.advertisement.user_advertisement.UserAdvertisementModel
import okhttp3.ResponseBody
import retrofit2.Response

class FoundRepository(private val api: RecyclerApi) {
    suspend fun getCitizenAdsByQr(qrToken : String) : Response<ArrayList<UserAdvertisementModel>>? {
        return try {
            api.getCitizenAdsByQr(qrToken)
        }catch (e : Exception) {
            null
        }
    }

    suspend fun putSubcategoryAds(adsId: Int, newAds: NewAdsForPut) : Response<OwnedScoreModel>?{
        return try {
            api.putSubcategoryAds(adsId, newAds)
        }catch (e : Exception) {
            Log.e("fdsko",e.message.toString())
            null
        }
    }

}