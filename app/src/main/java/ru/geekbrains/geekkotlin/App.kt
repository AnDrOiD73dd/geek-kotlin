package ru.geekbrains.geekkotlin

import android.app.Application
import com.github.ajalt.timberkt.Timber
import org.koin.android.ext.android.startKoin
import ru.geekbrains.geekkotlin.di.appModule
import ru.geekbrains.geekkotlin.di.mainModule
import ru.geekbrains.geekkotlin.di.noteModule
import ru.geekbrains.geekkotlin.di.splashModule

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(timber.log.Timber.DebugTree())

        startKoin(this, listOf(appModule, splashModule, mainModule, noteModule))
    }
}