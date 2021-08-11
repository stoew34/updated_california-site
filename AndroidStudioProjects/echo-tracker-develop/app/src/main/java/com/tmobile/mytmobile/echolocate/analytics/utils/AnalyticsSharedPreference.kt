package com.tmobile.mytmobile.echolocate.analytics.utils

import android.content.Context
import android.content.SharedPreferences

/**
 *  shared preferences used to hold crash analytics data
 */
object AnalyticsSharedPreference {

    /**
     * file name in which configuration file is saved
     */
    private const val ANALYTICS_PREF_FILE_NAME = "analyticsSharedPreferences"
    /**
     * specifies mode of the shared preferences
     */
    private const val MODE = Context.MODE_PRIVATE

    /**
     * shared preference variable that used to store/remove shared preference data
     */
    private lateinit var preferences: SharedPreferences

    private val NUM_OF_STARTS_PARAMETER = Pair("numOfStarts", 0L)
    private val NUM_OF_CRASHES_AT_REBOOT_PARAMETER = Pair("numOfCrashesAtReboot", 0L)
    private val NUM_OF_CRASHES_PARAMETER = Pair("numOfCrashes", 0L)
    private val NUM_OF_ANRS_AT_REBOOT_PARAMETER = Pair("numOfAnrsAtReboot", 0L)
    private val NUM_OF_ANRS_PARAMETER = Pair("numOfAnrs", 0L)
    private val NUM_OF_REBOOTS_PARAMETER = Pair("numOfReboots", 0L)
    private val NUM_OF_OS_CRASHES = Pair("numOfOSCrashes", 0L)
    private val TIMESTAMP_OF_APP_START_AFTER_LAST_KILL = Pair("timestampOfAppStartAfterLastKill", "")
    private val DATAMETRICS_SCHEDULER_WORK_ID = Pair("datametrics_workId", 0L)



    /**
     * initialization of the shared preference
     */
    fun init(context: Context) {
        preferences = context.getSharedPreferences(ANALYTICS_PREF_FILE_NAME, MODE)
    }

    /**
     * SharedPreferences extension function, so we won't need to call edit() and apply()
     * ourselves on every SharedPreferences operation.
     */
    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    /**
     * Used to save and get [NUM_OF_STARTS_PARAMETER] in shared preference
     */
    var numOfStarts: Long
        get() = preferences.getLong(NUM_OF_STARTS_PARAMETER.first, NUM_OF_STARTS_PARAMETER.second)
        set(value) = preferences.edit {
            it.putLong(NUM_OF_STARTS_PARAMETER.first, value)
        }

    /**
     * Used to save and get [NUM_OF_CRASHES_PARAMETER] in shared preference
     */
    var numOfCrashes: Long
        get() = preferences.getLong(NUM_OF_CRASHES_PARAMETER.first, NUM_OF_CRASHES_PARAMETER.second)
        set(value) = preferences.edit {
            it.putLong(NUM_OF_CRASHES_PARAMETER.first, value)
        }

    /**
     * Used to save and get [NUM_OF_CRASHES_AT_REBOOT_PARAMETER] in shared preference
     */
    var numOfCrashesAtReboot: Long
        get() = preferences.getLong(NUM_OF_CRASHES_AT_REBOOT_PARAMETER.first, NUM_OF_CRASHES_AT_REBOOT_PARAMETER.second)
        set(value) = preferences.edit {
            it.putLong(NUM_OF_CRASHES_AT_REBOOT_PARAMETER.first, value)
        }

    /**
     * Used to save and get [NUM_OF_ANRS_PARAMETER] in shared preference
     */
    var numOfAnrs: Long
        get() = preferences.getLong(NUM_OF_ANRS_PARAMETER.first, NUM_OF_ANRS_PARAMETER.second)
        set(value) = preferences.edit {
            it.putLong(NUM_OF_ANRS_PARAMETER.first, value)
        }

    /**
     * Used to save and get [NUM_OF_ANRS_AT_REBOOT_PARAMETER] in shared preference
     */
    var numOfAnrsAtReboot: Long
        get() = preferences.getLong(NUM_OF_ANRS_AT_REBOOT_PARAMETER.first, NUM_OF_ANRS_AT_REBOOT_PARAMETER.second)
        set(value) = preferences.edit {
            it.putLong(NUM_OF_ANRS_AT_REBOOT_PARAMETER.first, value)
        }

    /**
     * Used to save and get [NUM_OF_REBOOTS_PARAMETER] in shared preference
     */
    var numOfReboots: Long
        get() = preferences.getLong(NUM_OF_REBOOTS_PARAMETER.first, NUM_OF_REBOOTS_PARAMETER.second)
        set(value) = preferences.edit {
            it.putLong(NUM_OF_REBOOTS_PARAMETER.first, value)
        }

    /**
     * Used to save and get [NUM_OF_OS_CRASHES] in shared preference
     */
    var numOfOSCrashes: Long
        get() = preferences.getLong(NUM_OF_OS_CRASHES.first, NUM_OF_OS_CRASHES.second)
        set(value) = preferences.edit {
            it.putLong(NUM_OF_OS_CRASHES.first, value)
        }

    /**
     * Used to save and get [TIMESTAMP_OF_APP_START_AFTER_LAST_OS_KILL] in shared preference
     */
    var timestampOfAppStartAfterLastKill: String
        get() = preferences.getString(TIMESTAMP_OF_APP_START_AFTER_LAST_KILL.first,
            TIMESTAMP_OF_APP_START_AFTER_LAST_KILL.second) ?: TIMESTAMP_OF_APP_START_AFTER_LAST_KILL.second
        set(value) = preferences.edit {
            it.putString(TIMESTAMP_OF_APP_START_AFTER_LAST_KILL.first, value)
        }

    /**
     * Used to save and get [DATA METRICS SCHEDULER WORKID] in shared preference
     */
    var dataMetricsSchedulerWorkId: Long
        get() = preferences.getLong(
            DATAMETRICS_SCHEDULER_WORK_ID.first, DATAMETRICS_SCHEDULER_WORK_ID.second
        )
        set(value) = preferences.edit {
            it.putLong(DATAMETRICS_SCHEDULER_WORK_ID.first, value)
        }
}