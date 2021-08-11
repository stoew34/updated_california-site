package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model

import com.google.gson.annotations.SerializedName

/**
 * Model class that holds call Nr5gDeviceInfo data
 *
 * Serialized name annotation for all variables to be in JSON,
 * and it will be helpful while doing progaurding
 */
data class Nr5gDeviceInfo(

        /**
         * Returns the IMEI (International Mobile Equipment Identifier) value of the device
         */
        @SerializedName("IMEI")
        val imei: String,

        /**
         * Returns the IMSI value of the device
         */
        @SerializedName("IMSI")
        val imsi: String,

        /**
         *   Returns the MSISDN value of the device
         */
        @SerializedName("MSISDN")
        val msisdn: String?,

        /**
         *  Returns the UUID value of the device
         */
        @SerializedName("UUID")
        val uuid: String,

        /**
         * Returns the testSessionID value of the device
         */
        @SerializedName("testSessionID")
        val testSessionID: String,

        /**
         * Model code of the device[End-user-visible name for the end product]
         */
        @SerializedName("modelCode")
        var modelCode: String,

        /**
         * OEM of the device
         */
        @SerializedName("OEM")
        var oem: String


)