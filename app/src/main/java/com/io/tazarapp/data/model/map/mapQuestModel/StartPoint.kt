package com.io.tazarapp.data.model.map.mapQuestModel

import com.google.gson.annotations.SerializedName


data class StartPoint (
	@SerializedName("lng") val lng : Double,
	@SerializedName("lat") val lat : Double
)