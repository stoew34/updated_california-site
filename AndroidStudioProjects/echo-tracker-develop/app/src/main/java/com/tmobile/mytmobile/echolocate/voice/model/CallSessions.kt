package com.tmobile.mytmobile.echolocate.voice.model

import com.tmobile.mytmobile.echolocate.standarddatablocks.OEMSV

//CallSessions structure
//CallSessions(6 elements)
//      1-networkIdentity(2)
//      2-callId(-) - from BaseVoiceEntity
//      3-callNumber(-) - from CallSessionsEntity table
//      4-clientVersion(-)  - from CallSessionsEntity table
//      5-OEMSV(5)
//      6-DeviceIntents(7)
//          1--AppTriggeredCallData[1]
//          2--CallSettingData[1]
//          3--DetailedCallStateData[2]
//          4--ImsSignallingData[1]
//          5--RtpdlStateData[1]
//          6--UiCallStateData[5]
//          7--RadioHandoverData[1]
//location(-)
//deviceInfo(-)


/**
 * Class that holds call sessions
 */

data class CallSessions(

    /**
         * Call holds network operator data info
         */
        val networkIdentity: NetworkIdentity,

    /**
         * The unique call ID common to all the intents for the same call session.
         * Due to possibility of incrementing the call id from 0 for every reset of the device
         * after reboot additional number will be prepended before call ids of not sent events.
         *This value is retrieved from the OEM intent for call state.
         *Intent extra key "CallID".
         */
        val callId: String,

    /**
         * The phone number of the other party on the call.
         */
        val callNumber: String,
    /**
         * The version of the client at the time of the event.
         */
        val clientVersion: String,
    /**
         * baseOEMSVData
         */
        val OEMSV: OEMSV,
    /**
         * basedeviceIntentsdata.
         */
        val deviceIntents: DeviceIntents
)