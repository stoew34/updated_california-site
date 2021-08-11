package com.tmobile.mytmobile.echolocate.reporting.reportsender

import android.annotation.SuppressLint
import android.content.Context
import com.tmobile.mytmobile.echolocate.network.events.NetworkResponseEvent
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.SubscribeTicket
import com.tmobile.mytmobile.echolocate.reporting.NetworkProvider
import io.reactivex.disposables.Disposable

class ReportNetworkManager() {

    lateinit var context: Context
    private lateinit var networkResponseListener: ReportResponseListener

    private val bus = RxBus.instance
    private var networkEventDisposable: Disposable? = null

    constructor(context: Context, networkResponseListener: ReportResponseListener) : this() {
        this.context = context
        this.networkResponseListener = networkResponseListener
        subscribeRxBusForAnalyticsEvent()
    }

    /**
     * running network operation with input data
     *
     * @param payload
     * @param reportId
     */
    @SuppressLint("CheckResult")
    fun postReportToServer(payloadFile: String, reportId: String, datToken: String) {
        val networkProvider = NetworkProvider.getInstance(context) as NetworkProvider
        networkProvider.performNetworkOperationsUsingRxBus(
            ReportNetworkTask.getReportDiaRequest(context, payloadFile, reportId),
            ReportNetworkTask.getReportRequestHeader(),
            ReportNetworkTask.getReportRetryPrefs(),
            datToken,
            context
        )
    }

    /**
     * This fun listens several modules and passing new data once it available
     * Function starts when module starts from [startAnalyticsDataCollection]
     */
    private fun subscribeRxBusForAnalyticsEvent() {

        val subscribeTicket = SubscribeTicket(RxBus.SubjectType.PUBLISH_SUBJECT)

        networkEventDisposable?.dispose()
        networkEventDisposable = bus.register<NetworkResponseEvent>(subscribeTicket).subscribe {
            networkResponseListener.onNetworkResponse(it.networkResponseDetails)
        }
    }
}