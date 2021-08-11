package com.tmobile.mytmobile.echolocate.coverage.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tmobile.mytmobile.echolocate.coverage.database.CoverageDatabaseConstants

/**
 * Class that declare all the variables of Coverage report for single session
 */

@Entity(tableName = CoverageDatabaseConstants.COVERAGE_REPORT_TABLE_NAME)

data class CoverageSingleSessionReportEntity(

    @PrimaryKey(autoGenerate = false)
    var coverageReportId: String,

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