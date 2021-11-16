package com.io.tazarapp.data.model.advertisement.user_advertisement

data class UserAdvertisementModel(
    val advertisement: ArrayList<Advertisement>,
    val id: Int,
    val image: String,
    val name: String,
    val phone: String
)