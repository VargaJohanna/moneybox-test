package com.example.minimoneybox

import android.app.Application
import com.example.minimoneybox.di.networkModule
import com.example.minimoneybox.di.repositoryModule
import com.example.minimoneybox.di.schedulerModule
import com.example.minimoneybox.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            modules(repositoryModule, networkModule, viewModelModule, schedulerModule)
        }
    }
}