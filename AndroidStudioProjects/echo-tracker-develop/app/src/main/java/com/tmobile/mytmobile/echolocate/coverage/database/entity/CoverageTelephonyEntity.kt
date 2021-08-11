package com.tmobile.mytmobile.echolocate.coverage.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.coverage.database.CoverageDatabaseConstants

/**
 * Class to declare all the variables of TelephonyEntity
 * These are columns stored in the room data base
 */

@Entity(
    tableName = CoverageDatabaseConstants.COVERAGE_TELEPHONY_TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = BaseEchoLocateCoverageEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class CoverageTelephonyEntity(
    /**
     * sim state type- READY
     */
    val simState: String?,

    /**
     * DOMESTIC
     */
    val roamingNetwork: String?,

    /**
     * In telephony Manager check TelephonyProperties for PROPERTY_OPERATOR_ISROAMING.
     * Returns true if the device is considered roaming on the current network for a subscription. Ex:false
     */
    val roamingVoice: String?,

    /**
     * the NETWORK_TYPE_xxxx for current data connection.
     */
    val networkType: String?,

    /**
     * An integer corresponding to the following fields:
     * 0 - IN_SERVICE
     * 1 - OUT_OF_SERVICE
     * 2 - EMERGENCY_ONLY
     * 3 - POWER_OFF
     */
    val serviceState: String?

) : BaseParentCoverageEntity("", "")