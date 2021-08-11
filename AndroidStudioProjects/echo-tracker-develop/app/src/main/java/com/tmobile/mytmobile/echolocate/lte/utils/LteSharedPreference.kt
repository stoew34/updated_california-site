package com.tmobile.mytmobile.echolocate.lte.utils

import android.content.Context
import android.content.SharedPreferences

/**
 *  shared preferences used to hold configurationd data
 */
object LteSharedPreference {

    /**
     * file name in which configuration file is saved
     */
    private const val LTE_PREF_FILE_NAME = "lteSharedPreferences"
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
    private val TRIGGER_LIMIT_PARAMETER = Pair("triggerLimit", 0)
    private val TRIGGER_COUNT_PARAMETER = Pair("triggerCount", 0)


    /**
     * initialization of the shared preference
     */
    fun init(context: Context) {
        preferences = context.getSharedPreferences(LTE_PREF_FILE_NAME, MODE)
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
        get() = preferences.getString(CONFIGURATION_PARAMETER.first, CONFIGURATION_PARAMETER.second)
        set(value) = preferences.edit {
            it.putString(CONFIGURATION_PARAMETER.first, value)
        }

    /**
     * Used to save and get [WORKID_PARAMETER] in shared preference
     */
    var scheduledWorkId: Long
        get() = preferences.getLong(WORKID_PARAMETER.first, WORKID_PARAMETER.second)
        set(value) = preferences.edit {
            it.putLong(WORKID_PARAMETER.first, value)
        }

    /**
     * Used to save and get [TRIGGER_LIMIT_PARAMETER] in shared preference
     */
    var triggerLimit: Int
        get() = preferences.getInt(TRIGGER_LIMIT_PARAMETER.first, TRIGGER_LIMIT_PARAMETER.second)
        set(value) = preferences.edit {
            it.putInt(TRIGGER_LIMIT_PARAMETER.first, value)
        }

    /**
     * Used to save and get [TRIGGER_COUNT_PARAMETER] in shared preference
     */
    var triggerCount: Int
        get() = preferences.getInt(TRIGGER_COUNT_PARAMETER.first, TRIGGER_COUNT_PARAMETER.second)
        set(value) = preferences.edit {
            it.putInt(TRIGGER_COUNT_PARAMETER.first, value)
        }
}