package com.tmobile.mytmobile.echolocate.stetho

import com.facebook.stetho.Stetho

class StethoInitializer {

    fun start(context: android.content.Context) {
        Stetho.initializeWithDefaults(context)
    }
}