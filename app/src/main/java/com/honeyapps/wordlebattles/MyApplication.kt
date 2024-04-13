package com.honeyapps.wordlebattles


import android.app.Application
import com.honeyapps.wordlebattles.di.KoinModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
//import com.google.firebase.FirebaseApp
//import com.google.firebase.appcheck.FirebaseAppCheck
//import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
//import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // initialize Koin
        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(KoinModules.modules)
        }
    }

    // initialize Firebase App Check
//        FirebaseApp.initializeApp(this)
//        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
////            // debug
//            if (dotenv.get("APP_CHECK_DEBUG_TOKEN", null) != null) {
//                Log.i("FirebaseAppCheck", "Using DebugAppCheckProviderFactory")
//                DebugAppCheckProviderFactory.getInstance()
////            // release
//            } else
//                PlayIntegrityAppCheckProviderFactory.getInstance(), true
//        )
//    }

    // runs on emulators only
    override fun onTerminate() {
        super.onTerminate()
        stopKoin()
    }
}