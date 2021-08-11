package com.tmobile.mytmobile.echolocate.nr5g.sa5g.model

/**
 * Base class for all EchoLocate 5G intents. Contains all fields common for all EchoLocate 5G intents
 */
open class BaseSa5gData(

    /**
     * The unique session ID common to all the intents for the same session.
     */
    var sessionId: String,

    /**
     * its a unique id which will get generated at the time of insertion
     */
    var uniqueId: String
)