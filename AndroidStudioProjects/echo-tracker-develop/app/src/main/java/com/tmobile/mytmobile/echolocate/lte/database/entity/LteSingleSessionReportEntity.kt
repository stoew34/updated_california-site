package com.tmobile.mytmobile.echolocate.lte.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tmobile.mytmobile.echolocate.lte.database.LteDatabaseConstants

/**
 * Class that declare all the variables of lte report for single session
 */

@Entity(tableName = LteDatabaseConstants.LTE_REPORT_TABLE_NAME)

data class LteSingleSessionReportEntity(
    @PrimaryKey(autoGenerate = false)
    var lteReportId: String,

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

