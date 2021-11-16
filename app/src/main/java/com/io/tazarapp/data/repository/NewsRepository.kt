package com.io.tazarapp.data.repository

import com.io.tazarapp.data.api.SharedApi
import com.io.tazarapp.data.model.Paging
import com.io.tazarapp.data.model.news.NewsDetailModel
import com.io.tazarapp.data.model.news.NewsModel
import retrofit2.Response

class NewsRepository(private val api : SharedApi) {

    suspend fun getNewsList(page: Int): Response<Paging<NewsModel>>? {
        return try {
            api.getNewsList(page)
        } catch (e : Exception) {
            null
        }
    }

    suspend fun getNewsDetail(id : Int) : Response<NewsDetailModel>? {
        return try {
            api.getNewsDetail(id)
        } catch (e : Exception) {
            null
        }
    }
}