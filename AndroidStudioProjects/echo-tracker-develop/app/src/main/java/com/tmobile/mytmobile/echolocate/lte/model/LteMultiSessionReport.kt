package com.tmobile.mytmobile.echolocate.lte.model

import com.google.gson.annotations.SerializedName

/**
 * Data class that declare all the variables of Lte model
 */
data class LteMultiSessionReport(

    /**
     * column "Status" with data type String,
     *  so that all the session Ids can be tracked if they are already processed or not.
     */
    @Transient
    val status: String,

    /**
     * LteSingleSessionReport data list
     */
    @SerializedName("sessions")
    val lteMultiSessionReport: List<LteSingleSessionReport>
)