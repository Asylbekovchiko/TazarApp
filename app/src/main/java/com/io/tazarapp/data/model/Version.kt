package com.io.tazarapp.data.model

import java.io.Serializable

data class Version(
    val version: String,
    val force_update: Boolean
) : Serializable
