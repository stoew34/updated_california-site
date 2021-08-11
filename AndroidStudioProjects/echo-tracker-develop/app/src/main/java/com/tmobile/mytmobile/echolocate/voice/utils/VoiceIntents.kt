package com.tmobile.mytmobile.echolocate.voice.utils

/**
 *  [VoiceIntents] holds all the actions related to voice module
 */
object VoiceIntents {

    /**
     * REPORT_GENERATOR_COMPONENT_NAME
     *
     * use with schedule a periodic job
     * value VoiceReportScheduler
     */
    const val REPORT_GENERATOR_COMPONENT_NAME = "VoiceReportScheduler"

    /**
     * DETAILED_CALL_STATE
     *
     * use with registerVoiceActions method to register intent
     * value diagandroid.phone.detailedCallState
     */
    const val DETAILED_CALL_STATE = "diagandroid.phone.detailedCallState"

    /**
     * UI_CALL_STATE
     *
     * use with {@link #registerVoiceActions()} method to register intent
     * value "diagandroid.phone.UICallState
     */
    const val UI_CALL_STATE = "diagandroid.phone.UICallState"

    /**
     * RADIO_HAND_OVER_STATE
     *
     * use with registerVoiceActions method to register intent
     * value diagandroid.phone.VoiceRadioBearerHandoverState
     */
    const val RADIO_HAND_OVER_STATE = "diagandroid.phone.VoiceRadioBearerHandoverState"

    /**
     * IMS_SIGNALING_MESSAGE
     *
     * use with registerVoiceActions method to register intent
     * value diagandroid.phone.imsSignallingMessage
     */
    const val IMS_SIGNALING_MESSAGE = "diagandroid.phone.imsSignallingMessage"

    /**
     * APP_TRIGGERED_CALL
     *
     * use with registerVoiceActions method to register intent
     * value diagandroid.phone.AppTriggeredCall
     */
    const val APP_TRIGGERED_CALL = "diagandroid.phone.AppTriggeredCall"

    /**
     * CALL_SETTING
     *
     * use with registerVoiceActions method to register intent
     * value diagandroid.phone.CallSetting
     */
    const val CALL_SETTING = "diagandroid.phone.CallSetting"

    /**
     * RTPDL_STAT
     *
     * use with registerVoiceActions method to register intent
     * value diagandroid.phone.RTPDLStat
     */
    const val RTPDL_STAT = "diagandroid.phone.RTPDLStat"

    /**
     * EMERGENCY_CALL_TIMER_STATE
     *
     * use with registerVoiceActions method to register intent
     * value diagandroid.phone.emergencyCallTimerState
     */
    const val EMERGENCY_CALL_TIMER_STATE = "diagandroid.phone.emergencyCallTimerState"

    /**
     * CARRIER_CONFIG
     *
     * use with registerVoiceActions method to register intent
     * value diagandroid.phone.carrierConfig
     */
    const val CARRIER_CONFIG = "diagandroid.phone.carrierConfig"
}