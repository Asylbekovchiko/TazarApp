package com.io.tazarapp.data.repository.filter

import com.io.tazarapp.data.api.SharedApi
import com.io.tazarapp.data.model.filter.FilterCategoryDetailModel
import com.io.tazarapp.data.model.filter.FilterCategoryModel
import okhttp3.ResponseBody
import retrofit2.Response

class FilterRepository (private val api : SharedApi) {

    suspend fun getCategoryList() : Response<ArrayList<FilterCategoryModel>>? {
        return try {
            api.getFilterCategoryList()
        }catch (e : Exception) {
            null
        }
    }

    suspend fun getCategoryDetail(id : Int) : Response<FilterCategoryDetailModel>? {
        return try {
            api.getFilterCategoryDetail(id)
        }catch (e : Exception) {
            null
        }
    }
    suspend fun postUserType(id: Int, subcategory: ArrayList<Int>): Response<ResponseBody>? {
        return try {
            api.postUserType(id, subcategory)
        } catch (e: Exception) {
            null
        }
    }
}