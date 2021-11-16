package com.io.tazarapp.data.model.ad

import java.io.Serializable

data class AdModel(
    var id: Int? = null,
    var image: String? = null,
    var name: String? = null,
    var phone: String? = null,
    var description: String? = null,
    var as_company: Boolean = false,
    var schedule: ArrayList<ScheduleItem> = arrayListOf(),
    var subcategory_ads: ArrayList<SubCategory> = arrayListOf(),
    var city: Int? = null,
    var category: Int? = null,
    var ads_map: ArrayList<AdsMap> = arrayListOf()
) : Serializable {

    fun clearData() {
        id = null
        image = null
        name = null
        phone = null
        description = null
        as_company = false
        schedule = arrayListOf()
        subcategory_ads = arrayListOf()
        city = null
        ads_map = arrayListOf()
    }

    fun fillData(model: AdModel) {
        id = model.id
        image = model.image
        name = model.name
        phone = model.phone
        description = model.description
        as_company = model.as_company
        schedule = model.schedule
        subcategory_ads = model.subcategory_ads
        city = model.city
        ads_map = model.ads_map
    }
}

