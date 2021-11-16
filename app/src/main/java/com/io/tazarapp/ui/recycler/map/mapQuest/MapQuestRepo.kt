package com.io.tazarapp.ui.recycler.map.mapQuest

import com.io.tazarapp.data.api.MapApi
import com.io.tazarapp.data.model.map.mapQuestModel.MapClassModel
import retrofit2.Response

class MapQuestRepo(private var api: MapApi) {

    suspend fun getTestMapData(
        from: String,
        to: String
    ): Response<MapClassModel>? {
        return try {
            api.getTestMapData(
                "MycLjS5Rw5scCLvptgK9zBB0WXDn1rxy",
                from,
                to,
                "json",
                "ignore",
                "fastest",
                doReverseGeocode = false,
                enhancedNarrative = false,
                avoidTimedConditions = false,
                fullShape = true
            )
        } catch (e: Exception) {
            null
        }
    }

}