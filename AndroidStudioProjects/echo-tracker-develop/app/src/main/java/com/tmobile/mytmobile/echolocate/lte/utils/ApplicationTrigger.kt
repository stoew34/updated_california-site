package com.tmobile.mytmobile.echolocate.lte.utils

import android.content.Context
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.utils.SingletonHolder

/**
 * This class maintains all the trigger related data for the applications used
 */
class ApplicationTrigger(val context: Context) {
    companion object : SingletonHolder<ApplicationTrigger, Context>(::ApplicationTrigger) {
        /**
         * Increments the count by 1 whenever an event is received
         */
        private const val INCREMENT_TRIGGER_COUNT = 1
    }

    init {
        LteSharedPreference.init(context)
    }

    /**
     * Increments the count by 1 whenever an event is received and saves it in the shared preferences
     */
    fun increaseTriggerCount() {
        val triggerCount = LteSharedPreference.triggerCount + INCREMENT_TRIGGER_COUNT
        EchoLocateLog.eLogE("Diagnostic : CMS Limit-increased count is: $triggerCount")
        LteSharedPreference.triggerCount = triggerCount
    }

    /**
     * Returns true when the count exceeds the limit defined in the configuration
     * @return Boolean returns true if the count exceeds the limit
     */
    fun isCountWithinLimit(): Boolean {
        return (LteSharedPreference.triggerCount <= LteSharedPreference.triggerLimit)
    }

    /**
     * Returns true when the count equals the limit defined in the configuration
     * @return Boolean returns true if the count equals the limit
     */
    fun isTriggerLimitReached(): Boolean {
        return LteSharedPreference.triggerCount >= LteSharedPreference.triggerLimit
    }

    /**
     * saves the trigger limit the shared preferences
     * @param triggerLimit:[Int] the limit to set
     */
    fun saveTriggerLimit(triggerLimit: Int) {
        LteSharedPreference.triggerLimit = triggerLimit
    }

    /**
     * saves the trigger count the shared preferences
     * @param triggerCount:[Int] the count to set
     */
    fun saveTriggerCount(triggerCount: Int) {
        LteSharedPreference.triggerCount = triggerCount
    }

    /**
     * Returns the trigger limit that is saved in the shared preferences
     * @return [Int] returns the limit
     */
    fun getTriggerLimit(): Int {
        return LteSharedPreference.triggerLimit
    }

    /**
     * Returns the trigger count that is saved in the shared preferences
     * @return [Int] returns the count
     */
    fun getTriggerCount(): Int {
        return LteSharedPreference.triggerCount
    }
}