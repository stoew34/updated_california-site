package com.tmobile.mytmobile.echolocate.coverage

import android.content.Context
import com.tmobile.mytmobile.echolocate.configuration.events.reportingevents.DIAReportResponseEvent
import io.reactivex.Observable

/**
 * The interface class for Coverage data collection [CoverageModuleProvider]. The functions defined here are the ones which will be responsible to external world.
 *
 * All the other functions and classes are hidden from the external world.
 */
interface ICoverageProvider {

    /**
     * Public API to initCoverageModule Coverage module.
     * This API is responsible for preparing the module to start
     * accepting the incoming data, process and store it.
     *
     * If this API is not called, the Coverage module will not start the data collection.
     */
    fun initCoverageModule(context: Context)

    /**
     * Public API to stop Coverage module. If this function is called, the Coverage module will stop working.
     *
     * Call initCoverageModule [initCoverageModule] to start Coverage module again.
     */
    fun stopCoverageModule()


    /**
     * This API is responsible for returning all the Coverage reports for the mentioned time range captured by the Coverage module. This API will look up the Coverage report database, get the requested data
     * and align the data as per the contract defined between the client and the server for the coverage module.
     */
    fun getCoverageReport(startTime: Long, endTime: Long): Observable<List<DIAReportResponseEvent>>

    /**
     * Public API to check if Coverage module is ready to collect the data.
     * Returns true if ready
     */
    fun isCoverageModuleReady(): Boolean

    /**
     * This function deletes the processed reports from db
     */
    fun deleteReportFromDatabase()
}