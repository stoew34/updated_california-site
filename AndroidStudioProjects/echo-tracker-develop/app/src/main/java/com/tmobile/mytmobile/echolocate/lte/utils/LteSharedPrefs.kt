package com.tmobile.mytmobile.echolocate.lte.utils

import android.content.Context
import android.content.SharedPreferences


/**
 *  shared preferences used lte
 */

object LteSharedPrefs {


    /**
     * file name in which report compiler file is saved
     */
    private const val REPORT_COMPILER_PREF_FILE_NAME = "LteSharedPreferences"
    /**
     * specifies mode of the shared preferences
     */
    private const val MODE = Context.MODE_PRIVATE

    private lateinit var preferences: SharedPreferences

    private val LTE_WORKID_PARAMETER = Pair("workId", 0L)

    private val LTE_SCHEDULER_RESPONSE = Pair("LteSchedulerResponse", "")


    fun init(context: Context) {
        preferences = context.getSharedPreferences(
            REPORT_COMPILER_PREF_FILE_NAME,
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
     * Gets and Sets the WorkID's based on the object passed
     */
    var scheduledWorkId: Long
        get() = preferences.getLong(
            LTE_WORKID_PARAMETER.first, LTE_WORKID_PARAMETER.second
        )
        set(value) = preferences.edit {
            it.putLong(LTE_WORKID_PARAMETER.first, value)
        }

    var schedulerResponseEvent: String?
        get() = preferences.getString(
            LTE_SCHEDULER_RESPONSE.first,
            LTE_SCHEDULER_RESPONSE.second
        )
        set(value) = preferences.edit {
            it.putString(LTE_SCHEDULER_RESPONSE.first, value)
        }

}