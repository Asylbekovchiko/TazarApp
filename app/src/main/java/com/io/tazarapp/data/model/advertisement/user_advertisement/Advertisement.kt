package com.io.tazarapp.data.model.advertisement.user_advertisement

import com.io.tazarapp.data.model.advertisement.AdsMap
import com.io.tazarapp.data.model.advertisement.SubcategoryAd

data class Advertisement(
    val ads_map: List<AdsMap>,
    val id: Int,
    val name: String,
    val status: Int,
    val subcategory_ads: ArrayList<SubcategoryAd>
)