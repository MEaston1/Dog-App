package com.measton.dogapp

import android.app.Application
import com.measton.dogapp.di.appModule
import com.measton.dogapp.network.DEBUG_BUILD_PROPERTY
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class DogApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@DogApp)
            properties(mapOf(DEBUG_BUILD_PROPERTY to BuildConfig.DEBUG))
            modules(appModule)
        }
    }
}
