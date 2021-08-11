package com.tmobile.mytmobile.echolocate.coverage.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.coverage.database.CoverageDatabaseConstants

/**
 * Class to declare all the variables of WifiStatusEntity
 * These are columns stored in the room data base
 */
@Entity(
    tableName = CoverageDatabaseConstants.COVERAGE_WIFI_STATUS__TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = BaseEchoLocateCoverageEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class CoverageConnectedWifiStatusEntity(
    /**
    An integer which corresponds to the following WiFi setting states WIFI_STATE_DISABLING WIFI_STATE_DISABLED WIFI_STATE_ENABLING WIFI_STATE_ENABLED WIFI_STATE_UNKNOWN
     */
    val wifiState: String?,

    /**
    MAC Address of scanned IP. Ex:00:19:92:50:ba:21
     */
    val bssid: String?,

    /**
    Value of BSSLoad
     */
    val bssLoad: String?,

    /**
    Returns the capabilities of the scanned AP. Ex:[WPA2-PSK-CCMP][ESS]
     */
    val capabilities: String?,

    /**
    Value represents centerFreq0 of scanned AP
     */
    val centerFreq0: String?,

    /**
    Value represents centerFreq1 of scanned AP
     */
    val centerFreq1: String?,

    /**
    Value representing channel mode
     */
    val channelMode: String?,

    /**
    Represents width of the channel
     */
    val channelWidth: String?,

    /**
    Represents frequency of scanned AP
     */
    val frequency: String?,

    /**
    Returns the received signal strength indicator of the current 802.11network, in dBm. Ex:-57
     */
    val rssiLevel: String?,

    /**
    Friendly name of operator, NA if not available
     */
    val operatorFriendlyName: String?,

    /**
    Returns False = 0, True = 1
     */
    val passportNetwork: String?,

    /**
    epresents beaconID/name of scanned device
     */
    val ssid: String?,

    /**
    Represents Value of uptime in MS (milliseconds)
     */
    val accessPointUpTime: String?,

    /**
    UTC time at which the wifi data was captured in milliseconds
     */
    val timestamp: String?

) : BaseParentCoverageEntity("", "")