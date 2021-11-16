package com.io.tazarapp.data.model.map

data class Schedule(
    var expires_at_time: String?,
    val id: Int,
    val is_weekend: Boolean,
    val name: Int,
    var start_at_time: String?
)