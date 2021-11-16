package com.io.tazarapp.data.model.rating


data class CompanyModel(
    val id: Int,
    val title: String,
    val icon: Int,
    val rateList: ArrayList<RateListModel>
)

data class RateListModel(
    val id: Int,
    val lvl: String,
    val name: String,
    val icon: Int,
    val city: String,
    val weight: String
)