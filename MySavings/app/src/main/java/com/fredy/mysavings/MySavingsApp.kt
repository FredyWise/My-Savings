package com.fredy.mysavings

import android.app.Application
//import com.fredy.mysavings.DI.AppModule
//import com.fredy.mysavings.DI.AppModuleImpl
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MySavingsApp: Application()
//{
//    companion object {
//        lateinit var appModule: AppModule
//    }
//    override fun onCreate() {
//        super.onCreate()
//        appModule = AppModuleImpl(this)
//    }
//}