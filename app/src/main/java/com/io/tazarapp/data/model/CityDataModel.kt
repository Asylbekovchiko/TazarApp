package com.io.tazarapp.data.model

import java.io.Serializable

data class CityDataModel(
    var address: ArrayList<String?> = arrayListOf(),
    var location: ArrayList<String?> = arrayListOf(),
    var city: ArrayList<String?> = arrayListOf(),
    var isNew: Boolean = false
) : Serializable {
    fun clearData() {
        address = arrayListOf()
        location = arrayListOf()
        city = arrayListOf()
        isNew = false
    }
}