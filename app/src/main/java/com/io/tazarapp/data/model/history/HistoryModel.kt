package com.io.tazarapp.data.model.history

import com.io.tazarapp.data.model.advertisement.SubcategoryAd

data class HistoryModel(
    val address: String,
    val date_created: String,
    val quantity: Int,
    val present_sender: String,
    val status: Int,
    val status_name: String,
    val ads_name: String,
    val subcategory: ArrayList<SubcategoryAd>
)