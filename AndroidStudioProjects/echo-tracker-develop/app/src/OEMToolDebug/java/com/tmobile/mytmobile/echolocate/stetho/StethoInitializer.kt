package com.tmobile.mytmobile.echolocate.stetho

import com.facebook.stetho.Stetho

/**
 * Class to initialize Stetho library to access live database for debug versions.
 */
class StethoInitializer {

    /**
     * Method to start the initialization
     */
    fun start(context: android.content.Context) {
        Stetho.initializeWithDefaults(context)
    }
}