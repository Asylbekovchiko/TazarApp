package com.io.tazarapp.data.model.profile

import java.io.Serializable

data class ProfileSendRecyclerModel(
    val title: String?,
    val description: String?,
    val city: String?,
    val point_image: String? = null,
    val contact: String?,
    val collection_map: CollectionMapSend?
) : Serializable

data class CollectionMapSend(
    var address: String,
    var coords: CoordsSend
)

data class CoordsSend(
    val type: String,
    var coordinates: ArrayList<Double> = arrayListOf()
)