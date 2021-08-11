package com.tmobile.mytmobile.echolocate.coverage.model

import com.google.gson.annotations.SerializedName
import com.tmobile.myaccount.events.diagnostics.pojos.collector.event.eventdata.BaseEventData

/**
 * Data class that declare all the variables of Coverage model
 */
data class CoverageSingleSessionReport(

    /**
     * Defines schemaVersion type
     */
    @SerializedName("schemaVersion")
    val schemaVersion: String,

    /**
     * Defines environment type
     */
    @SerializedName("environment")
    val environment: CoverageEnvironment,

    /**
     * Defines timestamp type
     */
    @SerializedName("timestamp")
    val timestamp: String,

    /**
     * Defines trigger type
     */
    @SerializedName("trigger")
    val trigger: String

) : BaseEventData()