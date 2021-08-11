package com.tmobile.mytmobile.echolocate.lte.utils.logcat

import android.annotation.SuppressLint
import java.io.IOException

/*
This is class is responsible to provide the runtime time instance,that will be further given to logcat reader to read the logs
 */
class Shell {
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: Shell? = null

        private var runtime: Runtime? = null

        /**
         * creates [Shell] instance
         */
        fun getInstance(): Shell {
            return INSTANCE
                ?: synchronized(this) {
                val instance =
                    Shell()
                INSTANCE = instance
                runtime = Runtime.getRuntime()
                instance
            }
        }
    }


    /**
     * @param command to execute
     * @return Process from executed command
     * @throws IOException
     */
    @Throws(IOException::class)
    fun exec(command: Array<String>): Process {
        return runtime!!.exec(command)
    }
}