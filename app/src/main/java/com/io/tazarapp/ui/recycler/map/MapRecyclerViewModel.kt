package com.io.tazarapp.ui.recycler.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.io.tazarapp.data.model.map.Ad
import com.io.tazarapp.data.model.rating.Makala
import com.io.tazarapp.data.repository.recycler.MapRecyclerRepository
import com.io.tazarapp.data.state.State
import com.io.tazarapp.utils.DEFAULT_NO_INTERNER_INTEGER
import com.io.tazarapp.utils.DEFAUL_INTEGER
import com.io.tazarapp.utils.hasInternet
import kotlinx.coroutines.launch

class MapRecyclerViewModel constructor(private val repo: MapRecyclerRepository) :
    ViewModel() {
    private val _state: MutableLiveData<State> = MutableLiveData()
    val state: LiveData<State> = _state
    val TAG = this.javaClass.name

    fun getAds() {
        viewModelScope.launch {
            if (hasInternet()) {
                _state.value = State.LoadingState(true)
                val response = repo.getAds()
                when {
                    response == null -> {
                        _state.value = State.ErrorState("", DEFAUL_INTEGER)
                    }
                    response.isSuccessful -> {
                        if (response.body()!!.isNotEmpty()) {
                            _state.value =
                                State.SuccessListState<Ad>((response.body())!!)
                        } else {
                            _state.value = State.NoItemState
                        }
                    }
                    else -> {
                        Log.e("$TAG: Error", response.toString())
                        if (response.errorBody() != null) {
                            _state.value =
                                State.ErrorState(response.errorBody()!!.string(), response.code())
                        }
                    }
                }
                _state.value = State.LoadingState(false)

            }else{
                _state.value = State.ErrorState("", DEFAULT_NO_INTERNER_INTEGER)

            }
        }
    }

    fun getAdDetail(id:Int) {
        viewModelScope.launch {
            if (hasInternet()) {
                _state.value = State.LoadingState(true)
                val response = repo.getAdDetail(id)
                when {
                    response == null -> {
                        _state.value = State.ErrorState("", DEFAUL_INTEGER)
                    }
                    response.isSuccessful -> {
                        val ad = response.body()!!
                        cutSeconds(ad)
                       _state.value = State.SuccessObjectState<Ad>(response.body()!!)
                    }
                    else -> {
                        Log.e("$TAG: Error", response.toString())
                        if (response.errorBody() != null) {
                            _state.value =
                                State.ErrorState(response.errorBody()!!.string(), response.code())
                        }
                    }
                }
                _state.value = State.LoadingState(false)

            }else{
                _state.value = State.ErrorState("", DEFAULT_NO_INTERNER_INTEGER)

            }
        }
    }

    private fun cutSeconds(ad: Ad) {
        Log.e("sddsa","absdjgbahjebdsghjl")
        ad.schedule.forEach {
            if (!it.is_weekend) {
                it.start_at_time = it.start_at_time?.dropLast(3)
                it.expires_at_time = it.expires_at_time?.dropLast(3)
            }
        }
    }
}