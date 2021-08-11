package com.tmobile.mytmobile.echolocate.nr5g

import android.content.Context
import com.tmobile.mytmobile.echolocate.configuration.events.reportingevents.DIAReportResponseEvent
import io.reactivex.Observable

/**
 * Created by Hitesh K Gupta on 2019-10-14
 *
 * The interface class for Nr5g data collection [Nr5gModuleProvider]. The functions defined here are the ones which will be responsible to external world.
 *
 * All the other functions and classes are hidden from the external world.
 */
interface INr5gProvider {

    /**
     * This API is responsible for initialize Nr5g module and preparing the module
     * to start accepting the incoming data, process and store it.
     * If this API is not being called, the Nr5g module will not start the data collection.
     * @param context : Context the context of the Nr5g module
     */

    fun initNr5gModule(context: Context)

    /**
     * This API is responsible to stop nr5g module
     */
    fun stopNr5gModule()

    /**
     * This API is responsible for returning all the nr5g reports for the mentioned time range captured by the nr5g module. This API will look up the nr5g report database, get the requested data
     * and align the data as per the contract defined between the client and the server for the nr5g module.
     * @param startTime : start time to get report
     * @param endTime : end time to get report
     */
    fun get5gReport(startTime: Long, endTime: Long): Observable<List<DIAReportResponseEvent>>

    /**
     * Public API to check if nr5g module is ready to collect the data.
     * Returns true if ready
     */
    fun isNr5gModuleReady(): Boolean

    /**
     * Function called by scheduler for start Report sending process
     */
    fun send5gReport(androidWorkId: String?)
}