package com.tmobile.mytmobile.echolocate.analytics.database

object AnalyticsDatabaseConstants {

    /**
     * ECHO_LOCATE_ANALYTICS_DB_NAME
     *
     * use with getEchoLocateAnalyticsDatabase to create the database
     * value echolocate_analytics_db
     */
    const val ECHO_LOCATE_ANALYTICS_DB_NAME = "echolocate_analytics_db"

    /**
     * ANALYTICS_EVENT_TABLE_NAME
     *
     * use with entity in AnalyticsEventEntity data class to create VoiceAnalyticsEntity table name
     * value analytics_event
     */
    const val ANALYTICS_EVENT_TABLE_NAME = "analytics_event"

    /**
     * ANALYTICS_EVENT_LIMIT
     *
     * Define a limit of analytics events
     */
    const val ANALYTICS_EVENT_LIMIT = 2000

    /**
     * ANALYTICS_EVENT_NUM_FOR_DELETE
     *
     * Define a numbers of record need to be removed from db once limit reached
     */
    const val ANALYTICS_EVENT_NUM_FOR_DELETE = 400

}
