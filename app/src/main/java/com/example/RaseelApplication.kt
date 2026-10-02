package com.example

import android.app.Application
import com.example.core.di.AppContainer

class RaseelApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        if (container.config.isConfigured() && container.authRepository.isLoggedIn()) {
            container.realtime.connect()
        }
    }
}
