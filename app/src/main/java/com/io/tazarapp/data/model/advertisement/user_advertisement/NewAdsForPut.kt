package com.io.tazarapp.data.model.advertisement.user_advertisement

import com.io.tazarapp.data.model.advertisement.SubcategoryAd

data class NewAdsForPut(
    val id : Int,
    val subcategory_ads : ArrayList<SubcategoryAd>
)