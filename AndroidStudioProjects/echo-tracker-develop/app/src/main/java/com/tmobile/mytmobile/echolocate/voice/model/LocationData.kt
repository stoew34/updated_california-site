package com.tmobile.mytmobile.echolocate.voice.model


/**
 * Model call for location info
 */
data class LocationData(

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
         * This is the value in nanoSeconds representing the difference in the time between the occurrence of the event and the location time.
         */
        val locationAge: Long? = 0

){
        /**
         * Do not initialize this parameter
         */
        var timestamp: String? = null
}