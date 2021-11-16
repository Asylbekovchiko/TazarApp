package com.io.tazarapp.ui.citizen.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.io.tazarapp.data.repository.citizen.ProfileRepository
import com.io.tazarapp.data.state.State
import com.io.tazarapp.utils.DEFAULT_NO_INTERNER_INTEGER
import com.io.tazarapp.utils.DEFAUL_INTEGER
import com.io.tazarapp.utils.gsm.FCMTokenUtils
import com.io.tazarapp.utils.hasInternet
import kotlinx.coroutines.launch

class ProfileMainViewModel constructor(val repo: ProfileRepository) : ViewModel() {
    private val _state: MutableLiveData<State> = MutableLiveData()
    val state: LiveData<State> = _state

    fun getMainProfile() {
        viewModelScope.launch {
            if (hasInternet()) {
                _state.value = State.LoadingState(true)
                val response = repo.getMainProfile()
                when {
                    response == null -> {
                        _state.value = State.ErrorState("", DEFAUL_INTEGER)
                    }
                    response.isSuccessful -> {
                        if (response.body() != null) {
                            _state.value = State.SuccessObjectState(response.body()!!)

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

    fun deleteRegistrationId() {
        viewModelScope.launch {
            if (hasInternet()) {
                _state.value = State.LoadingState(true)
                val registrationId = FCMTokenUtils.getTokenFromPrefs()
                val response = repo.deleterRegistrationId(registrationId!!)
                when {
                    response == null -> {
                        _state.value = State.ErrorState("", DEFAUL_INTEGER)
                    }
                    response.isSuccessful -> {
                        if (response.code() == 204) {
                            _state.value = State.SuccessObjectState(204)
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

