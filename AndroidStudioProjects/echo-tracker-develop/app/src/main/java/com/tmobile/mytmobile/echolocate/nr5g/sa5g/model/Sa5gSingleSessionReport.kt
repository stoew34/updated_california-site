package com.tmobile.mytmobile.echolocate.nr5g.sa5g.model

import com.google.gson.annotations.SerializedName
import com.tmobile.myaccount.events.diagnostics.pojos.collector.event.eventdata.BaseEventData

/**
 * Data class that declare all the variables of Sa5g model
 */
data class Sa5gSingleSessionReport(

    /**
     * Defines deviceInfo data
     */
    @SerializedName("deviceInfo")
    val deviceInfo: Sa5gDeviceInfo?,

    /**
     * Defines location data
     */
    @SerializedName("location")
    val location: Sa5gLocation?,

    /**
     * Defines trigger data
     */
    @SerializedName("trigger")
    val trigger: Sa5gTrigger?,

    /**
     * Defines trigger data
     */
    @SerializedName("datametricsVersion")
    val datametricsVersion: String?,

    /**
     * Defines WiFi State data
     */
    @SerializedName("getWiFiState")
    val getWiFiState: Sa5gWiFiState?,

    /**
     * Defines ActiveNetwork data
     */
    @SerializedName("getActiveNetwork")
    val getActiveNetwork: Sa5gActiveNetwork?,

    /**
     * Defines connectedWifiStatus data
     */
    @SerializedName("connectedWifiStatus")
    val connectedWifiStatus: Sa5gConnectedWifiStatus?,

    /**
     * Defines OEMSV data
     */
    @SerializedName("OEMSV")
    val oemsv: Sa5gOEMSV?,

    /**
     * DlCarrierLog contains the metrics for the downlink carriers.
     * One object instance per each downlink carrier. For instance, if device is on 4CA,
     * the number of the object instances must be 4. For instance, DlCarrierLog object is a list with
     * multiple items. Device may use more than 1 downlink carrier at the same time and we want to
     * collect the information of all the carriers in use. If a device is using 2 carrier aggregation;
     * one LTE B66 and the other NR n71, one object will contain the information about the LTE B66
     * carrier and the other object will contain the info about the NR n71 carrier.
     */
    @SerializedName("downlinkCarrierLogs")
    val downlinkCarrierLogs: List<Sa5gDownlinkCarrierLogs>?,

    /**
     * UlCarrierLog contains the metrics for the uplink carriers.
     * One object instance per each uplink carrier.
     * For instance, if device is on 2CA, the number of the object instances must be 2.
     */
    @SerializedName("uplinkCarrierLogs")
    val uplinkCarrierLogs: List<Sa5gUplinkCarrierLogs>?,

    /**
     * RrcLog contains the metrics for the RRC states both in LTE and in NR. Only one instance.
     */
    @SerializedName("rrcLog")
    val rrcLog: Sa5gRrcLog?,

    /**
     * NetworkLog contains the metrics for the network identity and capability. Only one instance.
     */
    @SerializedName("networkLog")
    val networkLog: Sa5gNetworkLog?,

    /**
     * SettingsLog contains the metrics for the device settings configuration. Only one instance.
     */
    @SerializedName("settingsLog")
    val settingsLog: Sa5gSettingsLog?,

    /**
     * Defines UI Log
     */
    @SerializedName("uiLog")
    val uiLog: Sa5gUiLog?,

    /**
     * Defines cCarrier Config
     */
    @SerializedName("carrierConfig")
    val carrierConfig: Sa5gCarrierConfig?

) : BaseEventData()