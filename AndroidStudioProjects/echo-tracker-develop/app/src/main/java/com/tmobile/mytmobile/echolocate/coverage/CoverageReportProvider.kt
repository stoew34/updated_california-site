package com.tmobile.mytmobile.echolocate.coverage

import android.annotation.SuppressLint
import android.content.Context
import com.tmobile.myaccount.events.diagnostics.pojos.collector.event.ClientSideEvent
import com.tmobile.mytmobile.echolocate.coverage.utils.CoverageUtils
import com.tmobile.mytmobile.echolocate.reporting.IBaseReportDataRequestor
import com.tmobile.mytmobile.echolocate.reporting.manager.ReportStatusFromModules
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import io.reactivex.disposables.Disposable
import java.util.*

/**
 * Created by Divya Mittal on 5/20/21
 */
class CoverageReportProvider private constructor(val context: Context) : IBaseReportDataRequestor {
    /** A companion object can access the private members of its companion.*/
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: CoverageReportProvider? = null

        /***
         * creates [CoverageModuleProvider] instance
         */
        fun getInstance(context: Context): IBaseReportDataRequestor {
            return INSTANCE ?: synchronized(this) {
                val instance = CoverageReportProvider(context)
                INSTANCE = instance
                instance
            }
        }
    }

    override fun getReports(startTime: Long, endTime: Long): List<ClientSideEvent> {
        var clientSideEventList = ArrayList<ClientSideEvent>()
        val coverageModuleProvider = CoverageModuleProvider.getInstance(context)
        if (coverageModuleProvider.isCoverageModuleReady()) {
            var coverageReportPayload: String
            var coverageReportDisposable: Disposable? = null

            coverageReportDisposable = coverageModuleProvider.getCoverageReport(startTime, endTime)
                .doOnError { throwable ->
                    EchoLocateLog.eLogE("Diagnostic : Error sending report for Coverage : ${throwable.localizedMessage}")
                }
                .subscribe {
                    it.forEach {
                        if (it.DIAReportResponseParameters.requestReportStatus == ReportStatusFromModules.STATUS_COMPLETED) {
                            coverageReportPayload = it.DIAReportResponseParameters.payload
                            val coverageClientSideEvent =
                                CoverageUtils.createClientSideEvent(
                                    coverageReportPayload
                                )
                            coverageClientSideEvent?.let { clientSideEvent ->
                                clientSideEventList?.add(
                                    clientSideEvent
                                )
                            }
                        }
                    }
                    coverageReportDisposable?.dispose()
                }
        }
        return clientSideEventList
    }

    override fun deleteReports() {
        CoverageModuleProvider.getInstance(context).deleteReportFromDatabase()
    }

}