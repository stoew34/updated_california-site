package com.tmobile.mytmobile.echolocate.nr5g.core.utils

import android.content.Context
import android.content.SharedPreferences

/**
 *  shared preferences used to hold configurationd data
 */
object Nr5gSharedPreference {

    /**
     * file name in which configuration file is saved
     */
    private const val NR5G_PREF_FILE_NAME = "nr5gSharedPreferences"

    /**
     * specifies mode of the shared preferences
     */
    private const val MODE = Context.MODE_PRIVATE

    /**
     * shared preference variable that used to store/remove shared preference data
     */
    private lateinit var preferences: SharedPreferences

    // list of app specific preferences
    private val CONFIGURATION_PARAMETER = Pair("configuration", "")
    private val WORKID_PARAMETER = Pair("workId", 0L)
    private val REPORT_SCHEDULER_WORK_ID = Pair("report_workId", 0L)
    private val SCREEN_TRIGGER_LIMIT_PARAMETER = Pair("nr5g_screen_triggerLimit", 200)
    private val SCREEN_TRIGGER_COUNT_PARAMETER = Pair("nr5g_screen_triggerCount", 0)

    private val APP_TRIGGER_LIMIT_PARAMETER = Pair("nr5g_app_triggerLimit", 1200)
    private val APP_TRIGGER_COUNT_PARAMETER = Pair("nr5g_app_triggerCount", 0)

    /**
     * initialization of the shared preference
     */
    fun init(context: Context) {
        preferences = context.getSharedPreferences(
            NR5G_PREF_FILE_NAME,
            MODE
        )
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
     * Used to save and get [CONFIGURATION_PARAMETER] in shared preference
     */
    var configurationObject: String?
        get() = preferences.getString(
            CONFIGURATION_PARAMETER.first, CONFIGURATION_PARAMETER.second
        )
        set(value) = preferences.edit {
            it.putString(CONFIGURATION_PARAMETER.first, value)
        }

    /**
     * Used to save and get [WORKID_PARAMETER] in shared preference
     */
    var scheduledWorkId: Long
        get() = preferences.getLong(
            WORKID_PARAMETER.first, WORKID_PARAMETER.second
        )
        set(value) = preferences.edit {
            it.putLong(WORKID_PARAMETER.first, value)
        }

    /**
     * Used to save and get [REPORT SCHEDULER WORKID] in shared preference
     */

    var reportSchedulerWorkId: Long
        get() = preferences.getLong(
            REPORT_SCHEDULER_WORK_ID.first, REPORT_SCHEDULER_WORK_ID.second
        )
        set(value) = preferences.edit {
            it.putLong(REPORT_SCHEDULER_WORK_ID.first, value)
        }

    /**
     * Used to save and get [SCREEN_TRIGGER_COUNT_PARAMETER] in shared preference
     */
    var screenTriggerCount: Int
        get() = preferences.getInt(
            SCREEN_TRIGGER_COUNT_PARAMETER.first,
            SCREEN_TRIGGER_COUNT_PARAMETER.second
        )
        set(value) = preferences.edit {
            it.putInt(SCREEN_TRIGGER_COUNT_PARAMETER.first, value)
        }

    /**
     * Used to save and get [TRIGGER_LIMIT_PARAMETER] in shared preference
     */
    var screenTriggerLimit: Int
        get() = preferences.getInt(
            SCREEN_TRIGGER_LIMIT_PARAMETER.first,
            SCREEN_TRIGGER_LIMIT_PARAMETER.second
        )
        set(value) = preferences.edit {
            it.putInt(SCREEN_TRIGGER_LIMIT_PARAMETER.first, value)
        }

    /**
     * Used to save and get [APP_TRIGGER_COUNT_PARAMETER] in shared preference
     */
    var appTriggerCount: Int
        get() = preferences.getInt(
            APP_TRIGGER_COUNT_PARAMETER.first,
            APP_TRIGGER_COUNT_PARAMETER.second
        )
        set(value) = preferences.edit {
            it.putInt(APP_TRIGGER_COUNT_PARAMETER.first, value)
        }

    /**
     * Used to save and get [APP_TRIGGER_LIMIT_PARAMETER] in shared preference
     */
    var appTriggerLimit: Int
        get() = preferences.getInt(
            APP_TRIGGER_LIMIT_PARAMETER.first,
            APP_TRIGGER_LIMIT_PARAMETER.second
        )
        set(value) = preferences.edit {
            it.putInt(APP_TRIGGER_LIMIT_PARAMETER.first, value)
        }
}