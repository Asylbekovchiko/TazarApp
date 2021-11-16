package com.io.tazarapp.data.repository.recycler

import com.io.tazarapp.data.api.RecyclerApi
import com.io.tazarapp.data.model.Paging
import com.io.tazarapp.data.model.history.HistoryModel
import retrofit2.Response

class HistoryRepository(private val api : RecyclerApi) {
    suspend fun getHistories(page: Int) : Response<Paging<HistoryModel>>? {
        return try {
            api.getHistory(page)
        }catch (e : Exception) {
            null
        }
    }
}