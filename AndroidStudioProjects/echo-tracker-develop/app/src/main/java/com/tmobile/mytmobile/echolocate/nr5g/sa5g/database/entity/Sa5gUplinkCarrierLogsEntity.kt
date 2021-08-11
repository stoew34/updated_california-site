package com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.Sa5gDatabaseConstants

/**
 * class that declares all variables of Sa5gTriggerEntity
 * These are columns stored in the room data base for Sa5gTriggerEntity
 */
@Entity(
    tableName = Sa5gDatabaseConstants.SA5G_UPLINK_CARRIER_LOGS_TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = BaseEchoLocateSa5gEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Sa5gUplinkCarrierLogsEntity(

    /**
     * Returns:
     * [NR|LTE|3G|2G|NO_SIGNAL]
     * 3G for UMTS and 2G for GSM
     * NR for 5G New Radio
     * LTE for LTE
     * NO_SIGNAL if device has no signal
     * UE in API version 1 shall return the technology type that it uses as follows:
     * <p>
     * NR: If the device is on a 5G SA connection, report NR for the NR carrier.
     * LTE: If the device is on an EN-DC connection, report LTE for the LTE carrier.
     * 3G: If the device is on a 3G connection, report 3G
     * 2G: If the device is on a 2G connection, report 2G
     * <p>
     * -1: If the UE is not connected to any cellular network at the time of this API call, the techType is not applicable. Hence, report -1.
     * -2: If there was an internal SW function call failure or a SW exception occurred in getting this value, return -2.
     */
    val techType: String?,

    /**
     * band number
     * For instance, 2 for LTE band 1900MHz and n71 for NR band 600MHz
     * UE in API version 1 shall return the name of the band that UE is on as follows:
     * bandNumber: The band name of the frequency on which the UE is connected
     * For example:
     * 2: For LTE band 1900MHz
     * n71: For NR band 600MHz
     * <p>
     * -1: In a 2G connection, 2G has no band number defined, which means that the bandNumber is not applicable. Hence, report -1.
     * -2: If there was an internal SW function call failure or a SW exception occurred in getting this value, return -2.
     */
    val bandNumber: String?,

    /**
     * Returns NR/LTE/UMTS/GSM ARFCN (absolute radio-frequency channel number)
     *
     * UE in API version 1 shall return the ARFCN (Absolute Radio-Frequency Channel Number) value as follows:
     * <p>
     * arfcn: NR/LTE/UMTS/GSM Absolute Radio- Frequency Channel Number
     * For example:
     * 1950: for a LTE Band 4 carrier
     * 392000: for a NR Band 2 carrier
     * <p>
     * -1: If the UE is not connected to any cellular network at the time of this API call, the arfcn is not applicable. Hence, report -1.
     * -2: If there was an internal SW function call failure or a SW exception occurred in getting this value, return -2.
     */
    val arfcn: String?,

    /**
     * UE in API version 1 shall return the bandwidth in positive number as follows:
     * <p>
     * bandwidth: bandwidth in MHz
     * for example: 20: for 20MHz
     * <p>
     * -1: If the UE is not connected to any cellular network at the time of this API call, the bandWidth is not applicable. Hence, report -1.
     * -2: If there was an internal SW function call failure or a SW exception occurred in getting this value, return -2.
     */
    val bandwidth: String?,

    /**
     * UE in API version 1 shall return whether a current carrier is primary or not as follows:
     * <p>
     * 1: If the UE is in EN-DC of LTE band 4 and NR band 71, the instance for the LTE band 4 carrier will be the primary to be reported with isPrimary = 1.
     * 2: If the UE is in EN-DC of LTE band 4 and NR band 71, the instance for the LTE band 4 carrier will be the primary. In this case, the NR band 71 carrier will be reported with isPrimary = 2.
     * <p>
     * -1: If the UE is not connected to any cellular network at the time of this API call, the isPrimary is not applicable. Hence, report -1.
     * -2: If there was an internal SW function call failure or a SW exception occurred in getting this value, return -2.
     */
    val isPrimary: String?

) : BaseEntity("", "")