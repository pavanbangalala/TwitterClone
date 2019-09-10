package com.ishapps.dev.twitterclone

import android.app.Application
import com.google.firebase.FirebaseApp

class TwitterCloneApp:Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}