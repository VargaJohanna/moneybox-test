package com.example.minimoneybox

import android.app.Application
import com.example.minimoneybox.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

class MyApplication : Application() {
    lateinit var koinApplication: KoinApplication
        private set

    override fun onCreate() {
        super.onCreate()
        koinApplication = startKoin {
            androidContext(this@MyApplication)
            modules(repositoryModule, networkModule, viewModelModule, schedulerModule, testModule)
        }
    }
}