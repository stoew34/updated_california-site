package com.tmobile.mytmobile.echolocate.nr5g.core.utils

import android.content.Context
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.utils.SingletonHolder

/**
 * This class maintains all the trigger related data for the nr5g
 */
class Nr5gApplicationTrigger(val context: Context) {
    companion object : SingletonHolder<Nr5gApplicationTrigger, Context>(::Nr5gApplicationTrigger)

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
    fun increaseAppTriggerCount() {
        val appTriggerCount = Nr5gSharedPreference.appTriggerCount + INCREMENT_TRIGGER_COUNT
        EchoLocateLog.eLogE("CMS App Trigger Limit-increased count is: $appTriggerCount")
        Nr5gSharedPreference.appTriggerCount = appTriggerCount
    }

    /**
     * Returns true when the count exceeds the limit defined in the configuration
     * @return Boolean returns true if the count exceeds the limit
     */
    fun isAppCountWithinLimit(): Boolean {
        return (Nr5gSharedPreference.appTriggerCount < Nr5gSharedPreference.appTriggerLimit)
    }

    /**
     * Returns true when the count equals the limit defined in the configuration
     * @return Boolean returns true if the count equals the limit
     */
    fun isAppTriggerLimitReached(): Boolean {
        return Nr5gSharedPreference.appTriggerCount >= Nr5gSharedPreference.appTriggerLimit
    }

    /**
     * saves the app trigger limit the shared preferences
     * @param appTriggerLimit:[Int] the limit to set
     */
    fun saveAppTriggerLimit(appTriggerLimit: Int) {
        Nr5gSharedPreference.appTriggerLimit = appTriggerLimit
    }

    /**
     * saves the app trigger count the shared preferences
     * @param appTriggerCount:[Int] the count to set
     */
    fun saveAppTriggerCount(appTriggerCount: Int) {
        Nr5gSharedPreference.appTriggerCount = appTriggerCount
    }

    /**
     * Returns the app trigger limit that is saved in the shared preferences
     * @return [Int] returns the limit
     */
    fun getAppTriggerLimit(): Int {
        return Nr5gSharedPreference.appTriggerLimit
    }

    /**
     * Returns the trigger count that is saved in the shared preferences
     * @return [Int] returns the count
     */
    fun getAppTriggerCount(): Int {
        return Nr5gSharedPreference.appTriggerCount
    }
}