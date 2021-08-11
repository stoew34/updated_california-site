package com.tmobile.mytmobile.echolocate.lte.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.lte.database.LteDatabaseConstants

/**
 * Class that declare all the variables of lte location entity.
 * These are columns stored in the voice room data base for lte location entity
 */
@Entity(
    tableName = LteDatabaseConstants.LTE_LOCATION_TABLE_NAME, foreignKeys = [ForeignKey(
        entity = BaseEchoLocateLteEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)

data class LteLocationEntity(

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
     * This is the value in nanoSeconds representing the difference in the time between the occurrence of the event and the location time.
     */
    val locationAge: Long?,

    /**
     * timestamp
     */
    var timestamp: String? = null

) : BaseLteEntity("", "")