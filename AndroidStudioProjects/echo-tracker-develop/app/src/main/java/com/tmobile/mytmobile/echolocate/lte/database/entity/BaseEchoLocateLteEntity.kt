package com.tmobile.mytmobile.echolocate.lte.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tmobile.mytmobile.echolocate.lte.database.LteDatabaseConstants

/**
 * Class that declare all the variables of BaseEchoLocateLteEntity
 * These are columns stored in the room data base
 */
@Entity(tableName = LteDatabaseConstants.ECHO_LOCATE_LTE_BASE_TABLE_NAME)
data class BaseEchoLocateLteEntity(

    /**
     * The Echo locate LTE data is captured when there is an event trigger. Each trigger should have
     * a unique id which should be reported as part of the report.
     */
    val trigger: Int,

    /**
     * column "Status" with data type String,
     *  so that all the session Ids can be tracked if they are already processed or not.
     */
    var status: String,

    /**
     * convert the timestamp as received in the intent from the OEMs to ISO timestamp with format
     * yyyy-MM-dd'T'HH:mm:ss.SSSZ. For example: 2018-05-16T16:14:10.456-0700.
     */
    val triggerTimestamp: String,

    /**
     * This is the OEM APIs version supported on this device.
     */
    val oemApiVersion: String,

    /**
     * The version of the schema for which the data is being reported.
     */
    val schemaVersion: String,

    @PrimaryKey(autoGenerate = false)
    val sessionId: String
)