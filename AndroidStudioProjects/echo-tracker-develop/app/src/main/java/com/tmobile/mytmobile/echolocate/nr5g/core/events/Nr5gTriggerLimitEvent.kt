package com.tmobile.mytmobile.echolocate.nr5g.core.events

import com.tmobile.mytmobile.echolocate.communicationbus.events.EchoLocateBaseEvent

/**
 * This listener is used to post the intent data for Nr5g trigger limit
 */
data class Nr5gTriggerLimitEvent(
    var isMaxTriggerLimitReached: Boolean,
    var triggerType: String
) : EchoLocateBaseEvent()