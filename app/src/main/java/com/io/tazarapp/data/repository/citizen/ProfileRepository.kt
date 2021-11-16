package com.io.tazarapp.data.repository.citizen

import com.io.tazarapp.data.api.CitizenApi
import com.io.tazarapp.data.model.profile.ProfileMainModel
import com.io.tazarapp.data.model.qr_prize.QrPrizeModel
import com.io.tazarapp.data.model.qr_prize.QrScoreModel
import com.io.tazarapp.data.model.statistics.StatisticData
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response

class ProfileRepository(private var api: CitizenApi) {
    suspend fun updateProfileImage(
        image: MultipartBody.Part?
    ): Response<ResponseBody>? {
        return try {
            api.updateProfileImage(image)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateProfileInfo(id: Int, city: Int?, name: String): Response<ResponseBody>? {
        return try {
            api.updateProfileInfo(id, city, name)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getStatistics(): Response<ArrayList<StatisticData>>? {
        return try {
            api.getStatistics()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getMainProfile(): Response<ProfileMainModel>? {
        return try {
            api.getMainProfile()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getPartnersPrize(qrToken: String): Response<QrPrizeModel>? {
        return try {
            api.getPartnersPrize(qrToken)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun postPartnersPrize(qrToken: String, id: Int): Response<QrScoreModel>? {
        return try {
            api.postPartnersPrize(qrToken, id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleterRegistrationId(registrationId: String): Response<ResponseBody>? =
            try {
                api.deleteRegistrationId(registrationId)
            } catch (e: Exception) {
                null
            }
}