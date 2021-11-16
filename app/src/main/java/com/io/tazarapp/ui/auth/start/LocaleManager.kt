package com.io.tazarapp.ui.auth.start

import android.content.Context
import android.content.res.Configuration
import java.util.*

class LocaleManager {

    fun updateResources(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val configuration = Configuration()
        configuration.locale = locale
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)

    }
}