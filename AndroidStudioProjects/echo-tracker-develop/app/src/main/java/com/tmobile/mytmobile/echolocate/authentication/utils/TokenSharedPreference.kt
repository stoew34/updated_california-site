package com.tmobile.mytmobile.echolocate.authentication.utils

import android.content.Context
import android.content.SharedPreferences
import com.tmobile.mytmobile.echolocate.BuildConfig

/**
 *  shared preferences used to hold configurationd data
 */
object TokenSharedPreference {

    /**
     * file name in which configuration file is saved
     */
    private const val TOKEN_PREF_FILE_NAME = "tokenSharedPreferences"
    /**
     * specifies mode of the shared preferences
     */
    private const val MODE = Context.MODE_PRIVATE

    /**
     * shared preference variable that used to store/remove shared preference data
     */
    private lateinit var preferences: SharedPreferences

    // list of app specific preferences
    private val TOKEN_PARAMETER = Pair("${BuildConfig.FLAVOR}_token", "")
    private val WORKID_PARAMETER = Pair("workId", 0L)
    private val SCHEDULER_RESPONSE = Pair("RefreshTokenSchedulerResponse", "")


    /**
     * initialization of the shared preference
     */
    fun init(context: Context) {
        preferences = context.getSharedPreferences(
            TOKEN_PREF_FILE_NAME,
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
    var tokenObject: String?
        get() = preferences.getString(
            TOKEN_PARAMETER.first, TOKEN_PARAMETER.second)
        set(value) = preferences.edit {
            it.putString(TOKEN_PARAMETER.first, value)
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

    var schedulerResponseEvent: String?
        get() = preferences.getString(
            SCHEDULER_RESPONSE.first, SCHEDULER_RESPONSE.second
        )
        set(value) = preferences.edit {
            it.putString(SCHEDULER_RESPONSE.first, value)
        }
}