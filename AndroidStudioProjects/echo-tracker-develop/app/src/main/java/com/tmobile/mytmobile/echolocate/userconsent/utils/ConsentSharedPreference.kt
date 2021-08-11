package com.tmobile.mytmobile.echolocate.userconsent.utils

import android.content.Context
import android.content.SharedPreferences

/**
 *  shared preferences used to hold user consent data
 */
object ConsentSharedPreference {
    /**
     * file name in which configuration file is saved
     */
    private const val CONSENT_PREF_FILE_NAME = "consentSharedPreferences"
    /**
     * specifies mode of the shared preferences
     */
    private const val MODE = Context.MODE_PRIVATE

    /**
     * shared preference variable that used to store/remove shared preference data
     */
    private lateinit var preferences: SharedPreferences

    // list of preferences specific to user consent flag
    private val CONSENT_FLAG_WORKID_PARAMETER = Pair("workId", 0L)
    private val CONSENT_FLAG_ATTEMPT_NUMBER = Pair("attemptNumber", UserConsentUtils.CONSENT_ATTEMPT_DEFAULT_UNASSIGNED_VALUE)

    /**
     * initialization of the shared preference
     */
    fun init(context: Context) {
        preferences = context.getSharedPreferences(
            CONSENT_PREF_FILE_NAME,
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
     * Used to save and get [CONSENT_FLAG_WORKID_PARAMETER] in shared preference
     */
    var scheduledWorkId: Long
        get() = preferences.getLong(
            CONSENT_FLAG_WORKID_PARAMETER.first, CONSENT_FLAG_WORKID_PARAMETER.second)
        set(value) = preferences.edit {
            it.putLong(CONSENT_FLAG_WORKID_PARAMETER.first, value)
        }

    /**
     * Used to save and get [CONSENT_FLAG_ATTEMPT_NUMBER] in shared preference
     */
    var attemptNumber: Int
        get() = preferences.getInt(
            CONSENT_FLAG_ATTEMPT_NUMBER.first, CONSENT_FLAG_ATTEMPT_NUMBER.second)
        set(value) = preferences.edit {
            it.putInt(CONSENT_FLAG_ATTEMPT_NUMBER.first, value)
        }
}