package com.io.tazarapp.data.model.ad

import java.io.Serializable

data class AdsMap(
    var id: Int? = null,
    var address: String? = null,
    var coords: Coordinates? = null
) : Serializable