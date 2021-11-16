package com.io.tazarapp.data.model.ad

import java.io.Serializable

data class Coordinates(
    var type: String? = null,
    var coordinates: ArrayList<Double>? = arrayListOf()
) : Serializable