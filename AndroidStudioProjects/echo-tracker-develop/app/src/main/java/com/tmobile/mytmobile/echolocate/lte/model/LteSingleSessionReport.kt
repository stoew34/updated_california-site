package com.tmobile.mytmobile.echolocate.lte.model

import com.google.gson.annotations.SerializedName
import com.tmobile.myaccount.events.diagnostics.pojos.collector.event.eventdata.BaseEventData

/**
 * Data class that declare all the variables of Lte model
 */
data class LteSingleSessionReport(

//    /**
//     * column "Status" with data type String,
//     *  so that all the session Ids can be tracked if they are already processed or not.
//     */
//    @Transient
//    val status: String,

    /**
     * The Echo locate LTE data is captured when there is an event trigger. Each trigger should
     * have a unique id which should be reported as part of the
     * report.
     */
    val trigger: Int,

    /**
     * convert the timestamp as received in the intent from the OEMs to ISO timestamp with format yyyy-MM-dd'T'HH:mm:ss.SSSZ. For example:
     * 2018-05-16T16:14:10.456-0700.
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

    /**
     * Defines LteOEMSV type
     */
    @SerializedName("OEMSV")
    val oemsv: LteOEMSV?,

    /**
     * Defines BearerConfiguration type
     */
    val bearerConfiguration: BearerConfiguration?,

    /**
     * Defines CommonRFConfiguration type
     */
    @SerializedName("commonRFConfiguration")
    val commonRfConfiguration: CommonRFConfiguration?,

    /**
     * Defines DownlinkCarrierInfo type
     */
    val downlinkCarrierInfo: DownlinkCarrierInfo?,

    /**
     * Defines DownlinkRFConfiguration type
     */
    val downlinkRFConfiguration: DownlinkRFConfiguration?,

    /**
     * Defines LteLocation type
     */
    val location: LteLocation?,

    /**
     * Defines LteNetworkIdentity type
     */
    val networkIdentity: LteNetworkIdentity?,

    /**
     * Defines LteSettings type
     */
    @SerializedName("settings")
    val lteSettings: LteSettings?,

    /**
     * Defines SignalCondition type
     */
    val signalCondition: SignalCondition?,

    /**
     * Defines UpLinkRFConfiguration type
     */
    @SerializedName("upLinkRFConfiguration")
    val upLinkRfConfiguration: UpLinkRFConfiguration?,

    /**
     * Defines UplinkCarrierInfo type
     */
    @SerializedName("upLinkCarrierInfo")
    val uplinkCarrierInfo: UplinkCarrierInfo?
) : BaseEventData()