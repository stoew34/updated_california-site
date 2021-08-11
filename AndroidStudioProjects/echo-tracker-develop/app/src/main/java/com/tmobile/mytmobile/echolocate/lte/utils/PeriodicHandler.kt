package com.tmobile.mytmobile.echolocate.lte.utils

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper

/**
 * This class is responsible to create the singleton instance of a handler to run
 * the scheduled tasks
 */
class PeriodicHandler : Handler(Looper.getMainLooper()) {

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: PeriodicHandler? = null

        fun getInstance(): PeriodicHandler {
            return INSTANCE ?: synchronized(this) {
                val instance = PeriodicHandler()
                INSTANCE = instance
                instance
            }
        }
    }


}