package com.tmobile.mytmobile.echolocate.lte.utils.logcat

import android.annotation.SuppressLint
import com.tmobile.mytmobile.echolocate.utils.FileUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

/*
This class is responsible to read the logcat using logcat helper class
It provides the functions to read the logcat line,and restart if need again
 */
class LogcatReader {

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: LogcatReader? = null

        private var logcatHelper = LogcatHelper.getInstance()

        /***
         * creates [LogcatReader] instance
         */
        fun getInstance(): LogcatReader {
            return INSTANCE
                ?: synchronized(this) {
                val instance =
                    LogcatReader()
                INSTANCE = instance

                instance.clearLogcat(Shell.getInstance())
                instance
            }
        }
    }

    private var logcatProcess: Process? = null
    private val BUFFER_SIZE = 8192

    /**
     * Buffer to read info from process.
     */
    private var bufferedReader: BufferedReader? = null

    /**
     * Reads line from the buffer.
     *
     * @return New log line.
     * @throws IOException          If this reader is closed or some other I/O error occurs.
     * @throws NullPointerException If reader is destroyed by destroy method [destroy]
     */
    @Throws(IOException::class, NullPointerException::class)
    fun readLine(): String? {
        var line: String? = bufferedReader?.readLine()
        // Check if LogCat process was restarted on the system level
        if (line == null) {
            restartLogcatProcess(Shell.getInstance())
            line = bufferedReader?.readLine()
        }
        
        return line
    }

    /**
     * Destroys the existing instance of logcat process if any and initialize new process.
     * @param shell
     * @throws IOException
     */
    @Throws(IOException::class)
    fun restartLogcatProcess(shell: Shell) {
        destroy()
        logcatProcess = logcatHelper.getLogcatProcess(shell)
        bufferedReader = BufferedReader(
            InputStreamReader(logcatProcess?.inputStream, "UTF-8"), BUFFER_SIZE)
    }

    fun clearLogcat(shell: Shell): LogcatReader {
        logcatHelper.clearLogcat(shell)

        return this
    }

    /**
     * Destroys the reader.
     */
    fun destroy() {
        if (logcatProcess != null) {
            logcatProcess?.destroy()
        }
        FileUtils.closeSafely(bufferedReader)
        bufferedReader?.close()
        bufferedReader = null
    }

}