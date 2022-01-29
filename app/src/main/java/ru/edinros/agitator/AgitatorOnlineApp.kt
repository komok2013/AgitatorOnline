package ru.edinros.agitator

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber.*
import timber.log.Timber.Forest.plant


@HiltAndroidApp
class AgitatorOnlineApp:Application() {
    override fun onCreate() {
        if (BuildConfig.DEBUG) {
            plant(DebugTree())
        } else {
            //plant(CrashReportingTree())
        }
        super.onCreate()
    }
}