package com.kiryanov.githubapp

import android.app.Application
import com.kiryanov.githubapp.di.createMainModule
import com.kiryanov.githubapp.di.createNetworkModule
import org.koin.android.ext.android.startKoin

class App: Application() {

    override fun onCreate() {
        super.onCreate()



        startKoin(
            this,
            listOf(
                createMainModule(this),
                createNetworkModule(getString(R.string.base_url))
            )
        )
    }
}