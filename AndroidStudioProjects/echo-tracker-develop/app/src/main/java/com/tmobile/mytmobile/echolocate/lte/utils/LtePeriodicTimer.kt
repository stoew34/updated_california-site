package com.tmobile.mytmobile.echolocate.lte.utils

import android.annotation.SuppressLint
import java.util.*

/*
This class is responsible to provide the timer instance,which is used to run the periodic intervels
 */
class LtePeriodicTimer {
    /**
     * Timer used in periodic trigger logic
     */
    private val mTimer = Timer()

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: LtePeriodicTimer? = null

        fun getInstance(): LtePeriodicTimer {
            return INSTANCE ?: synchronized(this) {
                val instance = LtePeriodicTimer()
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * Returns the current timer object
     */
    fun getTimer(): Timer {
        return mTimer
    }
}