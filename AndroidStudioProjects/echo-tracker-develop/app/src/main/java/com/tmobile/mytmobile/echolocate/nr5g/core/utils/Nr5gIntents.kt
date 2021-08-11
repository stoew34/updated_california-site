package com.tmobile.mytmobile.echolocate.nr5g.core.utils

/**
 *  [Nr5gIntents] holds all the actions related to 5g data collection module
 */

object Nr5gIntents {

    /**
     * DETAILED_APP_STATE
     *
     * use with registerNr5gActions method in 5g Module manager to register intent
     * value diagandroid.app.receiveDetailedApplicationState
     */
    const val DETAILED_APP_STATE = "diagandroid.app.receiveDetailedApplicationState"
    const val APP_STATE_KEY = "ApplicationState"
    val APP_PACKAGE = "ApplicationPackageName"
    val TRIGGER_TIMESTAMP = "triggerTimestamp"

    /**
     * APP_INTENT_ACTION
     *
     * use with registerNr5gActions method in Base5gDataManager to register intent
     * value diagandroid.app.ApplicationState
     */
    const val APP_INTENT_ACTION = "diagandroid.app.ApplicationState"

    /**
     *  ACTION_SCREEN_OFF
     *
     * The application screen off intent action name.
     */
    const val ACTION_SCREEN_OFF = "android.intent.action.SCREEN_OFF"

    /**
     * ACTION_SCREEN_ON
     *
     * The application screen ON intent action name.
     */
    const val ACTION_SCREEN_ON = "android.intent.action.SCREEN_ON"

}