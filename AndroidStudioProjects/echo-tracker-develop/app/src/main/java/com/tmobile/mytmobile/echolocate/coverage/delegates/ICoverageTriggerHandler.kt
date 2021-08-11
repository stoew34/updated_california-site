package com.tmobile.mytmobile.echolocate.coverage.delegates

/**
 * Created by Mahesh Shetye on 2020-04-27
 *
 * Base class for coverage triggers
 */

interface ICoverageTriggerHandler {
    /**
     * When a intent is received from broadcast [onHandleTrigger] function is called
     * to handle multiple data based on the action received from the intent
     * This class is implemented by all the delegate objects that want to handle the intent received from
     * broad cast receiver
     *
     */
    fun onHandleTrigger(triggerSource: TriggerSource)
}