package com.io.tazarapp.data.model.map.mapQuestModel

import com.google.gson.annotations.SerializedName


data class Legs (
	@SerializedName("maneuvers") val maneuvers : ArrayList<Maneuvers>
)