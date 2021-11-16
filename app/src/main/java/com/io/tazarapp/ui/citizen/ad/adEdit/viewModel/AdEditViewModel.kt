package com.io.tazarapp.ui.citizen.ad.adEdit.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.io.tazarapp.data.model.ad.AdModel
import com.io.tazarapp.data.repository.citizen.AdRepository
import com.io.tazarapp.data.state.State
import com.io.tazarapp.utils.DEFAULT_NO_INTERNER_INTEGER
import com.io.tazarapp.utils.DEFAUL_INTEGER
import com.io.tazarapp.utils.hasInternet
import kotlinx.coroutines.launch

class AdEditViewModel(private val repo: AdRepository) : ViewModel() {

    private val _state: MutableLiveData<State> = MutableLiveData()
    val state: LiveData<State> = _state

    fun getAd(id: Int) {
        viewModelScope.launch {
            if (hasInternet()) {
                _state.value = State.LoadingState(true)
                val response = repo.getAdvertisement(id)
                when {
                    response == null -> {
                        _state.value = State.ErrorState("", DEFAUL_INTEGER)
                    }
                    response.isSuccessful -> {
                        _state.value = State.SuccessObjectState(correctTime((response.body() as AdModel)))
                    }
                    else -> {
                        _state.value =
                            State.ErrorState(response.errorBody()!!.string(), response.code())
                    }
                }
                _state.value = State.LoadingState(false)
            } else {
                _state.value = State.ErrorState("", DEFAULT_NO_INTERNER_INTEGER)
            }
        }
    }

    private fun correctTime(adModel: AdModel): AdModel {
        adModel.schedule.forEach {
            it.start_at_time = it.start_at_time?.dropLast(3)
            it.expires_at_time = it.expires_at_time?.dropLast(3)
        }
        return adModel
    }

    fun updateAd(ad: AdModel) {
        viewModelScope.launch {
            if (hasInternet()) {
                _state.value = State.LoadingState(true)
                val response = repo.updateAd(ad)
                when {
                    response == null -> {
                        _state.value = State.ErrorState("", DEFAUL_INTEGER)
                    }
                    response.isSuccessful -> {
                        _state.value = State.SuccessObjectState((response.body() as AdModel))
                    }
                    else -> {
                        _state.value =
                            State.ErrorState(response.errorBody()!!.string(), response.code())
                    }
                }
                _state.value = State.LoadingState(false)
            } else {
                _state.value = State.ErrorState("", DEFAULT_NO_INTERNER_INTEGER)
            }
        }
    }
}