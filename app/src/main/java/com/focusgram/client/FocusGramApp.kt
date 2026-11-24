package com.focusgram.client

import android.app.Application
import com.focusgram.client.data.PreferencesRepository
import com.focusgram.client.domain.NetworkMonitor

class FocusGramApp : Application() {

    lateinit var preferencesRepository: PreferencesRepository
        private set

    lateinit var networkMonitor: NetworkMonitor
        private set

    override fun onCreate() {
        super.onCreate()
        preferencesRepository = PreferencesRepository(applicationContext)
        networkMonitor = NetworkMonitor(applicationContext)
    }
}
