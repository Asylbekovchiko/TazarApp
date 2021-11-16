package com.io.tazarapp.data.model.auth

data class AuthResponseModel(
    var token: String,
    var user_type: String,
    var phone: String
)
