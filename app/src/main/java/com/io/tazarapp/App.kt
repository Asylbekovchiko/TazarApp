package com.io.tazarapp

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.io.tazarapp.modules.repositoryModule
import com.io.tazarapp.modules.viewModelModule
import com.io.tazarapp.modules.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin


class App : Application() {

    companion object {
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(
                listOf(
                    repositoryModule,
                    networkModule,
                    viewModelModule
                )
            )
        }
    }
}