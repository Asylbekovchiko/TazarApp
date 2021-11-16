package com.io.tazarapp.data.model.ad

import java.io.Serializable

data class ScheduleItem(
    var id: Int? = null,
    var name: Int? = null,
//    var daySlug: String? = null,
    var is_weekend: Boolean = false,
    var start_at_time: String? = "09:00",
    var expires_at_time: String? = "18:00"
) : Serializable
