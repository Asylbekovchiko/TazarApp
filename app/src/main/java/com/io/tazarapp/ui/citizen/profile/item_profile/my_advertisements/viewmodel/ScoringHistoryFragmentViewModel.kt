package com.io.tazarapp.ui.citizen.profile.item_profile.my_advertisements.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.io.tazarapp.data.repository.citizen.MyAdvertisementRepository
import com.io.tazarapp.data.state.State
import com.io.tazarapp.utils.DEFAULT_NO_INTERNER_INTEGER
import com.io.tazarapp.utils.DEFAUL_INTEGER
import com.io.tazarapp.utils.hasInternet
import kotlinx.coroutines.launch

class ScoringHistoryFragmentViewModel(private val repo : MyAdvertisementRepository) : ViewModel() {
    private val _state: MutableLiveData<State> = MutableLiveData()
    val state: LiveData<State> = _state

    fun getAllUserScoringHistory(page: Int) {
        viewModelScope.launch {
            if (hasInternet()) {
                _state.value = State.LoadingState(true)
                val response = repo.getAllUserScoringHistory(page)
                when {
                    response == null -> {
                        _state.value = State.ErrorState("", DEFAUL_INTEGER)
                    }
                    response.isSuccessful -> {
                        val paging = response.body()
                        if (paging != null) {
                            _state.value = State.SuccessObjectState(paging)
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
}