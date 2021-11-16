package com.io.tazarapp.ui.citizen.profile.item_profile.profile_info

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

class ProfileViewModel constructor(private val repo: ProfileRepository) :
    ViewModel() {
    private val _state: MutableLiveData<State> = MutableLiveData()
    val state: LiveData<State> = _state

    fun updateProfileImage(
        image: MultipartBody.Part?
    ) {
        viewModelScope.launch {
            if (hasInternet()) {
                _state.value = State.LoadingState(true)
                val response = repo.updateProfileImage(image)
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

    fun updateProfileInfo(
        id: Int,
        city: Int?,
        name: String
    ) {
        viewModelScope.launch {
            if (hasInternet()) {
                _state.value = State.LoadingState(true)
                val response = repo.updateProfileInfo(id, city, name)
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
}