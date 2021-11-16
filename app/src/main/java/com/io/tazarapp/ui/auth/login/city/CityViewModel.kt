package com.io.tazarapp.ui.auth.login.city

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.io.tazarapp.data.repository.LoginRepository
import com.io.tazarapp.data.state.State
import com.io.tazarapp.utils.DEFAUL_INTEGER
import kotlinx.coroutines.launch

class CityViewModel constructor(private val repo: LoginRepository) : ViewModel() {
    private val _state: MutableLiveData<State> = MutableLiveData()
    val state: LiveData<State> = _state

    fun getCity() {
        viewModelScope.launch {
            _state.value = State.LoadingState(true)
            val response = repo.getCity()
            when {
                response == null -> {
                    _state.value = State.ErrorState("", DEFAUL_INTEGER)
                }
                response.isSuccessful -> {
                    if (response.body()!!.isNotEmpty()) {
                        _state.value =
                            State.SuccessListState(response.body()!!)
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
}