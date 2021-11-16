package com.io.tazarapp.data.repository.citizen


import com.io.tazarapp.data.api.CitizenApi
import com.io.tazarapp.data.model.auth.CityModel
import com.io.tazarapp.data.model.cp.CP
import com.io.tazarapp.data.model.filter.FilterCategoryModel
import com.io.tazarapp.data.model.filter.FilterSubcategoryModel
import retrofit2.Response

class MapCitizenRepository(private var api: CitizenApi) {

    suspend fun getCP(search:String?, city:Int?, parts:String?): Response<ArrayList<CP>>? {
        return try {
            api.getCp(search,city,parts)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getCategoryList() : Response<ArrayList<FilterSubcategoryModel>>? {
        return try {
            api.getFilterCategoryList()
        }catch (e : Exception) {
            null
        }
    }

    suspend fun getAdDetail(id:Int): Response<CP>? {
        return try {
            api.getCPDetail(id)
        } catch (e: Exception) {
            null
        }
    }

}