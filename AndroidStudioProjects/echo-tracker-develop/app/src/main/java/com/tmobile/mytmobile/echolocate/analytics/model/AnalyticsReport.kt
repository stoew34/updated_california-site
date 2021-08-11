package com.tmobile.mytmobile.echolocate.analytics.model

import com.google.gson.annotations.SerializedName
import com.tmobile.myaccount.events.diagnostics.pojos.collector.event.eventdata.BaseEventData
import com.tmobile.mytmobile.echolocate.standarddatablocks.DeviceInfo


/**
 * Model class that holds call AnalyticsReport data
 *
 * Serialized name annotation for all variables to be in JSON,
 * and it will be helpful while doing progaurding
 */
data class AnalyticsReport(

    /**
     * defines VoiceAnalyticsData data
     */
    @SerializedName("analytics")
    val analytics: List<AnalyticsEventModel?>?,

    /**
     * defines DeviceInfoAnalyticsData data
     */
    @SerializedName("deviceInfo")
    val deviceInfo: DeviceInfo?

) : BaseEventData()