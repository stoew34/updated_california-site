package com.tmobile.mytmobile.echolocate.lte.model

/**
 * Base class for all EchoLocate Lte intents. Contains all fields common for all EchoLocate Lte intents
 */
open class BaseLteData(

    var sessionId: String,

    /*
    * its a unique id which will get generated at the time of insertion
    */
    var uniqueId: String
)
