package com.io.tazarapp.data.model.profile

import com.io.tazarapp.data.model.ad.ScheduleItem
import java.io.Serializable

data class ScheduleModel(
    var user_schedule: ArrayList<ScheduleItem> = arrayListOf()
) : Serializable