package com.io.tazarapp.data.model

import java.io.Serializable

data class City(
    var id: Int? = null,
    var name: String? = null,
    var geolocation: String? = null
) : Serializable
