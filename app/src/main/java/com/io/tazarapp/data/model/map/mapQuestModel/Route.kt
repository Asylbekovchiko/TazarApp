package com.io.tazarapp.data.model.map.mapQuestModel

import com.google.gson.annotations.SerializedName


data class Route (
	@SerializedName("computedWaypoints") val computedWaypoints : List<String>,
	@SerializedName("formattedTime") val formattedTime : String,
	@SerializedName("sessionId") val sessionId : String,
	@SerializedName("legs") val legs : List<Legs>,
	@SerializedName("shape") val shape: Shape

)