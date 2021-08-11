package com.tmobile.mytmobile.echolocate.reporting

import com.tmobile.myaccount.events.diagnostics.pojos.collector.event.ClientSideEvent

interface IReportProvider {

    fun initReportingModule(): IReportProvider

    /**
     * This function is used to tell the reporting module that data collection module should be included in the report from the module
     * who is calling this function. The data from a module will only be included in the final report if the data collection module
     * call this function and registers with the reporting module.
     *
     * The report types is reference to class that implements [IBaseReportDataRequestor]
     *
     */
    fun registerReportTypes(reportType: IBaseReportDataRequestor)

    /**
     * This function is used to tell the reporting module to exclude the calling module from the reports.
     *
     * The report types is reference to class that implements [IBaseReportDataRequestor]
     *
     */
    fun unRegisterReportTypes(reportType: IBaseReportDataRequestor)

    /**
     * This function is used create the report for Debug mode
     */
    fun instantRequestReportsFromAllModules(androidWorkId: String? = null)

    fun stopReportingModule()

    /** This fun used for instant Send Reports from data collection modules*/
    fun performReportSending(reportEventList: List<ClientSideEvent>)

    /**
     * Check if reporting module is enabled or not
     */
    fun isReportingModuleEnabled(): Boolean

    fun idDropBox(fileID: Collection<String>): Boolean

}

