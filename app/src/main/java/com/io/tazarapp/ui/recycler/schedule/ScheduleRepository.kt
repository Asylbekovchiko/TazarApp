package com.io.tazarapp.ui.recycler.schedule


import com.io.tazarapp.data.api.RecyclerApi
import com.io.tazarapp.data.api.SharedApi
import com.io.tazarapp.data.model.cp.CpFile
import com.io.tazarapp.data.model.cp.CpImageURl
import com.io.tazarapp.data.model.profile.ProfileRecyclerModel
import com.io.tazarapp.data.model.profile.ProfileSendRecyclerModel
import com.io.tazarapp.data.model.profile.ScheduleModel
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response

class ScheduleRepository(private var api: RecyclerApi,private var shApi: SharedApi) {

    suspend fun getRecyclerProfile(): Response<ProfileRecyclerModel>? {
        return try {
            api.getRecyclerProfile()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateSchedule(schedule: ScheduleModel, id: Int?): Response<ResponseBody>? {
        return try {
            api.updateSchedule(schedule, id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateRecyclerProfileImage(
        image: MultipartBody.Part
    ): Response<ResponseBody>? {
        return try {
            api.updateRecyclerProfileImage(image)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateRecyclerProfile(
        id: Int, body: ProfileSendRecyclerModel?
    ): Response<ResponseBody>? {
        return try {
            api.updateRecyclerProfile(
                id, body
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun sendCpImage(cpFile: CpFile?): Response<CpImageURl>? {
        return try {
            shApi.sendImage(cpFile?.getFileAsMultipart())
        } catch (e: Exception) {
            null
        }
    }

    suspend fun deleterRegistrationId(registrationId: String?): Response<ResponseBody> ? =
        try {
            api.deleteRegistrationId(registrationId)
        } catch (e: Exception) {
            null
        }
}