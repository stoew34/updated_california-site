package com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.Sa5gDatabaseConstants

/**
 * class that declares all variables of Sa5gLocationEntity
 * These are columns stored in the room data base for Sa5gLocationEntity
 */
@Entity(
    tableName = Sa5gDatabaseConstants.SA5G_LOCATION_TABLE_NAME, foreignKeys = [ForeignKey(
        entity = BaseEchoLocateSa5gEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)

data class Sa5gLocationEntity(

    /**
     * This is the double value representing the altitude of the location of the device at the time of the event.
     */
    val altitude: Double?,
    /**
     * This is the double value representing precision of the location altitude of the device at the time of the event.
     */
    val altitudePrecision: Float?,
    /***
     * This is the double value of the user's location altitude at the time of the scan. 'BALANCED_POWER' option may be used to fix the location.
     */
    val latitude: Double?,
    /**
     * This is the double value of the user's location longitude at the time of the scan. 'BALANCED_POWER' option may be used to fix the location.
     */
    val longitude: Double?,
    /**
     * This is the double value of the user's location precision, also known as the location accuracy at the time of the scan
     */
    val precision: Float?,
    /**
     * Timestamp when the location is requested by the application. Returns ISO timestamp with format yyyy-MM-dd'T'HH:mm:ss.SSSZ. For example: 2018-05-16T16:14:10.456-0700
     */
    val timestamp: String?,
    /**
     * This is the value in nanoSeconds representing the difference in the time between the occurrence of the event and the location time.
     */
    val locationAge: Long?
) : BaseEntity("", "")