package com.io.tazarapp.data.model.cp

import com.io.tazarapp.data.model.advertisement.SubcategoryAd

data class CP(
    val id:Int,
    val city: Int,
    val contact: String,
    val cp_map: List<CpMap>,
    val point_image : String?,
    val cp_schedule: List<CpSchedule>,
    val cp_user: CpUser,
    val description: String,
    val subcategory: List<SubcategoryAd>,
    val title: String
)