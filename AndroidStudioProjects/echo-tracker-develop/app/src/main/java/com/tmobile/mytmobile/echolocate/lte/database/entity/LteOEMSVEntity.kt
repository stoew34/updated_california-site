package com.tmobile.mytmobile.echolocate.lte.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.lte.database.LteDatabaseConstants


/**
 * Class that declare all the variables of lte oemsv entity.
 * These are columns stored in the voice room data base
 */
@Entity(
    tableName = LteDatabaseConstants.LTE_OEMSV_TABLE_NAME, foreignKeys = [ForeignKey(
        entity = BaseEchoLocateLteEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)

data class LteOEMSVEntity(

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


) : BaseLteEntity("", "")