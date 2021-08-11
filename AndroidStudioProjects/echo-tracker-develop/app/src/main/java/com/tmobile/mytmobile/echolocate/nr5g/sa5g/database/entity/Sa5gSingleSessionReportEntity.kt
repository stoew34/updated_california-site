package com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.Sa5gDatabaseConstants

/**
 * Class that declare all the variables of Sa5g report for single session
 */

@Entity(tableName = Sa5gDatabaseConstants.SA5G_REPORT_TABLE_NAME)

data class Sa5gSingleSessionReportEntity(

    @PrimaryKey(autoGenerate = false)
    var sa5gReportId: String,

    /**
     *JSON String
     */
    val json: String,

    /**
     * Timestamp when the event is received by the application.
     */
    val eventTimestamp: String,

    /**
     * Report status if in process of reporting to report module
     */
    var reportStatus: String?
)