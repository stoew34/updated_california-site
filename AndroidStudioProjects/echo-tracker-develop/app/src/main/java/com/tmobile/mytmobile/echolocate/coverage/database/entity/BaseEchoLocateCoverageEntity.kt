package com.tmobile.mytmobile.echolocate.coverage.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tmobile.mytmobile.echolocate.coverage.database.CoverageDatabaseConstants

/**
 * Class that declare all the variables of BaseEchoLocateCoverageEntity
 * These are columns stored in the room data base
 */
@Entity(tableName = CoverageDatabaseConstants.COVERAGE_BASE_TABLE_NAME)
data class BaseEchoLocateCoverageEntity(

    /**
     * The Echo locate Coverage data is captured when there is an event trigger. Each trigger should have
     * a unique id which should be reported as part of the report.
     */
    val trigger: String,

    /**
     * column "Status" with data type String,
     *  so that all the session Ids can be tracked if they are already processed or not.
     */
    var status: String,

    /**
     * convert the timestamp as received in the intent from the OEMs to ISO timestamp with format
     * yyyy-MM-dd'T'HH:mm:ss.SSSZ. For example: 2018-05-16T16:14:10.456-0700.
     */
    val timestamp: String,

    /**
     * The version of the schema for which the data is being reported.
     */
    val schemaVersion: String,

    @PrimaryKey(autoGenerate = false)
    val sessionId: String
)