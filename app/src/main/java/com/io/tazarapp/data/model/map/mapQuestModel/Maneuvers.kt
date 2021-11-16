package com.io.tazarapp.data.model.map.mapQuestModel

import com.google.gson.annotations.SerializedName


data class Maneuvers (
	@SerializedName("startPoint") val startPoint : StartPoint
)