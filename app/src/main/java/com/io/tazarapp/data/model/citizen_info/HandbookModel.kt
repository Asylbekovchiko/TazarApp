package com.io.tazarapp.data.model.citizen_info

data class HandbookModel(
    val id: Int,
    val name: String,
    val type_guide: Int,
    val description: String,
    val image_guide: ArrayList<HandbookInfoModel>
)

data class HandbookInfoModel(
    val description: String,
    val title: String,
    val image: String
)