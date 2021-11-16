package com.io.tazarapp.ui.auth.login.signUp

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.io.tazarapp.data.repository.LoginRepository
import com.io.tazarapp.data.state.State
import com.io.tazarapp.utils.DEFAULT_NO_INTERNER_INTEGER
import com.io.tazarapp.utils.DEFAUL_INTEGER
import com.io.tazarapp.utils.gsm.FCMTokenUtils
import com.io.tazarapp.utils.hasInternet
import kotlinx.coroutines.launch


class SignUpViewModel constructor(private val repo: LoginRepository) : ViewModel() {
    private val _state: MutableLiveData<State> = MutableLiveData()
    val state: LiveData<State> = _state

    fun postAuthData(
        phoneNumber: String,
        userType: String,
        city: Int,
        idToken: String
    ) {
        viewModelScope.launch {
            _state.value = State.LoadingState(true)
            val response = repo.postAuthUser(phoneNumber, userType, city, idToken)
            when {
                response == null -> {
                    _state.value = State.ErrorState("", DEFAUL_INTEGER)
                }
                response.isSuccessful -> {
                    if (response.body() != null) {
                        _state.value =
                            State.SuccessObjectState(response.body()!!)
                    } else {
                        _state.value = State.NoItemState
                    }
                }
                else -> {
                }
            }
            _state.value = State.LoadingState(false)

        }
    }

    fun getAuthData(phone: String, idToken: String) {
        viewModelScope.launch {
            if (hasInternet()) {
                _state.value = State.LoadingState(true)
                val response = repo.getAuthUser(phone, idToken)
                when {
                    response == null -> {
                        _state.value = State.ErrorState("", DEFAUL_INTEGER)
                    }
                    response.isSuccessful -> {
                        if (response.body() != null) {
                            _state.value =
                                State.SuccessObjectState(response.body()!!)
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

    fun checkUser(phone: String) {
        viewModelScope.launch {
            if (hasInternet()) {
                _state.value = State.LoadingState(true)
                val response = repo.checkUser(phone)
                when {
                    response == null -> {
                        _state.value = State.ErrorState("", DEFAUL_INTEGER)
                    }
                    response.isSuccessful -> {
                        if (response.body() != null) {
                            _state.value =
                                State.SuccessObjectState(response.body()!!)
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

    fun saveDeviceId() {
        viewModelScope.launch {
            if (hasInternet()) {
                _state.value = State.LoadingState(true)
                val newDeviceId = FCMTokenUtils.getTokenFromPrefs()
                val response = repo.saveDeviceId(newDeviceId, "android")
                when {
                    response == null -> {
                        _state.value = State.ErrorState("", DEFAUL_INTEGER)
                    }
                    response.isSuccessful -> {
                        FCMTokenUtils.saveToken(newDeviceId)
                        _state.value = State.NoItemState
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