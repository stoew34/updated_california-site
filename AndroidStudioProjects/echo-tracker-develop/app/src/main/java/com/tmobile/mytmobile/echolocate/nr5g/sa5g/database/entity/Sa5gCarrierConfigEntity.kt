package com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.Sa5gDatabaseConstants

/**
 * class that declares all variables of Sa5gCarrierConfigEntity
 * These are columns stored in the room data base for Sa5gCarrierConfigEntity
 */
@Entity(
    tableName = Sa5gDatabaseConstants.SA5G_CARRIER_CONFIG_TABLE_NAME, foreignKeys = [ForeignKey(
        entity = BaseEchoLocateSa5gEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Sa5gCarrierConfigEntity(

    /**
     * Returns Carrier Config Version
     */
    val carrierConfigVersion: String?,

    /**
     * Returns Band Config Keys
     * Example: "SAn2Enabled", "SAn66Enabled", "NONE", "ERROR"
     */
    val bandConfigKeys: String?,

    /**
     * Returns Band Config Values
     * Example: "true", "false", "-1", "-2"
     */
    val bandConfigValues: String?

) : BaseEntity("", "")