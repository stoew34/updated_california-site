package com.tmobile.mytmobile.echolocate.voice

import android.content.Context
import com.tmobile.mytmobile.echolocate.configuration.events.reportingevents.DIAReportResponseEvent
import io.reactivex.Observable

/**
 * This is an abstract class for the [VoiceModuleProvider]
 */
abstract class VoiceModuleProviderAbstract {

    /**
     * This API is responsible for returning all the voice reports for the mentioned
     * time range captured by the voice module. This API will look up the voice report database,
     * get the requested data and align the data as per the contract defined between the client
     * and the server for the voice module.
     */
    abstract fun getVoiceReport(startTime: Long, endTime: Long): Observable<List<DIAReportResponseEvent>>

    /**
     * Public API to initReportingModule voice module.
     * This API is responsible for preparing the module to start
     * accepting the incoming data, process and store it.
     *
     * If this API is not called, the voice module will not start the data collection.
     */
    abstract fun initVoiceModule(context: Context)

    /**
     * Public API to stop voice module. If this function is called, the voice module will stop working.
     *
     * Call initVoiceModule [initVoiceModule] to start voice module again.
     */
    abstract fun stopVoiceModule()

    /**
     * Public API to check if voice module is ready to collect the data.
     * Returns true if ready
     */
    abstract fun isVoiceModuleReady(): Boolean
}