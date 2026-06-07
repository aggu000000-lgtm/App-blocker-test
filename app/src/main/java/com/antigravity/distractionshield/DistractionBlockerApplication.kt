package com.antigravity.distractionshield

import android.app.Application
import com.antigravity.distractionshield.di.DependencyProvider

class DistractionBlockerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DependencyProvider.initialize(this)
    }
}
