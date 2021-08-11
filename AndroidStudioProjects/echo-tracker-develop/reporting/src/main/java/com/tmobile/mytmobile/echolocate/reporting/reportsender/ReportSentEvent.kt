package com.tmobile.mytmobile.echolocate.reporting.reportsender

import com.tmobile.mytmobile.echolocate.communicationbus.events.EchoLocateBaseEvent

class ReportSentEvent(var totalNumberOfReports: Int, var numberOfReportsSent: Int) :
    EchoLocateBaseEvent()

const val NO_REPORTS_SENT_TO_NETWORK = -1