package com.tmobile.mytmobile.echolocate.nr5g.sa5g.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity.Sa5gUplinkCarrierLogsEntity
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.model.BaseSa5gData
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.model.BaseSa5gMetricsData
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.utils.Sa5gConstants
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.utils.Sa5gEntityConverter.Companion.convertSa5gUplinkCarrierLogsEntity
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 * Collects Sa5gUplinkCarrierLogs data and saves the data in the database
 * when focus gain or focus loss event is triggered
 */
class Sa5gUplinkCarrierLogsProcessor(val context: Context) : Sa5gBaseDataProcessor(context) {
    private var sa5gUpLinkCarrierLogsDisposable: Disposable? = null
    /**
     * Sets the expected size of the source received from data metrics
     * @return Int returns the size of the source
     */
    override fun getExpectedSourceSize(): Int {
        return Sa5gConstants.SA5G_UL_CARRIER_LOGS_SIZE
    }

    /**
     * This function processes UpLinkCarrierLogs and return disposable
     *  @param baseSa5gMetricsData: BaseSa5gMetricsData
     *  @param baseSa5gData: BaseSa5gData
     */
    override suspend fun processSa5gMetricsData(
        baseSa5gMetricsData: BaseSa5gMetricsData,
        baseSa5gData: BaseSa5gData
    ): Disposable? {
        sa5gUpLinkCarrierLogsDisposable =  Observable.just(saveUpLinkCarrierLogsData(baseSa5gMetricsData,
            baseSa5gData)).subscribeOn(
            Schedulers.io()).subscribe()
        return sa5gUpLinkCarrierLogsDisposable
    }

    /**
     *  This function fetches Sa5gUplinkCarrierLogsEntity from source and saves it in database
     *  @param baseSa5gMetricsData: total metrics data to be processed
     *  @param baseSa5gData: [BaseSa5gData]
     */
    private fun saveUpLinkCarrierLogsData(baseSa5gMetricsData: BaseSa5gMetricsData, baseSa5gData: BaseSa5gData) {
        val sa5gUplinkCarrierLogsEntityList = generateUplinkCarrierLogsEntityList(
            baseSa5gMetricsData.source as List<Any?>,
            baseSa5gData
        )

        CoroutineScope(Dispatchers.IO).launch {
            saveSa5gUplinkCarrierLogsToDatabase(sa5gUplinkCarrierLogsEntityList)
        }
    }

    private fun generateUplinkCarrierLogsEntityList(
        source: List<Any?>,
        baseSa5gData: BaseSa5gData
    ): List<Sa5gUplinkCarrierLogsEntity> {

        val uplinkCarrierLogsEntityList = mutableListOf<Sa5gUplinkCarrierLogsEntity>()

        for (uplinkCarrierLogs in source) {
            EchoLocateLog.eLogD(
                "\tDiagnostic :Sa5g source size for UplinkCarrierLogs:\n" +
                        "\t : ${source.size}\n" +
                        "\t at TimeStamp: ${EchoLocateDateUtils.convertToShemaDateFormat(
                            System.currentTimeMillis().toString()
                        )}\n"
            )
            val sa5gUplinkCarrierLogsEntity =
                convertSa5gUplinkCarrierLogsEntity(uplinkCarrierLogs)

            if (sa5gUplinkCarrierLogsEntity != null) {
                sa5gUplinkCarrierLogsEntity.sessionId = baseSa5gData.sessionId
                sa5gUplinkCarrierLogsEntity.uniqueId = UUID.randomUUID().toString()
                uplinkCarrierLogsEntityList.add(sa5gUplinkCarrierLogsEntity)
            }
        }
        return uplinkCarrierLogsEntityList
    }

    /**
     * Inserts the data in database
     * @param sa5gUplinkCarrierLogsEntityList:
     *  List<Sa5gUplinkCarrierLogsEntity> to save
     */
    private fun saveSa5gUplinkCarrierLogsToDatabase(sa5gUplinkCarrierLogsEntityList: List<Sa5gUplinkCarrierLogsEntity>) {
        sa5gRepository.insertAllSa5gUplinkCarrierLogsEntity(sa5gUplinkCarrierLogsEntityList)
    }
}