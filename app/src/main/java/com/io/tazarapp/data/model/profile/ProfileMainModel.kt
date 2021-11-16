package com.io.tazarapp.data.model.profile

data class ProfileMainModel(
    val id: Int,
    val phone: String,
    val image: String?,
    val geolocation: String?,
    val name: String?,
    val city: String,
    val user_level: String?,
    val bottom_line: Int,
    val upper_line: Int,
    val experience: Int,
    val qr_image: String,
    val points: Int?,
    val raw_calculation: Int?
)