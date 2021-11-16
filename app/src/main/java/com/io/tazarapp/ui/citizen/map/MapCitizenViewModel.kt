package com.io.tazarapp.ui.citizen.map

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.io.tazarapp.data.model.auth.CityModel
import com.io.tazarapp.data.model.cp.CP
import com.io.tazarapp.data.model.filter.FilterSubcategoryModel
import com.io.tazarapp.data.repository.citizen.MapCitizenRepository
import com.io.tazarapp.data.state.State
import com.io.tazarapp.utils.DEFAULT_NO_INTERNER_INTEGER
import com.io.tazarapp.utils.DEFAUL_INTEGER
import com.io.tazarapp.utils.hasInternet
import kotlinx.coroutines.launch

class MapCitizenViewModel constructor(private val repo: MapCitizenRepository) :
    ViewModel() {
    private val _state: MutableLiveData<State> = MutableLiveData()
    val state: LiveData<State> = _state
    val TAG = this.javaClass.name

    fun getAds(search: String?, city: CityModel?, parts: ArrayList<FilterSubcategoryModel>) {
        var cityId: Int? = null
        if (city != null) {
            cityId = city.id
        }
        var subcategory: String? = null
        if (parts.size > 0) {
            subcategory = ""
            parts.forEach {
                subcategory += "${it.id},"
            }
            subcategory = subcategory.substring(0, subcategory.length - 1)
        }
        Log.e("SubCATEGORY",subcategory.toString())
        viewModelScope.launch {
            if (hasInternet()) {
                _state.value = State.LoadingState(true)
                val response = repo.getCP(search, cityId, subcategory)
                when {
                    response == null -> {
                        _state.value = State.ErrorState("", DEFAUL_INTEGER)
                    }
                    response.isSuccessful -> {
                        if (response.body()!!.isNotEmpty()) {
                            _state.value =
                                State.SuccessListState<CP>((response.body())!!)
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

            } else {
                _state.value = State.ErrorState("", DEFAULT_NO_INTERNER_INTEGER)

            }
        }
    }

    fun getAdDetail(id: Int) {
        viewModelScope.launch {
            if (hasInternet()) {
                _state.value = State.LoadingState(true)
                val response = repo.getAdDetail(id)
                when {
                    response == null -> {
                        _state.value = State.ErrorState("", DEFAUL_INTEGER)
                    }
                    response.isSuccessful -> {
                        val cp = response.body()!!
                        cutSeconds(cp)
                        _state.value = State.SuccessObjectState<CP>(cp)
                    }
                    else -> {
                        Log.e("$TAG: Error", response.toStrinag())
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

    private fun cutSeconds(cp: CP) {
        cp.cp_schedule.forEach {
            if (!it.is_weekend) {
                it.start_at_time = it.start_at_time.dropLast(3)
                it.expires_at_time = it.expires_at_time.dropLast(3)
            }
        }
    }

    fun getCategoryList(name : String) {
        viewModelScope.launch {
            if (hasInternet()) {
                _state.value = State.LoadingState(true)
                val response = repo.getCategoryList()
                when {
                    response == null -> {
                        _state.value = State.ErrorState("", DEFAUL_INTEGER)
                    }
                    response.isSuccessful -> {
                        val list = response.body()!!
                        list.add(0,FilterSubcategoryModel(
                            -1,
                            name,
                            "",
                            ""
                        ))
                        if (list.isNotEmpty()) {
                            _state.value = State.SuccessListState(list)
                        } else {
                            _state.value = State.NoItemState
                        }
                    }
                    else -> {
                        response.errorBody()?.apply {
                            _state.value = State.ErrorState(string(), response.code())
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