package com.tmobile.mytmobile.echolocate.analytics

import android.content.Intent

/**
 * Interface that defines method onHandleIntent. When a intent is received from broadcast [onHandleIntent] function is called.
 */
interface IAnalyticsIntentHandler {
    /**
     * When a intent is received from broadcast [onHandleIntent] function is called
     * to handle multiple data based on the action received from the intent
     * This class is implemented by all the data objects that want to handle the intent received from
     * broad cast receiver
     *
     * @param intent Intent object received by the broadcast receiver
     *
     * @param eventTimestamp The timestamp at which the intent was received by the application.
     */
    fun onHandleIntent(intent: Intent?, eventTimestamp: Long)
}