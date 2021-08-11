package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.Nr5gDatabaseConstants


/**
 * Class that declare all the variables of nr5g report for single session
 */

@Entity(tableName = Nr5gDatabaseConstants.NR5G_REPORT_TABLE_NAME)

data class Nr5gSingleSessionReportEntity(

    @PrimaryKey(autoGenerate = false)
    var nr5gReportId: String,

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