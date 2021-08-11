package com.tmobile.mytmobile.echolocate.reporting.utils

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Divya Mittal on 5/19/21
 */
/**
 * Utils class to handle all [Date] related operations
 */
class ReportingDateUtils {

    companion object {

        /**
         * returns date in the long format converted from string
         * @param dateStr:String string to convert
         * @return [Long]
         */
        @SuppressLint("SimpleDateFormat")
        fun convertStringToLong(dateStr: String): Long {
            return SimpleDateFormat("MM/dd/yyyy").run { parse(dateStr).time }
        }

        /**
         * returns date in the long format converted from string
         * @param dateStr:String string to convert
         * @return [Long]
         */
        @SuppressLint("SimpleDateFormat")
        fun convertFormatStringToLong(dateStr: String): Long {
            return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").run { parse(dateStr).time }
        }

        /**
         * returns date in the Shema format(yyyy-MM-dd'T'HH:mm:ssZ) converted from string
         * @param dateString:String string to convert
         * @return [Long]
         * format:
         * [yyyy-MM-dd'T'HH:mm:ssZ]
         * yyyy-MM-dd'T'HH:mm:ss.SSSZ
         * "2019-10-25T16:14:10.456-0700"
         */

        @SuppressLint("SimpleDateFormat")
        fun convertToShemaDateFormat(dateString: String): String {
            if (dateString.isEmpty()) {
                return ""
            }
            try {
                return getFormattedTime(dateString.toLong())
            } catch (e: ParseException) {
            }
            return dateString
        }

        /**
         * Converts long to string with the format specified
         * @param timeStamp: Long the value to convert
         * @return [String] the formatted time in string
         */
        @SuppressLint("SimpleDateFormat")
        fun getFormattedTime(timeStamp: Long): String {
            val formatString = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
            val formatter = SimpleDateFormat(formatString, Locale.US)
            return formatter.format(Date(timeStamp))
        }

        /**
         * Used for file name of saved report(lte) for QA
         * @param dateString:String string to convert
         * @return [Long]
         * format:
         * "M-dd-yyyy-hh-mm-ss-z"
         */

        @SuppressLint("SimpleDateFormat")
        fun convertToFileNameFormat(date: Date): String {
            val formatString = "MMddyyyy-hh-mm-ss"
            try {
                val formatter = SimpleDateFormat(formatString, Locale.US)
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = date.time
                return formatter.format(calendar.time)
            } catch (e: ParseException) {
                ReportingLog.eLogE("error: ${e.localizedMessage}")
            }
            return ""
        }

        /**
         * returns date in the Shema format(yyyy-MM-dd'T'HH:mm:ssZ) converted from string
         * @param dateString:String string to convert
         * @return [Long]
         * format:
         * [yyyy-MM-dd'T'HH:mm:ssZ]
         * yyyy-MM-dd'T'HH:mm:ss.SSSZ
         * "2019-10-25T16:14:10.456-0700"
         */

        @SuppressLint("SimpleDateFormat")
        fun convertToMillisFormat(dateString: String): String {
            val formatString = "SSSZ"
            try {
                val formatter = SimpleDateFormat(formatString, Locale.US)
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = dateString.toLong()
                return formatter.format(calendar.time)
            } catch (e: ParseException) {
            }
            return dateString
        }

        /**
         * returns date in [Long] which is [days] before the current date
         * @param days:Int
         * @return [Long]
         */
        fun getDateBeforeDays(days: Int): Long {
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_MONTH, -days)
            return cal.timeInMillis
        }

        /**
         * returns current time in millis
         * @return [Long]
         */
        fun getCurrentTimeInMillis(): Long {
            return Calendar.getInstance().timeInMillis
        }

        /**
         * Check if the date passed can be parsed
         * @return [Boolean]
         */
        @SuppressLint("SimpleDateFormat")
        fun isDateParsable(dateStr: String): Boolean {
            return try {
                SimpleDateFormat("MM/dd/yyyy").run { parse(dateStr) }
                true
            } catch (e: Exception) {
                false
            }

        }

        /**
         * converts hours to days
         * @param reportingInterval:Int hours to be converted
         * @return Int: returns the converted hours as int
         */
        fun convertHoursToDays(reportingInterval: Int): Int {
            return reportingInterval / 24
        }

        /**
         * returns date and time with yyyy-MM-dd'T'HH:mm:ss.SSSZ format
         */
        @SuppressLint("SimpleDateFormat")
        fun getTriggerTimeStamp(): String {
//            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US)
            return sdf.format(Date())
        }

    }


}