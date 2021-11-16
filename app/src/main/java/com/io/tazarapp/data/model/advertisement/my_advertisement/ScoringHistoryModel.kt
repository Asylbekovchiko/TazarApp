package com.io.tazarapp.data.model.advertisement.my_advertisement

import com.io.tazarapp.data.model.advertisement.SubcategoryAd

data class ScoringHistoryModel(
    val address: String,
    val date_created: String,
    val present: String?,
    val present_sender: String?,
    val quantity: Int,
    val sender: String?,
    val status: Int,
    val status_name: String,
    val subcategory: ArrayList<SubcategoryAd>?
)