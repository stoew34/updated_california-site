package com.tmobile.mytmobile.echolocate.reporting.reportsender

import com.tmobile.mytmobile.echolocate.network.result.NetworkResponseDetails

/**
 * Interface class for [ReportSender] to listen to the network response. After the response is received, the report sender can
 *
 * take appropriate action such as update the status of the report, delete the files from the storage.
 */
interface ReportResponseListener {

    /**
     * This function is invoked when a response is received from the network module to let the [ReportSender] know about the response received from the server.
     */
    fun onNetworkResponse(networkResponse: NetworkResponseDetails)
}