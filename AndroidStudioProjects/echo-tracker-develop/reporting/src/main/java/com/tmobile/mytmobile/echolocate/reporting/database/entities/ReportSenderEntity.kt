package com.tmobile.mytmobile.echolocate.reporting.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tmobile.mytmobile.echolocate.reporting.database.ReportSenderDatabaseConstants

/**
 * This entity is saved in the database as a row
 */
@Entity(tableName = ReportSenderDatabaseConstants.REPORT_SENDER_TABLE_NAME)
data class ReportSenderEntity(
    /**
     * reportId of the report
     */
    @PrimaryKey
    var reportId: String,

    /**
     *  Time at which the report is created
     */
    var reportCreationTime: Long,

    /**
     * The fully qualified path of the file saved in the local
     */
    var fileName: String,

    /**
     * Status [ReportSenderDatabaseConstants] of the report sent (SENT, NOT_SENT)
     */
    var status: String,

    /**
     * If report sending is failed, the http error code will be saved in the db
     */
    var httpError: String?
)