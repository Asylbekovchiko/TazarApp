package com.io.tazarapp.data.model.news

data class NewsDetailModel (
    val id : Int,
    val image : String?,
    val title : String,
    var created_at : String,
    val description : String
)