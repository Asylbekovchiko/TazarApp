package com.io.tazarapp.data.model.ad

import java.io.Serializable

data class SubCategory(
    var id: String? = null,
    var subcategory: Int? = null,
    var name: String? = null,
    var value: Int? = null,
    var corrected_weight: Int? = null,
    var is_delete: Boolean = false
) : Serializable
