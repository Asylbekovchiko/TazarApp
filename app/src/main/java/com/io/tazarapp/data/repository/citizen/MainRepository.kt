package com.io.tazarapp.data.repository.citizen


import com.io.tazarapp.data.api.RecyclerApi
import com.io.tazarapp.data.model.citizen_info.CitizenInfoModel
import com.io.tazarapp.data.model.citizen_info.HandbookModel
import com.io.tazarapp.data.model.partners.PartnersDetModel
import com.io.tazarapp.data.model.partners.PartnersModel
import com.io.tazarapp.data.model.profile.ProfileMainModel
import retrofit2.Response

class MainRepository(private var api: RecyclerApi) {

    suspend fun getPartners(): Response<List<PartnersModel>>? {
        return try {
            api.getPartners()
        } catch (e: Exception) {
            null
        }
    }
    suspend fun getPartnersDetail(id: Int): Response<PartnersDetModel>? {
        return try {
            api.getPartnersDetail(id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getAboutInfo(): Response<CitizenInfoModel>? {
        return try {
            api.getAboutInfo()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getHandbook(): Response<ArrayList<HandbookModel>>? {
        return try {
            api.getHandbook()
        } catch (e: Exception) {
            null
        }
    }
    suspend fun getHandbookDetail(id: Int): Response<HandbookModel>? {
        return try {
            api.getHandbookDetail(id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getMainProfile(): Response<ProfileMainModel>? {
        return try {
            api.getMainProfile()
        }catch (e: Exception){
            null
        }
    }
}