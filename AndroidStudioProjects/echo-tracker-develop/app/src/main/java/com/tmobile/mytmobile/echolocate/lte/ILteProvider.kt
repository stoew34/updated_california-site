package com.tmobile.mytmobile.echolocate.lte

import android.content.Context
import com.tmobile.mytmobile.echolocate.configuration.events.reportingevents.DIAReportResponseEvent
import io.reactivex.Observable

/**
 * Created by Hitesh K Gupta on 2019-10-14
 *
 * The interface class for LTE data collection [LteModuleProvider]. The functions defined here are the ones which will be responsible to external world.
 *
 * All the other functions and classes are hidden from the external world.
 */
interface ILteProvider {

    fun initLteModule(context: Context)
    fun stopLteModule()

    /**
     * This API is responsible for returning all the lte reports for the mentioned time range captured by the lte module. This API will look up the lte report database, get the requested data
     * and align the data as per the contract defined between the client and the server for the lte module.
     */
    fun getLteReport(startTime: Long, endTime: Long): Observable<List<DIAReportResponseEvent>>

    /**
     * Public API to check if lte module is ready to collect the data.
     * Returns true if ready
     */
    fun isLteModuleReady(): Boolean

    /**
     * This function deletes the processed reports from db
     */
    fun deleteReportFromDatabase()
}