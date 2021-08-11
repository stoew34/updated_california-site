package com.tmobile.mytmobile.echolocate.lte.lteevents

import android.content.Intent
import com.tmobile.mytmobile.echolocate.communicationbus.events.EchoLocateBaseEvent

/**
 * Created by Hitesh K Gupta on 2019-12-17
 */
/**
 * This listener is used to post the intent data for lte non stream trigger
 */
class BaseNonStreamEvent(var intent: Intent) : EchoLocateBaseEvent()