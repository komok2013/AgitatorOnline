package ru.edinros.agitator

import android.app.Application
import com.chibatching.kotpref.Kotpref
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber.*
import timber.log.Timber.Forest.plant


@HiltAndroidApp
class AgitatorOnlineApp : Application() {
    override fun onCreate() {
        Kotpref.init(this)
        if (BuildConfig.DEBUG) {
            plant(DebugTree())
        } else {
            //plant(CrashReportingTree())
        }
        AndroidThreeTen.init(this)
        super.onCreate()
    }
}