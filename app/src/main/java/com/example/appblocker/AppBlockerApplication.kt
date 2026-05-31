package com.example.appblocker

import android.app.Application

class AppBlockerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CrashReporter.init()
    }
}
