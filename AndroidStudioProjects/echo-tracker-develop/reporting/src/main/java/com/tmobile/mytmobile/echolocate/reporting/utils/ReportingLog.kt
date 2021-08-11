package com.tmobile.mytmobile.echolocate.reporting.utils

import android.content.Context
import com.tmobile.pr.androidcommon.log.TmoLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

/**
 * Created by Divya Mittal on 5/19/21
 */
/**
 * ReportingLog is a wrapper class over the common TmoLog class
 * Any logging API specific to EchoLocate App has to be added in this class
 * This will require API changes when there is a change in underlying logging library
 */

internal class ReportingLog {
    companion object {

        internal val TAG = ReportingLog::class.java.name

        private const val FILE_NAME_APP_LOGS = "log_dia_reporting.txt"
        var stackIndex = 0

        /**
         * EchoLocate Debug Log message
         */
        fun eLogD(msg: String, timeInterval: Long, context: Context) {
            try {
                if (ReportingModuleSharedPrefs.clientAppBuildTypeDebug) {
                    TmoLog.d(getCompleteLogMsg(msg), timeInterval)
                    writeLogToFile(msg, context)
                }
            } catch (e: Exception) {
                //log failing for convert reason
            }
        }

        /**
         * EchoLocate Debug Log message with timestamp as current time.
         */
        fun eLogD(msg: String, context: Context) {
            if (ReportingModuleSharedPrefs.clientAppBuildTypeDebug)
                eLogD(msg, System.currentTimeMillis(), context)
        }

        /**
         * EchoLocate Error Log message
         */
        fun eLogE(msg: String) {
            TmoLog.e(getCompleteLogMsg(msg))
        }

        /**
         * EchoLocate Information Log message
         */
        fun eLogI(msg: String) {
            TmoLog.i(getCompleteLogMsg(msg))
        }

        /**
         * EchoLocate Verbose Log message
         */
        fun eLogV(msg: String, context: Context) {
            if (ReportingModuleSharedPrefs.clientAppBuildTypeDebug) {
                TmoLog.v(getCompleteLogMsg(msg))
                writeLogToFile(msg, context)
            }
        }

        /**
         * This method returns the log path to be displayed with actual log message
         */

        private fun getLogPath(): String {
            try {
                val stackTraceElement = getCallingStackTraceElement()
                var className = stackTraceElement.className
                val index = className.lastIndexOf(".")
                if (index != -1) {
                    className = className.substring(index + 1)
                }

                return String.format(
                    Locale.US,
                    "%s.%s:%d:%s ",
                    className,
                    stackTraceElement.methodName,
                    stackTraceElement.lineNumber,
                    Thread.currentThread().name
                )
            } catch (var4: Exception) {
                return ""
            }
        }

        /**
         * Helper method to obtain the complete log path
         */

        private fun getCallingStackTraceElement(): StackTraceElement {
            if (stackIndex != 0) {
                val stackElement = Thread.currentThread().stackTrace
                return stackElement[stackIndex]
            } else {
                var foundClassLogcat = false
                stackIndex = 0
                val stackTraceElements = Thread.currentThread().stackTrace
                val var3 = stackTraceElements.size

                for (var4 in 0 until var3) {
                    val stackTraceElement = stackTraceElements[var4]
                    val found =
                        stackTraceElement.className.startsWith(ReportingLog::class.java.name)
                    foundClassLogcat = foundClassLogcat || found
                    if (foundClassLogcat && !found) {
                        return stackTraceElement
                    }
                    stackIndex += 1
                }
                return StackTraceElement(
                    ReportingLog::class.java.name,
                    "getCallingStackTraceElement",
                    null as String?,
                    -1
                )
            }
        }

        private fun getCompleteLogMsg(msg: String): String {
            return getLogPath() + msg
        }

        private fun writeLogToFile(msg: String, context: Context) {
            GlobalScope.launch(Dispatchers.IO) {
                val updatedMsg = ReportingDateUtils.getTriggerTimeStamp() + msg + "\n"
                ReportingFileUtils.saveFileToExternalStorage(
                    updatedMsg, FILE_NAME_APP_LOGS, true, context
                )
            }
        }
    }
}