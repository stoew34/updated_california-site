package com.tmobile.mytmobile.echolocate.voice.model


/**
 * Base class for all EchoLocate intents. Contains all fields common for all EchoLocate intents
 */
open class BaseVoiceData(
        /**
         * The unique call ID common to all the intents for the same call session.
         * Due to possibility of incrementing the call id from 0 for every reset of the device
         * after reboot additional number will be prepended before call ids of not sent events.
         *This value is retrieved from the OEM intent for call state.
         *Intent extra key "CallID".
         */
        var callId: String,

        /**
         * its a unique id which will get generated at the time of insertion
         */
        var uniqueId: String
)