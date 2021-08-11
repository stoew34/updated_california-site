package com.tmobile.mytmobile.echolocate.lte.utils

/**
 *  [LteIntents] holds all the actions related to lte module
 */
object LteIntents {


    /**
     * DETAILED_APP_STATE
     *
     * use with registerLteActions method in LteModule manager to register intent
     * value diagandroid.app.receiveDetailedApplicationState
     */
    const val DETAILED_APP_STATE = "diagandroid.app.receiveDetailedApplicationState"
    const val APP_STATE_KEY = "ApplicationState"
    val APP_PACKAGE = "ApplicationPackageName"
    const val APP_INTENT_ACTION = "diagandroid.app.ApplicationState"

    /**
     * REPORT_GENERATOR_COMPONENT_NAME
     *
     * use with schedule a periodic job
     * value LteReportScheduler
     */
    const val REPORT_GENERATOR_COMPONENT_NAME = "LteReportScheduler"


}