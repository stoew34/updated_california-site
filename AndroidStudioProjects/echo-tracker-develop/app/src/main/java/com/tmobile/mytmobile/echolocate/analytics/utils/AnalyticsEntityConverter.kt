package com.tmobile.mytmobile.echolocate.analytics.utils

import com.tmobile.mytmobile.echolocate.analytics.database.entity.AnalyticsEventEntity
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticsEvent
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils

class AnalyticsEntityConverter {
    companion object {

        /**
         * Converts [ELAnalyticsEvent] to [AnalyticsEventEntity]
         * @param elAnalyticsEvent: [ELAnalyticsEvent]
         * @return [AnalyticsEventEntity]
         */
        fun convertAnalyticsEventEntity(
            elAnalyticsEvent: ELAnalyticsEvent
        ): AnalyticsEventEntity {
            return AnalyticsEventEntity(
                EchoLocateDateUtils.convertToShemaDateFormat(elAnalyticsEvent.timeStamp.toString()),
                elAnalyticsEvent.moduleName!!.name,
                elAnalyticsEvent.action.name,
                elAnalyticsEvent.payload
            )
        }
    }
}