package com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.Sa5gDatabaseConstants

/**
 * class that declares all variables of Sa5gLocationEntity
 * These are columns stored in the room data base for Sa5gLocationEntity
 */
@Entity(
    tableName = Sa5gDatabaseConstants.SA5G_NETWORK_LOG_TABLE_NAME, foreignKeys = [ForeignKey(
        entity = BaseEchoLocateSa5gEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Sa5gNetworkLogEntity(

    /**
     * Returns the 3-digit MCC of the connected network.
     * Notes: Returns -2 if no data is available
     */
    val mcc: String?,

    /**
     * Returns the MNC of the connected network. MNC is either a 1, 2 or 3 digit number.
     * Notes: Returns -2 if no data is available
     */
    val mnc: String?,

    /**
     * [1|2|-999|-2]
     * report if the upperLayerIndication-r15 in SystemInformatinoBlockType2 was received from the LTE serving cell (Reference: 3GPP TS 36.331-
     * f31 6.3.1). This metric will be used to identify if the UE is in the LTE coverage where 5G NR coverage is also expected.
     * Report 1 (ON) if UE received the upperLayerIndication-r15 indicating that the LTE serving cell is capable of EN-DC.
     * Report 2 (OFF) if UE didn't receive the upperLayerIndication-r15 from the LTE serving cell.
     * Report -999 if 'networkType' is not 1 (LTE or LTE with EN-DC).
     * Report -2 if the return value is not available even if 'networkType' is 1 (LTE or LTE with EN-DC).
     * Note: If UE confirmed that the LTE PCell didn’t send upperLayerIndication-r15 value in SystemInformatinoBlockType2,
     * it’s 2 (OFF). If UE was unable to confirm either 1
     * (ON) or 2 (OFF) in LTE, report -2. In other words, 2 (OFF) means 'UE knows that the LTE serving cell is not capable of EN-DC',
     * and -2 means 'UE does not know whether the LTE serving cell is capable of EN-DC or not'.
     */
    val endcCapability: String?,

    /**
     * Returns endc Connection Status
     * UE in API version 1 shall report whether NR leg is added or not in the EN-DC capable condition as follows:
     * <p>
     * 1: The device is in an active EN-DC connection. NR leg is added. UE is connected to a NR cell.
     * 2: The device is on an LTE connection where EN-DC is capable but not yet connected to an NR cell. NR leg is not added. UE is not connected to a NR cell.
     */
    val endcConnectionStatus: String?

) : BaseEntity("", "")