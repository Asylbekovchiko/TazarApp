package com.io.tazarapp.ui.auth.changeNumber

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.io.tazarapp.data.repository.LoginRepository
import com.io.tazarapp.data.state.State
import com.io.tazarapp.utils.DEFAULT_NO_INTERNER_INTEGER
import com.io.tazarapp.utils.DEFAUL_INTEGER
import com.io.tazarapp.utils.hasInternet
import kotlinx.coroutines.launch

class       ChangeNumViewModel constructor(private val repo: LoginRepository) : ViewModel() {
    private val _state: MutableLiveData<State> = MutableLiveData()
    val state: LiveData<State> = _state

    fun changeNumber(phone: String, idToken: String) {
        viewModelScope.launch {
            if (hasInternet()) {
                _state.value = State.LoadingState(true)
                val response = repo.changeNumber(phone, idToken)
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
}