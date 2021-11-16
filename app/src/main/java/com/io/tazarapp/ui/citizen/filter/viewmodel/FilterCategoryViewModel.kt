package com.io.tazarapp.ui.citizen.filter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.io.tazarapp.data.repository.filter.FilterRepository
import com.io.tazarapp.data.state.State
import com.io.tazarapp.utils.DEFAULT_NO_INTERNER_INTEGER
import com.io.tazarapp.utils.DEFAUL_INTEGER
import com.io.tazarapp.utils.hasInternet
import kotlinx.coroutines.launch

class FilterCategoryViewModel (private val repo: FilterRepository) : ViewModel() {
    private val _state: MutableLiveData<State> = MutableLiveData()
    val state: LiveData<State> = _state

    fun getCategoryList() {
        viewModelScope.launch {
            if (hasInternet()) {
                _state.value = State.LoadingState(true)
                val response = repo.getCategoryList()
                when {
                    response == null -> {
                        _state.value = State.ErrorState("", DEFAUL_INTEGER)
                    }
                    response.isSuccessful -> {
                        val list = response.body()!!
                        if (list.isNotEmpty()) {
                            _state.value = State.SuccessListState(response.body()!!)
                        }else {
                            _state.value = State.NoItemState
                        }
                    }
                    else -> {
                        response.errorBody()?.apply {
                            _state.value = State.ErrorState(string(),response.code())
                        }
                    }
                }
                _state.value = State.LoadingState(false)
            }else {
                _state.value = State.ErrorState("", DEFAULT_NO_INTERNER_INTEGER)
            }
        }
    }

    fun getCategoryDetail(id : Int) {
        viewModelScope.launch {
            if (hasInternet()) {
                _state.value = State.LoadingState(true)
                val response = repo.getCategoryDetail(id)
                when {
                    response == null -> {
                        _state.value = State.ErrorState("", DEFAUL_INTEGER)
                    }
                    response.isSuccessful -> {
                        _state.value = State.SuccessObjectState(response.body()!!)
                    }
                    else -> {
                        response.errorBody()?.apply {
                            _state.value = State.ErrorState(string(),response.code())
                        }
                    }
                }
                _state.value = State.LoadingState(false)
            }else {
                _state.value = State.ErrorState("", DEFAULT_NO_INTERNER_INTEGER)
            }
        }
    }
    fun postUserType(id: Int, subcategory: ArrayList<Int>) {
        viewModelScope.launch {
            if (hasInternet()) {
                _state.value = State.LoadingState(true)
                val response = repo.postUserType(id, subcategory)
                when {
                    response == null -> {
                        _state.value = State.ErrorState("", DEFAUL_INTEGER)
                    }
                    response.isSuccessful -> {
                        _state.value = State.SuccessObjectState(response.body()!!)
                    }
                    else -> {
                        response.errorBody()?.apply {
                            _state.value = State.ErrorState(string(), response.code())
                        }
                    }
                }
                _state.value = State.LoadingState(false)
            } else {
                _state.value = State.ErrorState("", DEFAULT_NO_INTERNER_INTEGER)
            }
        }
    }
}