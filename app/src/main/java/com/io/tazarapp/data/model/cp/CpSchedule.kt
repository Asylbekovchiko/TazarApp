package com.io.tazarapp.data.model.cp

data class CpSchedule(
    val day_of_week: String,
    var expires_at_time: String,
    val id: Int,
    val is_weekend: Boolean,
    val name: Int,
    var start_at_time: String
)