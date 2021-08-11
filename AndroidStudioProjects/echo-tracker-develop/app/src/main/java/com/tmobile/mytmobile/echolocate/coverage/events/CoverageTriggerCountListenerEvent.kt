package com.tmobile.mytmobile.echolocate.coverage.events

import android.content.Intent
import com.tmobile.mytmobile.echolocate.communicationbus.events.EchoLocateBaseEvent

/**
 * Created by Mahesh Shetye on 2020-04-23
 *
 * Base class for coverage trigger count listener event
 */

class CoverageTriggerCountListenerEvent(var intent: Intent) : EchoLocateBaseEvent()
