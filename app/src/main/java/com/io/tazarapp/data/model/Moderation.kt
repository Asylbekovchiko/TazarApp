package com.io.tazarapp.data.model

import java.io.Serializable

data class Moderation(
    var is_moderate: Boolean = false,
    var is_profile_exists: Boolean = false
) : Serializable
