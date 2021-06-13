package com.begoml.app.presentation

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.begoml.app.di.AppComponent

class ArchKitApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        AppComponent.init(applicationContext)
    }
}
