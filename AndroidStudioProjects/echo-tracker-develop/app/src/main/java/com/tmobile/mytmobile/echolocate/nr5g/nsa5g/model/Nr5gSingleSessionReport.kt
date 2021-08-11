package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model

import com.google.gson.annotations.SerializedName
import com.tmobile.myaccount.events.diagnostics.pojos.collector.event.eventdata.BaseEventData

/**
 * Model class that holds call Nr5gSingleSessionReport data
 *
 * Serialized name annotation for all variables to be in JSON,
 * and it will be helpful while doing progaurding
 */
data class Nr5gSingleSessionReport(
    /**
     * defines Nr5gDeviceInfo data
     */
    @SerializedName("deviceInfo")
    val deviceInfo: Nr5gDeviceInfo?,

    /**
     * defines Nr5gLocation data
     */
    @SerializedName("location")
    val location: Nr5gLocation?,

    /**
     * defines ConnectedWifiStatus data
     */
    @SerializedName("connectedWifiStatus")
    val connectedWifiStatus: ConnectedWifiStatus?,

    /**
     * defines Nr5gOEMSV data
     */
    @SerializedName("OEMSV")
    val oemsv: Nr5gOEMSV?,

    /**
     * defines EndcLteLog data
     */
    @SerializedName("endcLteLog")
    val endcLteLog: EndcLteLog?,

    /**
     * defines Nr5gMmwCellLog data
     * according to shema "5gNrMmwCellLog"
     */
    @SerializedName("5gNrMmwCellLog")
    val nr5GMmwCellLog: Nr5gMmwCellLog?,

    /**
     * defines EndcUplinkLog data
     */
    @SerializedName("endcUplinkLog")
    val endcUplinkLog: EndcUplinkLog?,

    /**
     * defines Nr5gUiLog data
     */
    @SerializedName("5gUiLog")
    val nr5gUiLog: Nr5gUiLog?,

    /**
     * defines Nr5gStatus data
     */
    @SerializedName("getNrStatus")
    val nr5gStatus: Nr5gStatus?,

    /**
     * defines Nr5gNetworkIdentity(getNetworkIdentity) data
     */
    @SerializedName("getNetworkIdentity")
    val nr5gNetworkIdentity: Nr5gNetworkIdentity?,

    /**
     * defines Nr5gDataNetworkType(getDataNetworkType) data
     */
    @SerializedName("getDataNetworkType")
    val nr5gDataNetworkType: Nr5gDataNetworkType?,

    /**
     * defines Nr5gTrigger data
     */
    @SerializedName("trigger")
    val nr5gTrigger: Nr5gTrigger?,

    /**
     * defines Nr5gWiFiState(getWiFiState) data
     */
    @SerializedName("getWiFiState")
    val nr5gWiFiState: Nr5gWiFiState?,

    /**
     * defines Nr5gActiveNetwork(getActiveNetwork) data
     */
    @SerializedName("getActiveNetwork")
    val nr5gActiveNetwork: Nr5gActiveNetwork?

) : BaseEventData()