package com.tmobile.mytmobile.echolocate.voice.model

/**
 * The class holds call info and location info objects
 */
data class EventInfo(

        /**
         * cell info
         */
        val cellInfo: CellInfo,
        /**
         * voice location
         */
        val location: LocationData?
)