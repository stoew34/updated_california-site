package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.Nr5gDatabaseConstants

/**
 * class that declares all variables of ConnectedWifiStatusEntity
 * These are columns stored in the room data base for ConnectedWifiStatusEntity entity
 */
@Entity(
    tableName = Nr5gDatabaseConstants.NR5G_CONNECTED_WIFI_STATUS_TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = BaseEchoLocateNr5gEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class ConnectedWifiStatusEntity(

    /**
     * Returns the BSSID of the scanned device as a string.
     */
    val bssId: String,

    /**
     *  Returns the BSSLoad of the scanned device as a string.
     */
    val bssLoad: String,

    /**
     * Returns the beaconID/name of scanned device as a string.
     */
    val ssId: String,

    /**
     * Returns the uptime of the scanned device in miliseconds
     */
    val accessPointUpTime: Int,

    /**
     *  Returns the capabilities of the scanned device.
     */
    val capabilities: String,

    /**
     * Returns the value of the center frequency as an integer.
     */
    val centerFreq0: Int,

    /**
     * Returns the value of the second center frequency as an integer.
     */
    val centerFreq1: Int,

    /**
     * Returns a string value which represents the channel mode.
     */
    val channelMode: String,

    /**
     * Returns an integer which represents the width of the channel.
     */
    val channelWidth: Int,

    /**
     *  Returns the frequency of the scanned AP.
     */
    val frequency: Int,

    /**
     * Returns the friendly name of operator as a string.
     *  Notes: “NA” if not available
     */
    val operatorFriendlyName: String,

    /**
     *  Returns an integer which corresponds to the following values:
     *  [0] False
     *  [1] True
     */
    val passportNetwork: Int,

    /**
     *  Returns the received signal level as an integer.
     */
    val rssiLevel: Int
) : BaseEntity("", "")