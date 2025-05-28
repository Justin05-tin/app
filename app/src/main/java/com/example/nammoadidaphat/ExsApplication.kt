package com.example.nammoadidaphat

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class ExsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber for better logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
} 