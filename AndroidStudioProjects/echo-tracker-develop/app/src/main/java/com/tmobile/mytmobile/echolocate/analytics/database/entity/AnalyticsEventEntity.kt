package com.tmobile.mytmobile.echolocate.analytics.database.entity

import androidx.room.Entity
import com.tmobile.mytmobile.echolocate.analytics.database.AnalyticsDatabaseConstants
import com.tmobile.mytmobile.echolocate.analytics.reportprocessor.AnalyticsDataStatus.Companion.STATUS_RAW

/**
 * class that declares all variables of AnalyticsEventEntity
 * These are columns stored in the room data base for AnalyticsEventEntity
 */
@Entity(tableName = AnalyticsDatabaseConstants.ANALYTICS_EVENT_TABLE_NAME)

data class AnalyticsEventEntity(

    /**
     * convert the timestamp as received in the intent from the OEMs to ISO timestamp with format
     * yyyy-MM-dd'T'HH:mm:ss.SSSZ. For example: 2018-05-16T16:14:10.456-0700.
     */
    val timestamp: String,

    /**
     * The name of module/app reporting it.
     */
    val moduleName: String,

    /**
     * What is the nature of the analytics, what kind of action
     */
    val action: String,

    /**
     * Any other information/description.
     */
    val payload: String?

) : AnalyticsBaseEntity(0L, "")