package com.tmobile.mytmobile.echolocate.analytics.database.entity

import androidx.room.PrimaryKey

/**
 * class that declares the base type
 */
open class AnalyticsBaseEntity(

    /**
     * aReportId it is a unique id which will get generated at the time of insertion
     */
    @PrimaryKey(autoGenerate = true)
    var aReportId: Long,

    /**
     * column "Status" (RAW-PROCESSED) with data type String,
     *  so that all the session Ids can be tracked if they are already processed or not.
     */
    var status: String

)