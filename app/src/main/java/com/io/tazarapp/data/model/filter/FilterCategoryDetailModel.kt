package com.io.tazarapp.data.model.filter

import java.io.Serializable

data class FilterCategoryDetailModel (
    val id : Int,
    val name : String,
    val subcategory : ArrayList<FilterSubcategoryModel>
) : Serializable