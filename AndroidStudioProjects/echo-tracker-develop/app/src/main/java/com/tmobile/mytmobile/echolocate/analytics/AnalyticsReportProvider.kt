package com.tmobile.mytmobile.echolocate.analytics

import android.annotation.SuppressLint
import android.content.Context
import com.tmobile.myaccount.events.diagnostics.pojos.collector.event.ClientSideEvent
import com.tmobile.mytmobile.echolocate.analytics.utils.AnalyticsUtils
import com.tmobile.mytmobile.echolocate.reporting.IBaseReportDataRequestor
import com.tmobile.mytmobile.echolocate.reporting.manager.ReportStatusFromModules
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import io.reactivex.disposables.Disposable
import java.util.ArrayList

/**
 * Created by Divya Mittal on 5/20/21
 */
class AnalyticsReportProvider private constructor(val context: Context) : IBaseReportDataRequestor {
    var clientSideEventList = ArrayList<ClientSideEvent>()
    /** A companion object can access the private members of its companion.*/
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: AnalyticsReportProvider? = null

        /***
         * creates [AnalyticsReportProvider] instance
         */
        fun getInstance(context: Context): IBaseReportDataRequestor {
            return INSTANCE ?: synchronized(this) {
                val instance = AnalyticsReportProvider(context)
                INSTANCE = instance
                instance
            }
        }
    }

    override fun getReports(startTime: Long, endTime: Long): List<ClientSideEvent> {
        var clientSideEventList = ArrayList<ClientSideEvent>()
        val analyticsModuleProvider = AnalyticsModuleProvider.getInstance(context)
        if (analyticsModuleProvider.isAnalyticsModuleReady()) {
            var analyticsReportPayload: String
            var analyticsReportDisposable: Disposable? = null

            analyticsReportDisposable = analyticsModuleProvider.getAnalyticsReport()
                    .doOnError { throwable ->
                        EchoLocateLog.eLogE("Diagnostic : Error sending report for Analytics : ${throwable.localizedMessage}")
                    }
                    .subscribe {
                        if (it.DIAReportResponseParameters.requestReportStatus == ReportStatusFromModules.STATUS_COMPLETED) {
                            analyticsReportPayload = it.DIAReportResponseParameters.payload
                            val analyticsClientSideEvent =
                                    AnalyticsUtils.createClientSideEvent(analyticsReportPayload)
                            analyticsClientSideEvent?.let { clientSideEvent ->
                                clientSideEventList.add(
                                        clientSideEvent
                                )
                            }
                        }
                        analyticsReportDisposable?.dispose()
                    }
        }
        return clientSideEventList
    }

    override fun deleteReports() {
        AnalyticsModuleProvider.getInstance(context).deleteReportFromDatabase()
    }

}