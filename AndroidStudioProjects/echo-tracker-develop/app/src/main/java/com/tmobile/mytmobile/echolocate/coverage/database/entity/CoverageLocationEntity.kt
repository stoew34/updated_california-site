package com.tmobile.mytmobile.echolocate.coverage.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.coverage.database.CoverageDatabaseConstants

/**
 * Class to declare all the variables of LocationEntity
 * These are columns stored in the room data base
 */
@Entity(
    tableName = CoverageDatabaseConstants.COVERAGE_LOCATION_TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = BaseEchoLocateCoverageEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class CoverageLocationEntity(

    /** Latitude in degrees. */
    val latitude: String?,

    /** Longitude in degrees. */
    val longitude: String?,

    /**
     * Latitude and longitude accuracy of the field.
     * Provides a 68% confidence of being within the radius of provided value
     * around the provided latitude and longitude as described by Google with
     * its fused location API. Ex 16.995
     */
    val accuracy: String?,

    /** The altitude, in meters above the WGS 84 reference ellipsoid. Ex: 64.4000015258789 */
    val altitude: String?,

    /**
     * Bearing in degrees.
     * Bearing is the horizontal direction of travel of this device.
     * This is not related to the device orientation.
     */
    val bearing: String?,

    /** Accuracy of bearing. */
    val bearingAccuracy: String?,

    /** activity Type */
    val activityType: String?,

    /** activity Confidence */
    val activityConfidence: String?,

    /** Speed measured on device */
    val speed: String?,

    /** The name of the provider that generated this fix. In most cases, ‘fused’ will be reported.*/
    val provider: String?,

    /** Difference in the time between the occurrence of the event and the location time. */
    val locationAge: String?,

    /** Estimated speed accuracy of this location in meters per second.*/
    val speedAccuracyMetersPerSecond: String?,

    /** Estimated vertical accuracy of this location in meters.*/
    val verticalAccuracyMeters: String?,

    /**
     *  Location status For example, the following values are reported:
     *  LOCATION_READY
     *  PERMISSION_DENIED
     *  OPT_OUT
     *  LOCATION_OUTDATED
     *  LOCATION_DISABLED
     */
    val locationStatus: String?,

    /**
     * Timestamp when the location.
     * Returns ISO timestamp with format yyyy-MM-dd'T'HH:mm:ss.SSSZ.
     * For example: 2018-05-16T16:14:10.456-0700
     */
    val timestamp: String?

) : BaseParentCoverageEntity("", "")