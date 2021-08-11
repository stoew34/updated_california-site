package com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.Sa5gDatabaseConstants

/**
 * class that declares all variables of Sa5gOEMSVEntity
 * These are columns stored in the room data base for Sa5gOEMSVEntity
 */
@Entity(
    tableName = Sa5gDatabaseConstants.SA5G_OEMSV_TABLE_NAME, foreignKeys = [ForeignKey(
        entity = BaseEchoLocateSa5gEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Sa5gOEMSVEntity(

    /**
     * two digit device software version
     */
    var softwareVersion: String?,

    /**
     * Android SDK Version E.g., "1.0" or "3.4b5".
     */
    val customVersion: String?,

    /**
     * Build name is either a changelist number, or a label like "M4-rc20".
     */
    val radioVersion: String?,

    /**
     * Custom version is the version is the OEM specific custom version if available
     */
    val buildName: String?,

    /**
     *Radio version is the the version string for the radio firmware
     */
    val androidVersion: String?

) : BaseEntity("", "")