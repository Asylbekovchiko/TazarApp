package com.io.tazarapp.ui.citizen.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.io.tazarapp.data.model.Paging
import com.io.tazarapp.data.model.news.NewsDetailModel
import com.io.tazarapp.data.model.news.NewsModel
import com.io.tazarapp.data.repository.NewsRepository
import com.io.tazarapp.data.state.State
import com.io.tazarapp.utils.DEFAULT_NO_INTERNER_INTEGER
import com.io.tazarapp.utils.DEFAUL_INTEGER
import com.io.tazarapp.utils.hasInternet
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class NewsViewModel(private val repo : NewsRepository) : ViewModel() {

    private val _state: MutableLiveData<State> = MutableLiveData()
    val state: LiveData<State> = _state

    fun getNews(page: Int) {
        viewModelScope.launch {
            if (hasInternet()) {
                val response = repo.getNewsList(page)
                when {
                    response == null -> {
                        _state.value = State.ErrorState("", DEFAUL_INTEGER)
                    }
                    response.isSuccessful -> {
                        if (response.body() != null) {
                            val data = response.body()!!
                            changeDateFormat(data)
                            _state.value =
                                State.SuccessObjectState(data)
                        }
                    }
                    else -> {
                        if (response.errorBody() != null) {
                            _state.value =
                                State.ErrorState(response.errorBody()!!.string(), response.code())
                        }
                    }
                }
            } else {
                _state.value = State.ErrorState("", DEFAULT_NO_INTERNER_INTEGER)
            }
        }
    }

    private fun changeDateFormat(data: Paging<NewsModel>) {
        data.results?.forEach { item ->
            item.created_at = format(item.created_at)
        }
    }

    private fun changeDateFormat(data: NewsDetailModel) {
        data.created_at = format(data.created_at)
    }

    private fun format(date : String) : String {
        val builder = StringBuilder()
        builder.append(date.substring(8,10) + ".")
        builder.append(date.substring(5,7) + ".")
        builder.append(date.substring(0,4) + " ")
        builder.append(date.substring(11,16))
        return builder.toString()
    }

    fun getNewsDetail(id : Int) {
        viewModelScope.launch {
            if (hasInternet()) {
                _state.value = State.LoadingState(true)
                val response = repo.getNewsDetail(id)
                when {
                    response == null -> {
                        _state.value = State.ErrorState("", DEFAUL_INTEGER)
                    }
                    response.isSuccessful -> {
                        if (response.body() != null) {
                            val data = response.body()!!
                            changeDateFormat(data)
                            _state.value =
                                State.SuccessObjectState(data)
                        } else {
                            _state.value = State.NoItemState
                        }
                    }
                    else -> {
                        if (response.errorBody() != null) {
                            _state.value =
                                State.ErrorState(response.errorBody()!!.string(), response.code())
                        }
                    }
                }
                _state.value = State.LoadingState(false)
            } else {
                _state.value = State.ErrorState("", DEFAULT_NO_INTERNER_INTEGER)
            }
        }
    }

    fun setLoading (isLoading : Boolean) {
        _state.value = State.LoadingState(isLoading)
    }
}