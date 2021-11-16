package com.io.tazarapp.data.repository.citizen


import android.util.Log
import com.io.tazarapp.data.api.CitizenApi
import com.io.tazarapp.data.api.SharedApi
import com.io.tazarapp.data.model.City
import com.io.tazarapp.data.model.ad.AdFile
import com.io.tazarapp.data.model.ad.AdModel
import com.io.tazarapp.data.model.auth.AuthResponseModel
import com.io.tazarapp.data.model.collectionplace.CollectionPointModel
import com.io.tazarapp.data.model.cp.CpFile
import com.io.tazarapp.data.model.cp.CpImageURl
import com.io.tazarapp.data.model.rating.Makala
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response

class AdRepository(private var api: SharedApi, private var citizenApi: CitizenApi) {

    suspend fun getCitiesList(): Response<ArrayList<City>>? {
        return try {
            api.getCitiesList()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createAd(body: AdModel): Response<AdModel>? {
        return try {
            citizenApi.createAd(body)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun sendAdImage(
        adFile: AdFile?,
        id: RequestBody,
        action: RequestBody
    ): Response<ResponseBody>? {
        return try {
            citizenApi.sendFile(adFile?.getFileAsMultipart(), id, action)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getAdvertisement(id: Int): Response<AdModel>? {
        return try {
            citizenApi.getAd(id)
        } catch (e: Exception) {
            Log.e("ERROR", e.message.toString())
            null
        }
    }

    suspend fun updateAd(adModel: AdModel): Response<AdModel>? {
        return try {
            citizenApi.updateAd(adModel.id!!, adModel)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createCP(body: CollectionPointModel): Response<CollectionPointModel>? {
        return try {
            api.createCP(body)
        } catch (e: Exception) {
            Log.e("ERROR", e.message)
            null
        }
    }

    suspend fun sendCpImage(cpFile: CpFile?): Response<CpImageURl>? {
        return try {
            api.sendImage(cpFile?.getFileAsMultipart())
        } catch (e: Exception) {
            null
        }
    }
}