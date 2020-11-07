package com.edu.mvvmtutorial.ui.main

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.edu.mvvmtutorial.ui.callbacks.AppLifecycleObserver

class PQApp : Application() {

    lateinit var appLifecycleObserver: AppLifecycleObserver

    override fun onCreate() {
        super.onCreate()

        /*DaggerAppComponent
            .builder()
            .application(this)
            .build()
            .inject(this)*/
        appLifecycleObserver = AppLifecycleObserver(this)

        ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver)
    }
}