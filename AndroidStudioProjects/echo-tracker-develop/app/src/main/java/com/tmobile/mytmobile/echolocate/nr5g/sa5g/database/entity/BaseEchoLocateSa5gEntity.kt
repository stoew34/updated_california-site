package com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.Sa5gDatabaseConstants

/**
 * Class that declare all the variables of base echolocate SA 5G entity
 * These are columns stored in the room data base for base echolocate SA 5G entity
 */
@Entity(tableName = Sa5gDatabaseConstants.SA5G_ECHO_LOCATE_BASE_TABLE_NAME)
data class BaseEchoLocateSa5gEntity(

    /**
     * The Echo locate SA 5G data is captured when there is an event trigger. Each trigger should have
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


    @PrimaryKey(autoGenerate = false)
    val sessionId: String
)