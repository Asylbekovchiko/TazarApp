package com.io.tazarapp.data.model.statistics

data class StatisticData(
    var color: String,
    var total: Int = 0,
    var percent: Float = 0.0F,
    var name: String = ""
    )