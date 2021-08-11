/*
 * Copyright (c) 2018. T-Mobile USA, Inc. – All Rights Reserved
 * Not for release external to T-Mobile USA and partners under contract.
 * Source code subject to change. Refer to Notices.txt in source tree for changes and attributions.
 */

package com.tmobile.mytmobile.echolocate.dateutils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Class used to format time tmobile way
 */
open class TMobileDateFormat : SimpleDateFormat {

    /**
     * If you need UTC time.
     */
    constructor() : super(DATE_FORMAT, LOCALE) {
        timeZone = UTC_ZONE
    }

    /**
     * If you need device time.
     */
    constructor(dateFormat: String) : super(dateFormat, LOCALE)

    /**
     * Format time which is provided in milliseconds
     *
     * @param milliseconds time in millis
     * @return time as string in proper format
     */
    fun format(milliseconds: Long): String {
        val date = Date()
        date.time = milliseconds

        return format(date)
    }

    companion object {

        private const val serialVersionUID = -7138672384036693258L

        /**
         * Format string
         */
        private const val DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'"

        /**
         * File must be saved with date format without colons, because Windows can't read the file.
         */
        private const val DATE_FORMAT_FOR_WINDOWS_WITHOUT_COLONS = "yyyy-MM-dd'T'HH-mm-ss-SSS'Z'"

        /**
         * Time zone
         */
        val UTC_ZONE: TimeZone = TimeZone.getTimeZone("UTC")

        /**
         * Locale
         */
        val LOCALE: Locale = Locale.US

        /**
         * Connection status bar date format
         */
        val CONNECTION_STATUS_DATE_FORMAT = "MM/dd/yy"

        /**
         * Connection status bar time format
         */
        val CONNECTION_STATUS_TIME_FORMAT = "hh:mm a"

        /**
         * Date format used in log files
         */
        val EXCEPTION_DATE_FORMAT = "yyyy-MM-dd kk:mm:ss.SSS"

        fun getLoggingDate(milliseconds: Long): String {
            return TMobileDateFormat(DATE_FORMAT_FOR_WINDOWS_WITHOUT_COLONS).format(milliseconds)
        }
    }
}
