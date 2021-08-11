package com.tmobile.mytmobile.echolocate.voice.model

/**
 * The class holds data of Device Intents list
 */
data class DeviceIntents(

        /**
         * List of app tiggered call data
         */
        val appTriggeredCallData: List<AppTriggeredCallData>?,

        /**
         * List of call setting data
         */
        val callSettingData: List<CallSettingData>,

        /**
         * List of detailed call state data
         */
        val detailedCallStateData: List<DetailedCallStateData>,

        /**
         * List of ims signalling data
         */
        val imsSignallingData: List<ImsSignallingData>,

        /**
         * List of rtpdlstate data
         */
        val rtpdlStateData: List<RtpdlStateData>,

        /**
         * List of app ui call state data
         */
        val uiCallStateData: List<UiCallStateData>,

        /**
         * List of app raido Hand overdata
         */
        val radioHandoverData: List<RadioHandoverData>,

        /**
         * List of app emergency Call Timer State data
         */
        val emergencyCallTimerState: List<EmergencyCallTimerStateData>?,

        /**
         * List of app carrier Config data
         */
        val carrierConfig: CarrierConfigData?
)