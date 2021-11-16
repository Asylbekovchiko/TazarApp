package com.io.tazarapp.data.model.qr_prize

data class QrPrizeModel(
    var id: Int,
    var partner: String,
    var name: String,
    var image: String,
    var price: Int,
    var user_points: Int
)