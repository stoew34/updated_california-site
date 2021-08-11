package com.tmobile.mytmobile.echolocate.coverage.utils

/**
 * Created by Mahesh Shetye on 2020-04-23
 *
 * Shared preferences for Coverage
 */

import android.content.Context
import android.content.SharedPreferences

object CoverageSharedPreference {

    /**
     * file name in which configuration file is saved
     */
    private const val COVERAGE_PREF_FILE_NAME = "coverageSharedPreferences"

    /**
     * specifies mode of the shared preferences
     */
    private const val MODE = Context.MODE_PRIVATE

    private lateinit var preferences: SharedPreferences

    // list of screen trigger preferences
    private val LAST_SCREEN_TRIGGER_TIME = Pair("lastScreenTriggerTime", 0L)
    private val SCREEN_TRIGGER_LIMIT = Pair("screenTriggerEventsPerHour", 1)
    private val SCREEN_TRIGGER_COUNT = Pair("screenTriggerCount", 0)

    // list of voice start  preferences
    private val VOICE_START_TRIGGER_LIMIT = Pair("voiceStartTriggerEventsPerHour", 1)
    private val VOICE_START_TRIGGER_COUNT = Pair("voiceStartTriggerCount", 0)
    // list of voice stop preferences
    private val VOICE_STOP_TRIGGER_LIMIT = Pair("voiceStopTriggerEventsPerHour", 1)
    private val VOICE_STOP_TRIGGER_COUNT = Pair("voiceStopTriggerCount", 0)

    // list of voice start  preferences
    private val DATA_SESSION_START_TRIGGER_LIMIT = Pair("dataSesionStartTriggerEventsPerHour", 1)
    private val DATA_SESSION_START_TRIGGER_COUNT = Pair("dataSesionStartTriggerCount", 0)
    // list of voice stop preferences
    private val DATA_SESSION_STOP_TRIGGER_LIMIT = Pair("dataSesionStopTriggerEventsPerHour", 1)
    private val DATA_SESSION_STOP_TRIGGER_COUNT = Pair("dataSesionStopTriggerCount", 0)

    // preference for scheduler work id
    private val WORK_ID_PARAMETER = Pair("workId", 0L)

    fun init(context: Context) {
        preferences = context.getSharedPreferences(COVERAGE_PREF_FILE_NAME, MODE)
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
     * Gets and Sets the time of last screen event triggered
     */
    var lastScreenTriggerTime: Long
        get() = preferences.getLong(LAST_SCREEN_TRIGGER_TIME.first, LAST_SCREEN_TRIGGER_TIME.second)
        set(value) = preferences.edit {
            it.putLong(LAST_SCREEN_TRIGGER_TIME.first, value)
        }

    /**
     * Gets and Sets the time of last screen event triggered
     */
    var screenTriggerLimit: Int
        get() = preferences.getInt(SCREEN_TRIGGER_LIMIT.first, SCREEN_TRIGGER_LIMIT.second)
        set(value) = preferences.edit {
            it.putInt(SCREEN_TRIGGER_LIMIT.first, value)
        }

    /**
     * Gets and Sets the time of last screen event triggered
     */
    var screenTriggerCount: Int
        get() = preferences.getInt(SCREEN_TRIGGER_COUNT.first, SCREEN_TRIGGER_COUNT.second)
        set(value) = preferences.edit {
            it.putInt(SCREEN_TRIGGER_COUNT.first, value)
        }

    /**
     * Gets and Sets the time of voice start trigger limit
     */
    var voiceStartTriggerLimit: Int
        get() = preferences.getInt(VOICE_START_TRIGGER_LIMIT.first, VOICE_START_TRIGGER_LIMIT.second)
        set(value) = preferences.edit {
            it.putInt(VOICE_START_TRIGGER_LIMIT.first, value)
        }
    /**
     * Gets and Sets the time of voice start trigger count
     */
    var voiceStartTriggerCount: Int
        get() = preferences.getInt(VOICE_START_TRIGGER_COUNT.first, VOICE_START_TRIGGER_COUNT.second)
        set(value) = preferences.edit {
            it.putInt(VOICE_START_TRIGGER_COUNT.first, value)
        }
    /**
     * Gets and Sets the time voice stop limit trigger count
     */
    var voiceStopTriggerLimit: Int
        get() = preferences.getInt(VOICE_STOP_TRIGGER_LIMIT.first, VOICE_STOP_TRIGGER_COUNT.second)
        set(value) = preferences.edit {
            it.putInt(VOICE_STOP_TRIGGER_LIMIT.first, value)
        }
    /**
     * Gets and Sets the time of voice stop trigger count
     */
    var voiceStopTriggerCount: Int
        get() = preferences.getInt(VOICE_STOP_TRIGGER_COUNT.first, VOICE_STOP_TRIGGER_COUNT.second)
        set(value) = preferences.edit {
            it.putInt(VOICE_STOP_TRIGGER_COUNT.first, value)
        }

    /**
     * Gets and Sets the time of data session start trigger limit
     */
    var dataSessionStartTriggerLimit: Int
        get() = preferences.getInt(DATA_SESSION_START_TRIGGER_LIMIT.first, DATA_SESSION_START_TRIGGER_LIMIT.second)
        set(value) = preferences.edit {
            it.putInt(DATA_SESSION_START_TRIGGER_LIMIT.first, value)
        }
    /**
     * Gets and Sets the time of data session start trigger count
     */
    var dataSessionStartTriggerCount: Int
        get() = preferences.getInt(DATA_SESSION_START_TRIGGER_COUNT.first, DATA_SESSION_START_TRIGGER_COUNT.second)
        set(value) = preferences.edit {
            it.putInt(DATA_SESSION_START_TRIGGER_COUNT.first, value)
        }
    /**
     * Gets and Sets the time data session stop limit trigger count
     */
    var dataSessionStopTriggerLimit: Int
        get() = preferences.getInt(DATA_SESSION_STOP_TRIGGER_LIMIT.first, DATA_SESSION_STOP_TRIGGER_LIMIT.second)
        set(value) = preferences.edit {
            it.putInt(DATA_SESSION_STOP_TRIGGER_LIMIT.first, value)
        }
    /**
     * Gets and Sets the time of data session stop trigger count
     */
    var dataSessionStopTriggerCount: Int
        get() = preferences.getInt(DATA_SESSION_STOP_TRIGGER_COUNT.first, DATA_SESSION_STOP_TRIGGER_COUNT.second)
        set(value) = preferences.edit {
            it.putInt(DATA_SESSION_STOP_TRIGGER_COUNT.first, value)
        }

    /**
     * Used to save and get [WORKID_PARAMETER] in shared preference
     */
    var scheduledWorkId: Long
        get() = preferences.getLong(WORK_ID_PARAMETER.first, WORK_ID_PARAMETER.second)
        set(value) = preferences.edit {
            it.putLong(WORK_ID_PARAMETER.first, value)
        }


}