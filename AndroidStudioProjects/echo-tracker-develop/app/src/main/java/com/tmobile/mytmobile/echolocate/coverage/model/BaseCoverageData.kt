package com.tmobile.mytmobile.echolocate.coverage.model

/**
 * Base class for all EchoLocate Coverage intents.
 * Contains all fields common for all EchoLocate Coverage intents
 */
open class BaseCoverageData(

    var sessionId: String,

    /**
     * its a unique id which will get generated at the time of insertion
     */
    var uniqueId: String
)
