package com.tmobile.mytmobile.echolocate.nr5g

import android.annotation.SuppressLint
import android.content.Context
import com.tmobile.myaccount.events.diagnostics.pojos.collector.event.ClientSideEvent
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gUtils
import com.tmobile.mytmobile.echolocate.reporting.IBaseReportDataRequestor
import com.tmobile.mytmobile.echolocate.reporting.manager.ReportStatusFromModules
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import io.reactivex.disposables.Disposable
import java.util.ArrayList

/**
 * Created by Divya Mittal on 5/20/21
 */
class Sa5gReportProvider private constructor(val context: Context) : IBaseReportDataRequestor {
    /** A companion object can access the private members of its companion.*/
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: Sa5gReportProvider? = null

        /***
         * creates [Sa5gReportProvider] instance
         */
        fun getInstance(context: Context): IBaseReportDataRequestor {
            return INSTANCE ?: synchronized(this) {
                val instance = Sa5gReportProvider(context)
                INSTANCE = instance
                instance
            }
        }
    }

    override fun getReports(startTime: Long, endTime: Long): List<ClientSideEvent> {
        var clientSideEventList = ArrayList<ClientSideEvent>()
        val nr5gModuleProvider = Nr5gModuleProvider.getInstance(context)
        if (nr5gModuleProvider.isNr5gModuleReady() && (nr5gModuleProvider.isNsa5gSupported || nr5gModuleProvider.isSa5gSupported))
        {
            var nr5gReportPayload: String
            var nr5gReportDisposable: Disposable? = null
            EchoLocateLog.eLogD("Diagnostic : NR5G report requested from module")

            nr5gReportDisposable = nr5gModuleProvider.get5gReport(startTime, endTime)
                .doOnError { throwable ->
                    EchoLocateLog.eLogE("Diagnostic : Error sending report for Nr5g : ${throwable.localizedMessage}")
                }
                .subscribe {
                    it.forEach {
                        if (it.DIAReportResponseParameters.requestReportStatus == ReportStatusFromModules.STATUS_COMPLETED) {
                            nr5gReportPayload = it.DIAReportResponseParameters.payload
                            val nr5gClientSideEvent =
                                Nr5gUtils.createSa5gClientSideEvent(nr5gReportPayload)
                            nr5gClientSideEvent?.let { clientSideEvent ->
                                clientSideEventList?.add(
                                    clientSideEvent
                                )
                            }
                        }
                    }
                    nr5gReportDisposable?.dispose()
                }
        }
        return clientSideEventList
    }

    override fun deleteReports() {
        Nr5gModuleProvider.getInstance(context).deleteReportFromDatabase()
    }

}