package com.io.tazarapp.data.model.auth

import java.io.Serializable

data class CityModel(
    val name: String,
    val id: Int,
    var geolocation: String? = null

) : Serializable