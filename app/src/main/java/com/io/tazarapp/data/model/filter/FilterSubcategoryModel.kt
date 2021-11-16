package com.io.tazarapp.data.model.filter

import java.io.Serializable

data class FilterSubcategoryModel (
    val id : Int,
    val name : String,
    val image : String,
    val description : String
) : Serializable