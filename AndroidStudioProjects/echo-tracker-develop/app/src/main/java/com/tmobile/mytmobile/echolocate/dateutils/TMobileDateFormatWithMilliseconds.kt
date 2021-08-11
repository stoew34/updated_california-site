/*
 * Copyright (c) 2018. T-Mobile USA, Inc. – All Rights Reserved
 * Not for release external to T-Mobile USA and partners under contract.
 * Source code subject to change. Refer to Notices.txt in source tree for changes and attributions.
 */

package com.tmobile.mytmobile.echolocate.dateutils

/**
 * Created by maciej.srokowski@mobica.com
 * Class extending standard used Date Format by appending milliseconds on the end. Added for use
 * with Echo Locate.
 */
class TMobileDateFormatWithMilliseconds : TMobileDateFormat(DATE_FORMAT_WITH_MILLISECONDS) {
    companion object {

        /**
         * Format string with milliseconds
         */
        val DATE_FORMAT_WITH_MILLISECONDS = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
        private val serialVersionUID = 9028130011172651286L
    }
}
