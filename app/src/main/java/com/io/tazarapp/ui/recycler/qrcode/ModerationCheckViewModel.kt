package com.io.tazarapp.ui.recycler.qrcode

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.io.tazarapp.data.model.advertisement.SubcategoryAd
import com.io.tazarapp.data.model.advertisement.user_advertisement.NewAdsForPut
import com.io.tazarapp.data.model.advertisement.user_advertisement.OwnedScoreModel
import com.io.tazarapp.data.repository.recycler.FoundRepository
import com.io.tazarapp.data.repository.recycler.ModerationCheckRepository
import com.io.tazarapp.data.state.State
import com.io.tazarapp.utils.DEFAULT_NO_INTERNER_INTEGER
import com.io.tazarapp.utils.DEFAUL_INTEGER
import com.io.tazarapp.utils.hasInternet
import kotlinx.coroutines.launch

class ModerationCheckViewModel(private val repo: ModerationCheckRepository) : ViewModel() {
    private val _state: MutableLiveData<State> = MutableLiveData()
    val state: LiveData<State> = _state

    fun getModerationStatus() {
        viewModelScope.launch {
            if (hasInternet()) {
                _state.value = State.LoadingState(true)
                val response = repo.getModerationStatus()
                when {
                    response == null -> _state.value = State.ErrorState("", DEFAUL_INTEGER)

                    response.isSuccessful -> {
                        _state.value = State.SuccessObjectState(response.body()!!)
                    }

                    else -> response.errorBody()?.apply {
                        _state.value = State.ErrorState(string(), response.code())
                    }
                }
                _state.value = State.LoadingState(false)
            } else {
                _state.value = State.ErrorState("", DEFAULT_NO_INTERNER_INTEGER)
            }
        }
    }
}