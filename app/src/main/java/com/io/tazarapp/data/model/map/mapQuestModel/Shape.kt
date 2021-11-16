package com.io.tazarapp.data.model.map.mapQuestModel


import com.google.gson.annotations.SerializedName

data class Shape(
    @SerializedName("shapePoints")
    val shapePoints: List<Double>
)