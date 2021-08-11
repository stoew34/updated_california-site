package com.tmobile.mytmobile.echolocate.autoupdate

import android.content.Context
import android.content.SharedPreferences

object AutoUpdatePreference {

    /**
     * File name in which auto update preference is saved
     */
    private const val AUTO_UPDATE_PREF_FILE_NAME = "autoUpdatePreference"

    /**
     * specifies mode of the shared preferences
     */
    private const val MODE = Context.MODE_PRIVATE

    /**
     * shared preference variable that used to store/remove shared preference data
     */
    private lateinit var preferences: SharedPreferences

    /**
     * initialization of the shared preference
     */
    fun init(context: Context) {
        preferences = context.getSharedPreferences(
            AUTO_UPDATE_PREF_FILE_NAME,
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

    private val APP_VERSION = Pair("AppVersion", "")

    /**
     * Used to save and get [APP Version] in shared preference
     */
    var appVersion: String
        get() = preferences.getString(APP_VERSION.first, APP_VERSION.second).toString()
        set(value) = preferences.edit {
            it.putString(APP_VERSION.first, value)
        }
}