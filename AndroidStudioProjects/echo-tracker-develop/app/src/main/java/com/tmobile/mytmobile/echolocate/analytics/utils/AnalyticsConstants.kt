package com.tmobile.mytmobile.echolocate.analytics.utils

object AnalyticsConstants {
    /**
     * OS_CRASH_PAYLOAD
     *
     * Payload for OS crashes
     */
    const val OS_CRASH_PAYLOAD = "1"

    const val DATAMETRIC_SCHEDULER_INTERVAL_IN_MINS = 24 * 60

    const val ANALYTICS_DATAMETRIC_SCHEDULER = "AnalyticsScheduler"

    //For DataMetrics Availability
    const val SA5G = "SA5G"
    const val LTE = "LTE"
    const val NULL = "NULL"

    //For Location and Phone permission availability
    const val PHONE_PERMISSION_AVAILABLE = "PHONE"
    const val LOCATION_PERMISSION_AVAILABLE = "LOC"
}