package com.io.tazarapp.data.model.advertisement

data class SubcategoryAd(
    var corrected_weight: Int,
    val id: Int,
    val color : String,
    val image : String,
    val inner_image: String?,
    var is_delete: Boolean,
    val name: String,
    val subcategory: Int,
    val value: Int
)