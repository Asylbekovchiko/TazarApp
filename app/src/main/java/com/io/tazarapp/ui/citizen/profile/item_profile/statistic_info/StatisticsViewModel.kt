package com.io.tazarapp.ui.citizen.profile.item_profile.statistic_info

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.io.tazarapp.data.repository.citizen.ProfileRepository
import com.io.tazarapp.data.state.State
import com.io.tazarapp.utils.DEFAULT_NO_INTERNER_INTEGER
import com.io.tazarapp.utils.DEFAUL_INTEGER
import com.io.tazarapp.utils.hasInternet
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class StatisticsViewModel constructor(private val repo: ProfileRepository) :
    ViewModel() {
    private val _state: MutableLiveData<State> = MutableLiveData()
    val state: LiveData<State> = _state

    fun getStatistics(
    ) {
        viewModelScope.launch {
            if (hasInternet()) {
                _state.value = State.LoadingState(true)
                val response = repo.getStatistics()
                when {
                    response == null -> {
                        _state.value = State.ErrorState("", DEFAUL_INTEGER)

                    }
                    response.isSuccessful -> {
                        if (response.body() != null) {
                            _state.value =
                                State.SuccessListState(response.body()!!)
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
}