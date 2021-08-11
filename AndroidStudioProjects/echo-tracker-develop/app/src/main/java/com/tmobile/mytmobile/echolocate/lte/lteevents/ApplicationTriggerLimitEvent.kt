package com.tmobile.mytmobile.echolocate.lte.lteevents

import com.tmobile.mytmobile.echolocate.communicationbus.events.EchoLocateBaseEvent

data class ApplicationTriggerLimitEvent(
    var isMaxTriggerLimitReached: Boolean
) : EchoLocateBaseEvent()