package com.io.tazarapp.data.model.partners

data class PartnersDetModel(
    val name: String,
    val logo: String,
    val description: String,
    val contacts: String,
    val prize: ArrayList<PartnersDetPrizeModel>
)

data class PartnersDetPrizeModel(
    val name: String,
    val image: String,
    val price: Int
)