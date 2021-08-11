package com.tmobile.mytmobile.echolocate.nr5g.core.utils

/**
 * Created by Divya Mittal on 4/13/21
 */
import java.util.*

/**
 * class EchoAppUUIDGenerator
 *
 * class containing "static"/companion object function for
 * creation of UUID
 *
 */

class Nr5gIDGenerator {

    companion object {

        /**
         *  fun getUuid
         *
         *      creates UUID and returns in form of String
         *
         *  @Return String
         */
        @JvmStatic // allows for static calls in Java
        fun getUuid(): String {
            val uuid = UUID.randomUUID().toString()
            return uuid
        }

        /**
         * fun getTestSessionID
         *
         * generates and returns testSessionID value
         *
         * @return String
         */
        fun getTestSessionID(): String {
            val testSessionId = UUID.randomUUID().toString()
            return testSessionId
        }
    }
}