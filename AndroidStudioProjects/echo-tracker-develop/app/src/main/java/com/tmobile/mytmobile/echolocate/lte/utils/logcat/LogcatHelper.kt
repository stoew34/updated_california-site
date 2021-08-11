package com.tmobile.mytmobile.echolocate.lte.utils.logcat

import android.annotation.SuppressLint
import java.io.IOException

/*
This is class is responsible to execute the shell
 */
class LogcatHelper {


    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: LogcatHelper? = null

        /***
         * creates [LogcatHelper] instance
         */
        fun getInstance(): LogcatHelper {
            return INSTANCE
                ?: synchronized(this) {
                val instance =
                    LogcatHelper()
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * Retrieves logCat process.
     *
     * @return LogCat process.
     * @throws IOException If the requested program can not be executed.
     */
    @Throws(IOException::class)
    fun getLogcatProcess(shell: Shell): Process {
        val args = arrayOf("logcat", "-v", "year")
        return shell.exec(args)
    }

    /**
     * Clear logcat buffer
     *
     * @param shell runtime executor
     * @throws IOException If the requested program can not be executed.
     */
    @Throws(IOException::class)
    fun clearLogcat(shell: Shell) {
        val args = arrayOf("logcat", "-c")
        shell.exec(args)
    }
}