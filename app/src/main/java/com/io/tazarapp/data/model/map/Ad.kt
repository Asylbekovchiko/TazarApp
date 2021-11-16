package com.io.tazarapp.data.model.map

import com.io.tazarapp.data.model.advertisement.SubcategoryAd
import java.util.ArrayList

data class Ad(
    val ads_map: List<AdsMap>,
    val city: Int,
    val description: String,
    val id: Int,
    val name: String,
    val phone: String,
    val as_company: Boolean,
    val image: String?,
    val user: String?,
    val user_image: String?,
    val user_name: String,
    val schedule: List<Schedule>,
    val subcategory_ads: ArrayList<SubcategoryAd>
)