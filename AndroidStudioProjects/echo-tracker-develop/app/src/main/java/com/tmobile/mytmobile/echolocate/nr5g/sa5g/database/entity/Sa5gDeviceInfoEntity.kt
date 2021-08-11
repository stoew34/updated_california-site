package com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.Sa5gDatabaseConstants

/**
 * class that declares all variables of Sa5gDeviceInfoEntity
 * These are columns stored in the room data base for Sa5gDeviceInfoEntity
 */
@Entity(
    tableName = Sa5gDatabaseConstants.SA5G_DEVICE_INFO_TABLE_NAME, foreignKeys = [ForeignKey(
        entity = BaseEchoLocateSa5gEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Sa5gDeviceInfoEntity(

    /**
     * Returns the IMEI (International Mobile Equipment Identifier) value of the device
     */
    val imei: String?,

    /**
     * Returns the IMSI value of the device
     */
    val imsi: String?,

    /**
     *   Returns the MSISDN value of the device
     */
    val msisdn: String?,

    /**
     *  Returns the UUID value of the device
     */
    val uuid: String?,

    /**
     * Returns the testSessionID value of the device
     */
    val testSessionID: String?,

    /**
     * Model code of the device[End-user-visible name for the end product]
     */
    var modelCode: String,

    /**
     * OEM of the device
     */
    var oem: String

) : BaseEntity("", "")