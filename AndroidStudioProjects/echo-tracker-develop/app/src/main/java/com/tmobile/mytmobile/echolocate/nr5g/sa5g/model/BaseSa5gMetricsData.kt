package com.tmobile.mytmobile.echolocate.nr5g.sa5g.model

import com.tmobile.mytmobile.echolocate.nr5g.core.oemdata.Sa5gDataMetricsWrapper

/**
 * This class holds the data of Sa5g metrics.
 * further that used for processing of Sa5g Metrics
 */
open class BaseSa5gMetricsData(

    /**
     * List<String> data to convert
     */
    var source: Any?,

    /**
     * String the time at which the event is triggered
     */
    var timeStamp: String,

    /**
     * data metrics apiVersion
     */
    var apiVersion: Sa5gDataMetricsWrapper.ApiVersion,

    /**
     * String the sessionId generated on Sa5g events
     */
    var sessionId: String
)
