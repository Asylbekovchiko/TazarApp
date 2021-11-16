package com.io.tazarapp.data.model.collectionplace


import com.io.tazarapp.data.model.ad.AdsMap
import com.io.tazarapp.data.model.ad.ScheduleItem
import java.io.Serializable

data class CollectionPointModel(
    var title: String? = null,
    var contact: String? = null,
    var point_image: String? = null,
    var cp_schedule: ArrayList<ScheduleItem> = arrayListOf(),
    var subcategory: ArrayList<Int> = arrayListOf(),
    var city: Int? = null,
    var cp_map: ArrayList<AdsMap> = arrayListOf()
) : Serializable {
    fun clearData() {
        title = null
        contact = null
        cp_schedule = arrayListOf()
        subcategory = arrayListOf()
        city = null
        cp_map = arrayListOf()
    }
}
