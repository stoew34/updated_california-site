package com.tmobile.mytmobile.echolocate.coverage.model

data class CoverageTrigger(
    /**
     *     An integer that corresponds to the event that triggers the data collection as follows:
     *     1 – Device screen activity (Device's interactive toggles between ON and OFF, which is a system protected property. The system will send a screen on or screen off broadcast whenever the interactive state of the device changes. Limited to 3 times per hour.)
     *     4 – Voice call start (Triggers when Telephony Manager changes to CALL_STATE_OFFHOOK)
     *     5 – Voice call end (Triggers when Telephony Manager changes from CALL_STATE_OFFHOOK to CALL_STATE_IDLE)
     *     6 – Device diagnostics (Triggers when TEST_COVERAGE_ON_DIAGNOSTIC trigger is received as an intent)
     *     Note: Hourly limit of ‘1’ events will be configurable in a future version Example:SCREEN_ACTIVITY
     */
    val trigger: Int

)