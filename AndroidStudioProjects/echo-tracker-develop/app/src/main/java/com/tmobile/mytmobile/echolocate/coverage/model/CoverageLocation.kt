package com.tmobile.mytmobile.echolocate.coverage.model

import com.google.gson.annotations.SerializedName

/**
 *  This class captures the location details to correlate the coverage metrics.
 */
data class CoverageLocation(

    /** Latitude in degrees. */
    @SerializedName("latitude")
    val latitude: String?,

    /** Longitude in degrees. */
    @SerializedName("longitude")
    val longitude: String?,

    /**
     * Latitude and longitude accuracy of the field.
     * Provides a 68% confidence of being within the radius of provided value
     * around the provided latitude and longitude as described by Google with
     * its fused location API. Ex 16.995
     */
    @SerializedName("accuracy")
    val accuracy: String?,

    /** The altitude, in meters above the WGS 84 reference ellipsoid. Ex: 64.4000015258789 */
    @SerializedName("altitude")
    val altitude: String?,

    /**
     * Bearing in degrees.
     * Bearing is the horizontal direction of travel of this device.
     * This is not related to the device orientation.
     */
    @SerializedName("bearing")
    val bearing: String?,

    /** Accuracy of bearing. */
    @SerializedName("bearingAccuracy")
    val bearingAccuracy: String?,

    /** activity Type */
    @SerializedName("activityType")
    val activityType: String?,

    /** activity Confidence */
    @SerializedName("activityConfidence")
    val activityConfidence: String?,

    /** Speed measured on device */
    @SerializedName("speed")
    val speed: String?,

    /** The name of the provider that generated this fix. In most cases, ‘fused’ will be reported.*/
    @SerializedName("provider")
    val provider: String?,

    /** Difference in the time between the occurrence of the event and the location time. */
    @SerializedName("locationAge")
    val locationAge: String?,

    /** Estimated speed accuracy of this location in meters per second.*/
    @SerializedName("speedAccuracyMetersPerSecond")
    val speedAccuracyMetersPerSecond: String?,

    /** Estimated vertical accuracy of this location in meters.*/
    @SerializedName("verticalAccuracyMeters")
    val verticalAccuracyMeters: String?,

    /**
     *  Location status For example, the following values are reported:
     *  LOCATION_READY
     *  PERMISSION_DENIED
     *  OPT_OUT
     *  LOCATION_OUTDATED
     *  LOCATION_DISABLED
     */
    @SerializedName("locationStatus")
    val locationStatus: String?,

    /**
     * Timestamp when the location.
     * Returns ISO timestamp with format yyyy-MM-dd'T'HH:mm:ss.SSSZ.
     * For example: 2018-05-16T16:14:10.456-0700
     */
    @SerializedName("timestamp")
    val timestamp: String?
) {
    constructor() : this (
        null, null, null, null, null,
        null, null, null, null, null,
        null, null, null, null, null
    )
}
