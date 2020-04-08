package com.kiryanov.githubapp

import android.app.Application
import com.kiryanov.githubapp.di.createMainModule
import com.kiryanov.githubapp.di.createNetworkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(listOf(
                createNetworkModule(
                    getString(R.string.base_url),
                    getString(R.string.api_token)
                ),
                createMainModule(this@App)
            ))
        }
    }
}