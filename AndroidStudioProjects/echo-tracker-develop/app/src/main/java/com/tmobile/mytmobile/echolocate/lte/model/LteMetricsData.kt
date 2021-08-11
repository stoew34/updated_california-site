package com.tmobile.mytmobile.echolocate.lte.model

import com.tmobile.mytmobile.echolocate.lte.oemdata.LteBaseDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.lte.utils.LteConstants

/**
 * This class holds the data of Lte metrics.
 * further that used for processing of Lte Metrics
 */
open class LteMetricsData(

    /**
     * List<String> data to convert
     */
    var source: List<String>,

    /**
     * String the time at which the event is triggered
     */
    var timeStamp: String,

    /**
     * [LteBaseDataMetricsWrapper.ApiVersion] data metrics apiVersion
     */
    var apiVersion: LteBaseDataMetricsWrapper.ApiVersion,

    /**
     * String the sessionId generated on LTE events
     */
    var sessionId: String,

    /**
     * Optional parameter, For other applications it will be empty, you tube link.
     */
    var yTLink: String = LteConstants.EMPTY,

    /**
     * YTContentId: Optional parameter, For other applications it will be empty, you tube content id.
     */
    var yTContentId: String = LteConstants.EMPTY
)
