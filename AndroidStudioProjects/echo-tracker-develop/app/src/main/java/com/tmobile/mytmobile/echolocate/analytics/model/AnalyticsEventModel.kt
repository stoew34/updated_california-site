package com.tmobile.mytmobile.echolocate.analytics.model

import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticActions
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELModulesEnum

/**
 *
 * Model class used for generate Analytics report
 */
data class AnalyticsEventModel(

    /**
     * name of the module reporting the analytics event.
     * This is mandatory field
     */
    var moduleName: ELModulesEnum? = null,

    /**
     * Action that is being reported.
     * This is mandatory field
     */
    var action: ELAnalyticActions = ELAnalyticActions.EL_OTHER,

    /**
     * Any more information that needs to be reported for that Analytics event.
     * Optional
     */
    var payload: String? = null,

    /**
     * Returns timestamp in UNIX epoch time as a UTC string,
     *
     * format: "yyyy-MM-dd'T'HH:mm:ss.SSSZ", example: 2019-06-24T18:57:23.567+0000
     */
    var timestamp: String? = null

)