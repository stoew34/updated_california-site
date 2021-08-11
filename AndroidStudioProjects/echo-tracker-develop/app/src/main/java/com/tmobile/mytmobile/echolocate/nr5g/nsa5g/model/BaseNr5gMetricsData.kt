package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model

import com.tmobile.mytmobile.echolocate.nr5g.core.oemdata.Nr5gBaseDataMetricsWrapper

/**
 * This class holds the data of Nr5g metrics.
 * further that used for processing of Nr5g Metrics
 */
open class BaseNr5gMetricsData(

    /**
     * List<String> data to convert
     */
    var source: Any?,

    /**
     * String the time at which the event is triggered
     */
    var timeStamp: String,

    /**
     * [Nr5gBaseDataMetricsWrapper.ApiVersion] data metrics apiVersion
     */
    var apiVersion: Nr5gBaseDataMetricsWrapper.ApiVersion,

    /**
     * String the sessionId generated on Nr5g events
     */
    var sessionId: String
)
