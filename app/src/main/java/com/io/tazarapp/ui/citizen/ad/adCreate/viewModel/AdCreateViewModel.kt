package com.io.tazarapp.ui.citizen.ad.adCreate.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.io.tazarapp.data.model.ad.AdFile
import com.io.tazarapp.data.model.ad.AdModel
import com.io.tazarapp.data.repository.citizen.AdRepository
import com.io.tazarapp.data.model.collectionplace.CollectionPointModel
import com.io.tazarapp.data.model.cp.CpFile
import com.io.tazarapp.data.model.cp.CpImageURl
import com.io.tazarapp.data.state.State
import com.io.tazarapp.utils.DEFAULT_NO_INTERNER_INTEGER
import com.io.tazarapp.utils.DEFAUL_INTEGER
import com.io.tazarapp.utils.hasInternet
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class AdCreateViewModel(private val repo: AdRepository) : ViewModel() {
    private val _state: MutableLiveData<State> = MutableLiveData()
    val state: LiveData<State> = _state

    fun createAd(adModel: AdModel) {
        viewModelScope.launch {
            if (hasInternet()) {
                _state.value = State.LoadingState(true)
                val response = repo.createAd(adModel)
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

    fun sendImage(file: AdFile?, id: Int, action: String) {
        val idPart = id.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val action = action.toRequestBody("text/plain".toMediaTypeOrNull())
        viewModelScope.launch {
            if (hasInternet()) {
                _state.value = State.LoadingState(true)
                val response = repo.sendAdImage(file, idPart, action)
                when {
                    response == null -> {
                        _state.value = State.ErrorState("", DEFAUL_INTEGER)
                    }
                    response.isSuccessful -> {
                        _state.value = State.SuccessObjectState(response.body())
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

    fun getCities() {
        viewModelScope.launch {
            if (hasInternet()) {
                _state.value = State.LoadingState(true)
                val response = repo.getCitiesList()
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

    fun createCP(cpModel: CollectionPointModel) {
        viewModelScope.launch {
            if (hasInternet()) {
                _state.value = State.LoadingState(true)
                val response = repo.createCP(cpModel)
                when {
                    response == null -> {
                        _state.value = State.ErrorState("", DEFAUL_INTEGER)
                    }
                    response.isSuccessful -> {
                        _state.value =
                            State.SuccessObjectState((response.body() as CollectionPointModel))
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

    fun sendCp(file: CpFile?) {
        viewModelScope.launch {
            if (hasInternet()) {
                _state.value = State.LoadingState(true)
                val response = repo.sendCpImage(file)
                when {
                    response == null -> {
                        _state.value = State.ErrorState("", DEFAUL_INTEGER)
                    }
                    response.isSuccessful -> {
                        _state.value =
                                State.SuccessObjectState((response.body() as CpImageURl))
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