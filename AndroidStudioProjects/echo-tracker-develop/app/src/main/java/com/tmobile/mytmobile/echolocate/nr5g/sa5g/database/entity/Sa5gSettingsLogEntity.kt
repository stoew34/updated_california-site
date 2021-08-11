package com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.Sa5gDatabaseConstants

/**
 * class that declares all variables of Sa5gOEMSVEntity
 * These are columns stored in the room data base for Sa5gOEMSVEntity
 */
@Entity(
    tableName = Sa5gDatabaseConstants.SA5G_SETTINGS_LOG_TABLE_NAME, foreignKeys = [ForeignKey(
        entity = BaseEchoLocateSa5gEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Sa5gSettingsLogEntity(

    /**
     * Provides the ability to make and receive phone calls over a Wi-Fi connection
     * 1 if off
     * 2 if WiFi preferred
     * 3 if cellular preferred
     * 4 if never use cellular
     * -1 if this setting does not exist
     * -2 if failed to check this info
     */
    val wifiCalling: String?,

    /**
     * Wifi setting represents the state of the Wifi on the device under test.
     * 1 if WiFi setting is off
     * 2 if on
     * -1 if this setting does not exist
     * -2 if failed to check this info
     */
    val wifi: String?,

    /**
     * Data Roaming setting value
     * 1 if data roaming is off
     * 2 if on
     * -1 if this setting does not exist
     * -2 if failed to check this info
     * 2.2.12.7 str[6] return - VoLTE setting
     */
    val roaming: String?,

    /**
     * 1 if off
     * 2 if on with ‘Visible During Calls’ option
     * 3 if on with ‘Always Visible – Manual’ option
     * 4 if on with ‘Always Visible – Automatic’ option
     * -1 if this setting does not exist
     * -2 if failed to check this info
     */
    val rtt: String?,

    /**
     * 1 if off
     * 2 if on
     * -1 if this setting does not exist
     * -2 if failed to check this info
     */
    val rttTranscript: String?,

    /**
     * Integer value which determines the value of the network mode on the device under test.
     * 1 if LTE/WCDMA/GSM auto
     * 2 if WCDMA/GSM auto
     * 3 if WCDMA only
     * 4 if GSM only
     * 5 if LTE/WCDMA auto
     * 6 if LTE only
     * 7 if LTE/GSM auto
     * 8 if NR/LTE/WCDMA/GSM auto
     * -1 if this setting does not exist
     * -2 if failed to check for this field
     */
    val networkMode: String?

) : BaseEntity("", "")