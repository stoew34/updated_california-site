package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity

import android.os.Build
import androidx.room.Entity
import androidx.room.ForeignKey
import com.google.gson.annotations.SerializedName
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.Nr5gDatabaseConstants

/**
 * class that declares all variables of Nr5gDeviceInfoEntity
 * These are columns stored in the room data base for Nr5gDeviceInfoEntity
 */
@Entity(
    tableName = Nr5gDatabaseConstants.NR5G_DEVICE_INFO_TABLE_NAME, foreignKeys = [ForeignKey(
        entity = BaseEchoLocateNr5gEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Nr5gDeviceInfoEntity(

    /**
     * Returns the IMEI (International Mobile Equipment Identifier) value of the device
     */
    val imei: String,

    /**
     * Returns the IMSI value of the device
     */
    val imsi: String,

    /**
     *   Returns the MSISDN value of the device
     */
    val msisdn: String?,

    /**
     *  Returns the UUID value of the device
     */
    val uuid: String,

    /**
     * Returns the testSessionID value of the device
     */
    val testSessionID: String,

    /**
     * Model code of the device[End-user-visible name for the end product]
     */
    val modelCode: String,

    /**
     * OEM of the device
     */
    val oem: String

) : BaseEntity("", "")