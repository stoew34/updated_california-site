package com.tmobile.mytmobile.echolocate.utils

/**
 * Util class that handles parsing of Strings
 */
class NumberUtils {

    companion object {
        /**
         * Converts the given value to int
         * @param value: String the value to convert
         * @return [Int] returns the converted value
         */
        fun convertToInt(value: String?): Int? {
            return try {
                value?.toInt()
            } catch (ex: KotlinNullPointerException) {
                EchoLocateLog.eLogE(ex.toString())
                null
            } catch (ex: NumberFormatException) {
                EchoLocateLog.eLogE(ex.toString())
                null
            }
        }

        /**
         * Converts the given value to float
         * @param value: String the value to convert
         * @return [Float] returns the converted value
         */
        fun convertToFloat(value: String?): Float? {
            return try {
                value?.toFloat()
            } catch (ex: NumberFormatException) {
                EchoLocateLog.eLogE(ex.toString())
                null
            }
        }

        /**
         * Converts the given value to long
         * @param value: String the value to convert
         * @return [Int] returns the converted value
         */
        fun convertToLong(value: String?): Long? {
            return try {
                value?.toLong()
            } catch (ex: KotlinNullPointerException) {
                EchoLocateLog.eLogE(ex.toString())
                null
            }
        }
    }
}