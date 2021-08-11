package com.tmobile.mytmobile.echolocate.lte

import android.annotation.SuppressLint
import android.content.Context
import com.tmobile.myaccount.events.diagnostics.pojos.collector.event.ClientSideEvent
import com.tmobile.mytmobile.echolocate.lte.utils.LteUtils
import com.tmobile.mytmobile.echolocate.reporting.IBaseReportDataRequestor
import com.tmobile.mytmobile.echolocate.reporting.manager.ReportStatusFromModules
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import io.reactivex.disposables.Disposable
import java.util.ArrayList

/**
 * Created by Divya Mittal on 5/20/21
 */
class LteReportProvider private constructor(val context: Context) : IBaseReportDataRequestor {
    var clientSideEventList = ArrayList<ClientSideEvent>()
    /** A companion object can access the private members of its companion.*/
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: LteReportProvider? = null

        /***
         * creates [LteReportProvider] instance
         */
        fun getInstance(context: Context): IBaseReportDataRequestor {
            return INSTANCE ?: synchronized(this) {
                val instance = LteReportProvider(context)
                INSTANCE = instance
                instance
            }
        }
    }

    override fun getReports(startTime: Long, endTime: Long): List<ClientSideEvent> {
        var clientSideEventList = ArrayList<ClientSideEvent>()
                val lteModuleProvider = LteModuleProvider.getInstance(context)
        if (lteModuleProvider.isLteModuleReady()) {
            var lteReportPayload: String
            var lteReportDisposable: Disposable? = null
            EchoLocateLog.eLogD("Diagnostic : LTE report requested from module")

            lteReportDisposable = lteModuleProvider.getLteReport(startTime, endTime)
                    .doOnError { throwable ->
                        EchoLocateLog.eLogE("Diagnostic : Error sending report for LTE : ${throwable.localizedMessage}")
                    }.subscribe {
                        it.forEach {
                            if (it.DIAReportResponseParameters.requestReportStatus == ReportStatusFromModules.STATUS_COMPLETED) {
                                lteReportPayload = it.DIAReportResponseParameters.payload
                                val lteClientSideEvent =
                                        LteUtils.createClientSideEvent(lteReportPayload)
                                lteClientSideEvent?.let { clientSideEvent ->
                                    clientSideEventList?.add(
                                            clientSideEvent
                                    )
                                }
                            }
                        }
                        lteReportDisposable?.dispose()
                    }
        }
        return clientSideEventList
    }

    override fun deleteReports() {
        LteModuleProvider.getInstance(context).deleteReportFromDatabase()
    }

}