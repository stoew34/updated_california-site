package com.tmobile.mytmobile.echolocate.voice

import android.annotation.SuppressLint
import android.content.Context
import com.tmobile.myaccount.events.diagnostics.pojos.collector.event.ClientSideEvent
import com.tmobile.mytmobile.echolocate.coverage.CoverageModuleProvider
import com.tmobile.mytmobile.echolocate.reporting.IBaseReportDataRequestor
import com.tmobile.mytmobile.echolocate.reporting.manager.ReportStatusFromModules
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.voice.utils.VoiceUtils
import io.reactivex.disposables.Disposable
import java.util.ArrayList

/**
 * Created by Divya Mittal on 5/20/21
 */
class VoiceReportProvider private constructor(val context: Context) : IBaseReportDataRequestor {
    /** A companion object can access the private members of its companion.*/
    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: VoiceReportProvider? = null

        /***
         * creates [CoverageModuleProvider] instance
         */
        fun getInstance(context: Context): IBaseReportDataRequestor {
            return INSTANCE ?: synchronized(this) {
                val instance = VoiceReportProvider(context)
                INSTANCE = instance
                instance
            }
        }
    }

    override fun getReports(startTime: Long, endTime: Long): List<ClientSideEvent> {
        var clientSideEventList = ArrayList<ClientSideEvent>()
                val voiceModuleProvider = VoiceModuleProvider.getInstance(context)
        if (voiceModuleProvider.isVoiceModuleReady()) {
            var voiceReportPayload: String
            var voiceReportDisposable: Disposable? = null
            EchoLocateLog.eLogD("Diagnostic : Voice report requested from module")

            voiceReportDisposable = voiceModuleProvider.getVoiceReport(startTime, endTime)
                    .doOnError { throwable ->
                        EchoLocateLog.eLogE("Diagnostic : Error sending report for Voice : ${throwable.localizedMessage}")
                    }
                    .subscribe {
                        EchoLocateLog.eLogD("Diagnostic : Processing on payload for Voice")
                        it.forEach {
                            if (it.DIAReportResponseParameters.requestReportStatus == ReportStatusFromModules.STATUS_COMPLETED) {
                                voiceReportPayload = it.DIAReportResponseParameters.payload
                                val voiceClientSideEvent =
                                        VoiceUtils.createClientSideEvent(voiceReportPayload)
                                voiceClientSideEvent?.let { clientSideEvent ->
                                    clientSideEventList?.add(
                                            clientSideEvent
                                    )
                                }
                            }
                        }
                        voiceReportDisposable?.dispose()
                    }
        }
        return clientSideEventList
    }

    override fun deleteReports() {
        VoiceModuleProvider.getInstance(context).deleteReportFromDatabase()
    }
}

