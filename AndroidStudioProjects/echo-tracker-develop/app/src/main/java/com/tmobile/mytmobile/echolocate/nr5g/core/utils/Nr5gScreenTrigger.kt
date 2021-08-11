package com.tmobile.mytmobile.echolocate.nr5g.core.utils

import android.content.Context
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.utils.SingletonHolder

/**
 * This class maintains all the trigger related data for the nr5g
 */
class Nr5gScreenTrigger(val context: Context) {
    companion object : SingletonHolder<Nr5gScreenTrigger, Context>(::Nr5gScreenTrigger)

    init {
        Nr5gSharedPreference.init(context)
    }

    /**
     * Increments the count by 1 whenever an event is received
     */
    private val INCREMENT_TRIGGER_COUNT = 1

    /**
     * Increments the count by 1 whenever an event is received and saves it in the shared preferences
     */
    fun increaseScreenTriggerCount() {
        val screenTriggerCount = Nr5gSharedPreference.screenTriggerCount + INCREMENT_TRIGGER_COUNT
        EchoLocateLog.eLogE("CMS Screen Trigger Limit-increased count is: $screenTriggerCount")
        Nr5gSharedPreference.screenTriggerCount = screenTriggerCount
    }

    /**
     * Returns true when the count exceeds the limit defined in the configuration
     * @return Boolean returns true if the count exceeds the limit
     */
    fun isScreenCountWithinLimit(): Boolean {
        return (Nr5gSharedPreference.screenTriggerCount <= Nr5gSharedPreference.screenTriggerLimit)
    }

    /**
     * Returns true when the count equals the limit defined in the configuration
     * @return Boolean returns true if the count equals the limit
     */
    fun isScreenTriggerLimitReached(): Boolean {
        return Nr5gSharedPreference.screenTriggerCount >= Nr5gSharedPreference.screenTriggerLimit
    }

    /**
     * saves the trigger limit the shared preferences
     * @param screenTriggerLimit:[Int] the limit to set
     */
    fun saveScreenTriggerLimit(screenTriggerLimit: Int) {
        Nr5gSharedPreference.screenTriggerLimit = screenTriggerLimit
    }

    /**
     * saves the screen trigger count the shared preferences
     * @param screenTriggerCount:[Int] the count to set
     */
    fun saveScreenTriggerCount(screenTriggerCount: Int) {
        Nr5gSharedPreference.screenTriggerCount = screenTriggerCount
    }

    /**
     * Returns the screen trigger limit that is saved in the shared preferences
     * @return [Int] returns the limit
     */
    fun getScreenTriggerLimit(): Int {
        return Nr5gSharedPreference.screenTriggerLimit
    }

    /**
     * Returns the trigger count that is saved in the shared preferences
     * @return [Int] returns the count
     */
    fun getScreenTriggerCount(): Int {
        return Nr5gSharedPreference.screenTriggerCount
    }

}