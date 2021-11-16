package com.io.tazarapp.data.model.profile

import com.io.tazarapp.data.model.ad.AdsMap
import com.io.tazarapp.data.model.ad.ScheduleItem
import com.io.tazarapp.data.model.filter.FilterSubcategoryModel
import java.io.Serializable

data class ProfileRecyclerModel(
    val id: Int,
    val title: String?,
    val description: String?,
    val contact: String?,
    val phone: String?,
    val city: String?,
    val city_id: Int?,
    val user_level: String?,
    val bottom_line: Int,
    val upper_line: Int,
    val experience: Int,
    val point_image: String?,
    val image: String?,
    val raw_calculation: Int?,
    val subcategory: ArrayList<FilterSubcategoryModel> = arrayListOf(),
    val user_schedule: ArrayList<ScheduleItem>? = arrayListOf(),
    val collection_map: ArrayList<CollectionMap?> = arrayListOf(),
    val processor_address: ArrayList<AdsMap>? = arrayListOf()
) : Serializable

data class CollectionMap(
    val address: String,
    val coords: Coords
)

data class Coords(
    val type: String,
    val coordinates: ArrayList<Double> = arrayListOf()
)