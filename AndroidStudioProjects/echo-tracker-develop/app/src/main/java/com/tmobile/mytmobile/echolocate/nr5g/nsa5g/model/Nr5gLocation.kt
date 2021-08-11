package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model

/**
 * Model class that holds call Nr5gLocation data
 *
 * Serialized name annotation for all variables to be in JSON,
 * and it will be helpful while doing progaurding
 */
data class Nr5gLocation(

    /**
     * This is the double value representing the altitude of the location of the device at the time of the event.
     */
    val altitude: Double? = 0.0,
    /**
     * This is the double value representing precision of the location altitude of the device at the time of the event.
     */
    val altitudePrecision: Float? = 0.0F,
    /***
     * This is the double value of the user's location altitude at the time of the scan. 'BALANCED_POWER' option may be used to fix the location.
     */
    val latitude: Double? = 0.0,
    /**
     * This is the double value of the user's location longitude at the time of the scan. 'BALANCED_POWER' option may be used to fix the location.
     */
    val longitude: Double? = 0.0,
    /**
     * This is the double value of the user's location precision, also known as the location accuracy at the time of the scan
     */
    val precision: Float? = 0.0F,
    /**
     * Timestamp when the location is requested by the application. Returns ISO timestamp with format yyyy-MM-dd'T'HH:mm:ss.SSSZ. For example: 2018-05-16T16:14:10.456-0700
     */
    val timestamp: String? = "",
    /**
     * This is the value in nanoSeconds representing the difference in the time between the occurrence of the event and the location time.
     */
    val locationAge: Long? = 0
)