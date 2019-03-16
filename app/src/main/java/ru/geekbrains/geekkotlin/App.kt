package ru.geekbrains.geekkotlin

import android.support.multidex.MultiDexApplication
import com.github.ajalt.timberkt.Timber

class App : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(timber.log.Timber.DebugTree())
    }
}