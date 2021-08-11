package com.tmobile.mytmobile.echolocate.voice.repository.database.entity

import androidx.room.PrimaryKey
import com.tmobile.mytmobile.echolocate.voice.model.CellInfo
import com.tmobile.mytmobile.echolocate.voice.model.LocationData

/** Public class, has event info entity. Any call can inherit event info entity
 */
open class EventInfoEntity(
        /**
         *
         */
        @PrimaryKey(autoGenerate = false)
        /**
         * cell info
         */
        val cellInfo: List<CellInfo>,
        /**
         * voice location
         */
        val location: List<LocationData>
)