package com.io.tazarapp.data.model.advertisement.my_advertisement

import com.io.tazarapp.data.model.advertisement.AdsMap
import com.io.tazarapp.data.model.advertisement.SubcategoryAd

data class MyAdvertisementModel(
    val ads_map: List<AdsMap>,
    val id: Int,
    val name: String,
    val points: Int,
    val status: Int,
    val status_name: String,
    val subcategory_ads: ArrayList<SubcategoryAd>
)