package com.tmobile.mytmobile.echolocate.voice.utils

import android.content.Context
import android.content.SharedPreferences

/**
 *  shared preferences used to hold configurationd data
 */
object VoiceSharedPreference {

    /**
     * file name in which configuration file is saved
     */
    private const val VOICE_PREF_FILE_NAME = "voiceSharedPreferences"
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


    /**
     * initialization of the shared preference
     */
    fun init(context: Context) {
        preferences = context.getSharedPreferences(VOICE_PREF_FILE_NAME, MODE)
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

}