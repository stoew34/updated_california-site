package com.tmobile.mytmobile.echolocate.lte.lteevents

import android.content.Intent
import com.tmobile.mytmobile.echolocate.communicationbus.events.EchoLocateBaseEvent

/**
 * This listener is used to post the intent data for Nr5g reset after 24 hours
 */
class LteResetTriggerCountListenerEvent(var intent: Intent) : EchoLocateBaseEvent()
