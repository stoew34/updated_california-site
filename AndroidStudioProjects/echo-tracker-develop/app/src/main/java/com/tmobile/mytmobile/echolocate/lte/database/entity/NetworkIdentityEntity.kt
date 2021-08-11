package com.tmobile.mytmobile.echolocate.lte.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.lte.database.LteDatabaseConstants

/**
 * Class that declare all the variables of network identity entity.
 * These are columns stored in the voice room data base for network identity entity
 */
@Entity(
    tableName = LteDatabaseConstants.LTE_NETWORK_IDENTITY_TABLE_NAME, foreignKeys = [ForeignKey(
        entity = BaseEchoLocateLteEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)

data class NetworkIdentityEntity(

    /**
     * mobile country code (MCC) is used in combination with a mobile network code (MNC
     * 3 digit MCC info.
     * Report -2 if no MCC is available
     */
    val mcc: String?,

    /**
     * mobile network code (MNC) is used in combination with mobile country code (MCC)
     * 2 or 3 digit MNC.Report -2
     * if no MNC is available
     */
    val mnc: String?,

    /**
     * Tracking area code.
     * has a range of 0 to 65536.
     * Report -2 if no TAC is available
     */
    val tac: String?,

    /**
     * Type of active data connection on the device at the time of data collection
     * 1: LTE, 2: UMTS, 3:EDGE, 4:GPRS, 0:SEARCHING
     * Report -2 if no data available (negative 2)
     * NOTE: in general, -1 (negative 1) is to be returned if the requested information is not
     * supposed to be available at the time of calling this method. For instance, LAC for SCell
     * is not expected when the device is only a single carrier.
     * NOTE2: -2 (negative 2) is to be returned if the requested information is not available
     * even if it is expected.
     */
    val networkType: String?,

    /**
     * convert the timestamp as received in the intent from the OEMs to ISO timestamp with format
     * yyyy-MM-dd'T'HH:mm:ss.SSSZ.
     * For example2018-05-16T16:14:10.456-0700.
     * Unix epoch time in milliseconds
     */
    val oemTimestamp: String?,

    /**
     * Value: Wi-Fi connection status: "1" if Wi-Fi is not connected. "2" if Wi-Fi is connected (expecting data is communicated on Wi-Fi). "0" if failed
     * to detected the Wi-Fi connection status.
     */
    val wifiConnectionStatus: String?
) : BaseLteEntity("", "")