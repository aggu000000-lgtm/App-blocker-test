package com.sharma.focusblocker

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FocusBlockerApp : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
