package com.tmobile.mytmobile.echolocate.lte.lteevents

import android.content.Intent
import com.tmobile.mytmobile.echolocate.communicationbus.events.EchoLocateBaseEvent

/**
 * This listener is used to post the intent data for LTE Hourly trigger
 */
class LteHourlyListenerEvent(var intent: Intent) : EchoLocateBaseEvent()