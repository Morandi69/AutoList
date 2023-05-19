package com.example.autolist

import android.app.Application


class AutoIntentApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AutoRepository.initialize(this)
    }
}