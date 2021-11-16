package com.io.tazarapp.ui.recycler.qrcode.found

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.io.tazarapp.data.model.advertisement.SubcategoryAd
import com.io.tazarapp.data.model.advertisement.user_advertisement.NewAdsForPut
import com.io.tazarapp.data.model.advertisement.user_advertisement.OwnedScoreModel
import com.io.tazarapp.data.repository.recycler.FoundRepository
import com.io.tazarapp.data.state.State
import com.io.tazarapp.utils.DEFAULT_NO_INTERNER_INTEGER
import com.io.tazarapp.utils.DEFAUL_INTEGER
import com.io.tazarapp.utils.hasInternet
import kotlinx.coroutines.launch

class FoundViewModel(private val repo : FoundRepository) : ViewModel() {
    private val _state: MutableLiveData<State> = MutableLiveData()
    val state: LiveData<State> = _state

    fun getUserAdvertisement(qrToken : String) {
        viewModelScope.launch {
            if (hasInternet()) {
                _state.value = State.LoadingState(true)
                val response = repo.getCitizenAdsByQr(qrToken)
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

    fun putSubcategoryAds(adsId : Int,citizenId: Int,ads : ArrayList<SubcategoryAd>)  {
        viewModelScope.launch {
            if (hasInternet()) {
                _state.value = State.LoadingState(true)
                val response = repo.putSubcategoryAds(adsId, NewAdsForPut(citizenId,ads))
                when {
                    response == null -> {
                        _state.value = State.ErrorState("", DEFAUL_INTEGER)
                    }
                    response.isSuccessful -> {
                        val score = response.body()!!
                        _state.value = State.SuccessObjectState(score)
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