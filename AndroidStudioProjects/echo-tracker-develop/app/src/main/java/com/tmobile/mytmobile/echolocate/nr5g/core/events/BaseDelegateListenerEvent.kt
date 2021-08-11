package com.tmobile.mytmobile.echolocate.nr5g.core.events

import android.content.Intent
import com.tmobile.mytmobile.echolocate.communicationbus.events.EchoLocateBaseEvent

/**
 * This listener is used to post the intent data for periodic event triggers
 */
class BaseDelegateListenerEvent(var intent: Intent) : EchoLocateBaseEvent()
