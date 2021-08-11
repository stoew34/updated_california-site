package com.tmobile.mytmobile.echolocate.nr5g.core.events

import android.content.Intent
import com.tmobile.mytmobile.echolocate.communicationbus.events.EchoLocateBaseEvent

/**
 * This listener is used to post the intent data for Nr5g reset after 24 hours
 */
class Nr5gResetListenerEvent(var intent: Intent) : EchoLocateBaseEvent()
