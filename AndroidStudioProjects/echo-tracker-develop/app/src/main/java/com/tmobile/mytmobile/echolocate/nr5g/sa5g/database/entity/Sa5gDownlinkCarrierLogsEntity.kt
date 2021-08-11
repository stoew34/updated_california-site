package com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.Sa5gDatabaseConstants

/**
 * class that declares all variables of ConnectedWifiStatusEntity
 * These are columns stored in the room data base for ConnectedWifiStatusEntity entity
 */
@Entity(
    tableName = Sa5gDatabaseConstants.SA5G_DOWNLINK_CARRIER_LOGS_TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = BaseEchoLocateSa5gEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Sa5gDownlinkCarrierLogsEntity(

    /**
     * UE in API version 1 shall return the technology type that it uses as follows:
     *
     * NR: If the UE is in EN-DC of LTE band 4 and NR band 71, the NR carrier component will be reported with techType = NR in one instance of DlCarrierLog while the other instance of DlCarrierLog will contain techType=LTE
     * LTE: If the UE is in EN-DC of LTE band 4 and NR band 71, the LTE carrier component will be reported with techType = LTE in one instance of DlCarrierLog while the other instance of DlCarrierLog will contain techType=NR
     * 3G: If the UE is connected to a UMTS cell, report techType=3G while only one instance of DlCarrierLog is expected.
     * 2G: If the UE is connected to a GSM cell, report techType=2G while only one instance of DlCarrierLog is expected.
     *
     * -1: If the UE is not connected to any cellular network at the time of this API call, the techType is not applicable. Hence, report -1.
     * -2: If there was an internal SW function call failure or a SW exception occurred in getting this value, return -2.
     */
    val techType: String?,

    /**
     * UE in API version 1 shall return the name of the band that UE is on as follows:
     *
     * bandNumber: The band name of the frequency on which the UE is connected
     * For example:
     * 2: LTE band 1900MHz
     * n71: NR band 600MHz
     * -1: In a 2G connection, 2G has no band number defined, which means that the bandNumber is not applicable. Hence, report -1.
     * -2: If there was an internal SW function call failure or a SW exception occurred in getting this value, return -2.
     */
    val bandNumber: String?,

    /**
     * UE in API version 1 shall return the ARFCN (Absolute Radio-Frequency Channel Number) value as follows:
     * arfcn: NR/LTE/UMTS/GSM Absolute Radio- Frequency Channel Number
     * For example:
     * 1950: for a LTE Band 4 carrier
     * 392000: for a NR Band 2 carrier
     *
     * -1: If the UE is not connected to any cellular network at the time of this API call, the arfcn is not applicable. Hence, report -1.
     * -2: If there was an internal SW function call failure or a SW exception occurred in getting this value, return -2.
     */
    val arfcn: String?,

    /**
     * UE in API version 1 shall return the bandwidth in positive number as follows:
     *
     * bandwidth: bandwidth in MHz
     * for example: 20: for 20MHz
     *
     * -1: If the UE is not connected to any cellular network at the time of this API call, the bandWidth is not applicable. Hence, report -1.
     * -2: If there was an internal SW function call failure or a SW exception occurred in getting this value, return -2.
     */
    val bandwidth: String?,

    /**
     * UE in API version 1 shall return whether a current carrier is primary or not as follows:
     *
     * 1: If the UE is in EN-DC of LTE band 4 and NR band 71, the instance for the LTE band 4 carrier will be the primary to be reported with isPrimary = 1.
     * 2: If the UE is in EN-DC of LTE band 4 and NR band 71, the instance for the LTE band 4 carrier will be the primary. In this case, the NR band 71 carrier will be reported with isPrimary = 2.
     *
     * -1: If the UE is not connected to any cellular network at the time of this API call, the isPrimary is not applicable. Hence, report -1.
     * -2: If there was an internal SW function call failure or a SW exception occurred in getting this value, return -2.
     */
    val isPrimary: String?,

    /**
     * UE in API version 1 shall return whether a current carrier is an EN-DC anchor or not as follows:
     *
     * 1: If the device is on EN-DC while the primary carrier is on LTE band 4 and the NR carrier is on NR Band 71, report isEndcAnchor=1 for the LTE band 4 carrier and isEndcAnchor=2 for the NR Band 71 carrier.
     * If the primary carrier for a connection is an NR band in 5G Standalone mode, isEndcAnchor=2 needs to be reported.
     * 2: If the device is on EN-DC while the primary carrier is on LTE band 4 and the NR carrier is on NR Band 71, report isEndcAnchor=2 for the NR band 71 carrier and isEndcAnchor=1 for the LTE Band 4 carrier.
     * If the primary carrier for a connection is an NR band in 5G Standalone mode, isEndcAnchor=2 needs to be reported.
     *
     * -1: If the device is not connected to any cellular network, -1 is to be reported.
     * -2: If there was an internal SW function call failure or a SW exception occurred in getting this value, return -2.
     */
    val isEndcAnchor: String?,

    /**
     * UE in API version 1 shall return the type of modulation that UE use in data transfer as follows:
     *
     * BPSK (Binary Phase-Shift Keying): BPSK modulation is being used
     * QPSK (Quadrature Phase Shift QPSK): modulation is being used Keying
     * 16QAM (16 Quadrature Amplitude Modulation): 16QAM modulation is being  used
     * 64QAM (64 Quadrature Amplitude Modulation): 64QAM modulation is being  used
     * 256QAM (256 Quadrature Amplitude Modulation): 256QAM modulation is being  used
     * 1024QAM (1024 Quadrature Amplitude Modulation): 1024QAM modulation is being  used
     *
     * -1: No modulation is being used such as on no cellular connection
     * -2: If there was an internal SW function call failure or a SW exception occurred in getting this value, return -2.
     */
    val modulationType: String?,

    /**
     * transmissionMode: The type of Transmission Mode
     * For example:
     * 9 for Transmission Mode 9 (TM9)
     *
     * -1: This field is not applicable to the current condition or the UE does not support this feature.
     * -2: If there was an internal SW function call failure or a SW exception occurred in getting this value, return -2.
     */
    val transmissionMode: String?,

    /**
     * UE in API version 1 shall return the number of layers that are received on the UE as follows:
     *
     * numberlayers: The number of layers (streams) being received on the UE
     * For example:
     * When 4 layers are being streamed in 4x4 MIMO, report 4.
     *
     * -1: If device is not connected to any cellular network, report -1.
     * -2: If there was an internal SW function call failure or a SW exception occurred in getting this value, return - 2.
     */
    val numberLayers: String?,

    /**
     * cellId:
     * If LTE, the 28-bit cell identity also called eCi that consists of 20-bit eNB ID and 8- bit CI. The same field is also supported on the following native Android API.
     * https://developer.android.com/reference/android/telephony/CellIdentityLte#getCi()
     * For example: 153599745
     *
     * if NR, the 36-bit NR Cell Identity (NCI).
     * Android API returns the location area code and the cell ID portions without MCC and MNC values. The purpose of this metric is to map to a unique cell managed in the network database.
     *
     * if UMTS, the 28-bit UMTS cell identity. - if GSM, the 16-bit GSM cell identity.
     *
     * if GSM, the 16-bit GSM cell identity.
     *
     * -1: This field is not applicable to the current condition or the UE does not support this feature.
     * -2: If there was an internal SW function call failure or a SW exception occurred in getting this value, return -2.
     */
    val cellId: String?,

    /**
     * UE in API version 1 shall return the physical cell id as follows:
     *
     * pci: Physical cell id
     * For example: 101
     *
     * -1: This field is not applicable to the current condition or the UE does not support this feature.
     * -2: If there was an internal SW function call failure or a SW exception occurred in getting this value, return -2.
     */
    val pci: String?,

    /**
     * UE in API version 1 shall return tracking area code as follows:
     *
     * tac: Tracking Area Code
     *
     * -1: This field is not applicable to the current condition or the UE does not support this feature.
     * -2: If there was an internal SW function call failure or a SW exception occurred in getting this value, return -2.
     */
    val tac: String?,

    /**
     * UE in API version 1 shall return the location area code as follows:
     *
     * lac: Location Area Code
     *
     * -1: This field is not applicable to the current condition or the UE does not support this feature.
     * -2: If there was an internal SW function call failure or a SW exception occurred in getting this value, return -2.
     */
    val lac: String?,

    /**
     * UE in API version 1 shall return the RSRP level on this carrier in dBm as follows:
     *
     * rsrp: RSRP level on this carrier in dBm Applicable to LTE and NR carriers
     * If the carrier is a NR carrier, use SS-RSRP value for this field.
     *
     * -999: If the device is on 2G or no cellular connection, report -999
     * -150: If there was an internal SW function call failure or a SW exception occurred in getting this value, return -2.
     */
    val rsrp: String?,

    /**
     * UE in API version 1 shall return the RSRQ level on the this carrier in dB as follows:
     *
     * rsrq: RSRQ level on this carrier in dB applicable to LTE and NR carriers
     * If the carrier is a NR carrier, use SS-RSRQ value for this field.
     *
     * -999: If the device is on 2G or no cellular connection, report -999
     * -150: If there was an internal SW function call failure or a SW exception occurred in getting this value, return -2.
     */
    val rsrq: String?,

    /**
     * rssi: carrier level on this carrier in dBm Applicable to 2G, 3G, LTE and NR
     * If the carrier is a NR carrier, use SS-RSSI value for this field.
     *
     * -999: If the device is no cellular connection, report -999
     * -150: If there was an internal SW function call failure or a SW exception occurred in getting this value, return -2.
     */
    val rssi: String?,

    /**
     * UE in API version 1 shall return the RSCP level on this carrier in dBm as follows:
     *
     * rscp: RSCP level on this carrier in dBm. Applicable in 3G
     *
     * -999: If the device is on 2G or no cellular connection, report -999
     * -150: If there was an internal SW function call failure or a SW exception occurred in getting this value, return -2.
     */
    val rscp: String?,

    /**
     * UE in API version 1 shall return the SINR level on this carrier in dB as follows:
     *
     * SINR: SINR level on this carrier in dB Applicable to LTE and NR carriers
     * If the carrier is a NR carrier, use SS- SINR value for this field.
     *
     * -999: If the device is on 2G or no cellular connection, report -999
     * -150: If there was an internal SW function call failure or a SW exception occurred in getting this value, return -2.
     */
    val sinr: String?,

    /**
     * csiRsrp: CSI RSRP level on this carrier in dBm Applicable to NR.
     *
     * -999: If device is on other technologies other than 5G or no cellular connection, report -999.
     * -150: If there was an internal SW function call failure or a SW exception occurred in getting this value, return -2.
     */
    val csiRsrp: String?,

    /**
     * UE in API version 1 shall return the CSI RSRQ level on this carrier in dB.
     *
     * CsiRsrq: CSI RSRQ level on this carrier in dB Applicable to NR
     *
     * -999: If the device is on other technologies than 5G NR or no cellular connection, report -999
     * -150: If there was an internal SW function call failure or a SW exception occurred in getting this value, return -2.
     */
    val csiRsrq: String?,

    /**
     * UE in API version 1 shall return the CSI RSSI level on the this carrier in dBm.
     *
     * csiRssi: CSI RSSI level on this carrier in dBm Applicable to NR
     *
     * -999: If the device is on other technologies than 5G NR or no cellular connection, report -999
     * -150: If there was an internal SW function call failure or a SW exception occurred in getting this value, return -2.
     */
    val csiRssi: String?,

    /**
     * UE in API version 1 shall return the CSI SINR level on this carrier in dB.
     *
     * csiSinr: CSI SINR level on this carrier in dB. Applicable to NR
     *
     * -999: If the device is on other technologies than 5G NR or no cellular connection, report -999
     * -150: If there was an internal SW function call failure or a SW exception occurred in getting this value, return -2.
     */
    val csiSinr: String?
) : BaseEntity("", "")