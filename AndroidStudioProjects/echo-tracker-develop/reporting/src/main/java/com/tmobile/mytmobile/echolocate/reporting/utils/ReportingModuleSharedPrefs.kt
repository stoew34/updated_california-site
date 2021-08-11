package com.tmobile.mytmobile.echolocate.reporting.utils

import android.content.Context
import android.content.SharedPreferences

/**
 *  shared preferences used to hold report compiler data
 */

object ReportingModuleSharedPrefs {


    /**
     * file name in which report compiler file is saved
     */
    const val REPORT_COMPILER_PREF_FILE_NAME = "ReportCompilerSharedPreferences"

    /**
     * specifies mode of the shared preferences
     */
    const val MODE = Context.MODE_PRIVATE

    private lateinit var preferences: SharedPreferences

    // list of app specific preferences
    private val REPORT_COMPILER_INTERVAL_PARAMETER = Pair("ReportCompilerInterval", 0)
    private val RC_WORKID_PARAMETER = Pair("workId", 0L)
    private val REPORT_COMPILER_SCHEDULER_RESPONSE = Pair("ReportCompilerSchedulerResponse", "")
    private lateinit var TOKEN_PARAMETER: Pair<String, String>
    private val CLIENT_APP_ENVIRONMENT = Pair("clientAppEnvironment", "")
    private val CLIENT_APP_REPORTING_URL = Pair("clientAppReportingUrl", "")
    private val CLIENT_APP_BUILD_TYPE_DEBUG = Pair("clientAppBuildTypeDebug", false)
    private val CLIENT_APP_VERSION_NAME = Pair("clientAppVersionName", "")
    private val CLIENT_APP_VERSION_CODE = Pair("clientAppVersionCode", "")
    private val CLIENT_APP_APPLICATION_ID = Pair("clientAppApplicationId", "")
    private val CLIENT_APP_SAVE_TO_FILE = Pair("clientAppSaveToFile", false)


    fun init(context: Context, flavour: String) {
        preferences = context.getSharedPreferences(
            REPORT_COMPILER_PREF_FILE_NAME,
            MODE
        )
        TOKEN_PARAMETER = Pair("${flavour}_token", "")
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
     * Gets and Sets the iterval based on the object passed
     */
    var interval: Int
        get() = preferences.getInt(
            REPORT_COMPILER_INTERVAL_PARAMETER.first, REPORT_COMPILER_INTERVAL_PARAMETER.second
        )
        set(value) = preferences.edit {
            it.putInt(REPORT_COMPILER_INTERVAL_PARAMETER.first, value)
        }

    /**
     * Gets and Sets the WorkID's based on the object passed
     */
    var scheduledWorkId: Long
        get() = preferences.getLong(
            RC_WORKID_PARAMETER.first, RC_WORKID_PARAMETER.second
        )
        set(value) = preferences.edit {
            it.putLong(RC_WORKID_PARAMETER.first, value)
        }

    var schedulerResponseEvent: String?
        get() = preferences.getString(
            REPORT_COMPILER_SCHEDULER_RESPONSE.first, REPORT_COMPILER_SCHEDULER_RESPONSE.second
        )
        set(value) = preferences.edit {
            it.putString(REPORT_COMPILER_SCHEDULER_RESPONSE.first, value)
        }

    /**
     * Used to save and get [TOKEN_PARAMETER] in shared preference
     */
    var tokenObject: String?
        get() = preferences.getString(
            TOKEN_PARAMETER.first, TOKEN_PARAMETER.second)
        set(value) = preferences.edit {
            it.putString(TOKEN_PARAMETER.first, value)
        }

    /**
     * Used to save and get [CLIENT_APP_ENVIRONMENT] in shared preference
     */
    var clientAppEnvironment: String?
        get() = preferences.getString(CLIENT_APP_ENVIRONMENT.first, CLIENT_APP_ENVIRONMENT.second)
        set(value) = preferences.edit {
            it.putString(CLIENT_APP_ENVIRONMENT.first, value)
        }

    /**
     * Used to save and get [CLIENT_APP_REPORTING_URL] in shared preference
     */
    var clientAppReportingUrl: String?
        get() = preferences.getString(
            CLIENT_APP_REPORTING_URL.first, CLIENT_APP_REPORTING_URL.second)
        set(value) = preferences.edit {
            it.putString(CLIENT_APP_REPORTING_URL.first, value)
        }

    /**
     * Used to save and get [CLIENT_APP_BUILD_TYPE_DEBUG] in shared preference
     */
    var clientAppBuildTypeDebug: Boolean
        get() = preferences.getBoolean(CLIENT_APP_BUILD_TYPE_DEBUG.first, CLIENT_APP_BUILD_TYPE_DEBUG.second)
        set(value) = preferences.edit {
            it.putBoolean(CLIENT_APP_BUILD_TYPE_DEBUG.first, value)
        }

    /**
     * Used to save and get [CLIENT_APP_VERSION_NAME] in shared preference
     */
    var clientAppVersionName: String?
        get() = preferences.getString(
            CLIENT_APP_VERSION_NAME.first, CLIENT_APP_VERSION_NAME.second)
        set(value) = preferences.edit {
            it.putString(CLIENT_APP_VERSION_NAME.first, value)
        }

    /**
     * Used to save and get [CLIENT_APP_VERSION_CODE] in shared preference
     */
    var clientAppVersionCode: String?
        get() = preferences.getString(
            CLIENT_APP_VERSION_CODE.first, CLIENT_APP_VERSION_CODE.second)
        set(value) = preferences.edit {
            it.putString(CLIENT_APP_VERSION_CODE.first, value)
        }

    /**
     * Used to save and get [CLIENT_APP_APPLICATION_ID] in shared preference
     */
    var clientAppApplicationId: String?
        get() = preferences.getString(
            CLIENT_APP_APPLICATION_ID.first, CLIENT_APP_APPLICATION_ID.second)
        set(value) = preferences.edit {
            it.putString(CLIENT_APP_APPLICATION_ID.first, value)
        }

    /**
     * Used to save and get [CLIENT_APP_SAVE_TO_FILE] in shared preference
     */
    var clientAppSaveToFile: Boolean
        get() = preferences.getBoolean(CLIENT_APP_SAVE_TO_FILE.first, CLIENT_APP_SAVE_TO_FILE.second)
        set(value) = preferences.edit {
            it.putBoolean(CLIENT_APP_SAVE_TO_FILE.first, value)
        }
}