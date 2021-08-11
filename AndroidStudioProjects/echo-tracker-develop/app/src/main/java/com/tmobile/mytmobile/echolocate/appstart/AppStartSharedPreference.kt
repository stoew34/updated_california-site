package com.tmobile.mytmobile.echolocate.appstart

import android.content.Context
import android.content.SharedPreferences
import com.tmobile.mytmobile.echolocate.appstart.utils.AppStartUtils

/**
 *  shared preferences used to hold configurationd data
 */
object AppStartSharedPreference {

    /**
     * file name in which configuration file is saved
     */
    private const val APPSTART_PREF_FILE_NAME = "appStartSharedPreferences"
    /**
     * specifies mode of the shared preferences
     */
    private const val MODE = Context.MODE_PRIVATE

    /**
     * shared preference variable that used to store/remove shared preference data
     */
    private lateinit var preferences: SharedPreferences

    // list of app specific preferences
    private val CURRENT_APP_FLAVOR = Pair("currentAppFlavor", AppStartUtils.BUILD_FLAVOR_DEV)


    /**
     * initialization of the shared preference
     */
    fun init(context: Context) {
        preferences = context.getSharedPreferences(
            APPSTART_PREF_FILE_NAME,
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
     * Used to save and get the current flavor from shared preference
     */
    var currentAppFlavor: Int
        get() = preferences.getInt(
            CURRENT_APP_FLAVOR.first, CURRENT_APP_FLAVOR.second)
        set(value) = preferences.edit {
            it.putInt(CURRENT_APP_FLAVOR.first, value)
        }
}