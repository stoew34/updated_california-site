package com.tmobile.mytmobile.echolocate.authentication.datevents

import com.tmobile.mytmobile.echolocate.communicationbus.events.EchoLocateBaseEvent

/**
 * data class for holding the changes in the config json
 * @param configKey:String
 */
open class DatUpdateEvent(

        //updated dattoken
        var datToken: String


) : EchoLocateBaseEvent()