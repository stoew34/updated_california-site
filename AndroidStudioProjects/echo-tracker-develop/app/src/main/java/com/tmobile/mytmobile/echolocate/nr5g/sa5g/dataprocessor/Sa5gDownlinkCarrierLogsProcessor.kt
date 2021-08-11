package com.tmobile.mytmobile.echolocate.nr5g.sa5g.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity.Sa5gDownlinkCarrierLogsEntity
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.model.BaseSa5gData
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.model.BaseSa5gMetricsData
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.utils.Sa5gConstants
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.utils.Sa5gEntityConverter.Companion.convertDownlinkCarrierLogsToEntity
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
 * Collects Sa5gDownlinkCarrierLogs data and saves the data in the database when focus gain or focus loss event is triggered
 */
class Sa5gDownlinkCarrierLogsProcessor(val context: Context) : Sa5gBaseDataProcessor(context) {

    private var sa5gDownLinkCarrierLogsDisposable: Disposable? = null
    /**
     * Sets the expected size of the source received from data metrics
     * @return Int returns the size of the source
     */
    override fun getExpectedSourceSize(): Int {
        return Sa5gConstants.SA5G_DL_CARRIER_LOGS_SIZE
    }

    /**
     * This function processes down link carrier logs and return disposable
     *  @param baseSa5gMetricsData: BaseSa5gMetricsData
     *  @param baseSa5gData: BaseSa5gData
     */
    override suspend fun processSa5gMetricsData(
        baseSa5gMetricsData: BaseSa5gMetricsData,
        baseSa5gData: BaseSa5gData
    ): Disposable? {
        sa5gDownLinkCarrierLogsDisposable =  Observable.just(saveDownLinkCarrierLogsData(baseSa5gMetricsData,
            baseSa5gData)).subscribeOn(
            Schedulers.io()).subscribe()
        return sa5gDownLinkCarrierLogsDisposable
    }

    /**
     *  This function fetches Sa5gDownLinkCarrierLogsEntity from source and saves it in database
     *  @param baseSa5gMetricsData: total metrics data to be processed
     *  @param baseSa5gData: [BaseSa5gData]
     */
    private fun saveDownLinkCarrierLogsData(baseSa5gMetricsData: BaseSa5gMetricsData, baseSa5gData: BaseSa5gData){
        val downLinkCarrierLogsEntityList = generateDownlinkCarrierLogsEntityList(
            baseSa5gMetricsData.source as List<Any?>,
            baseSa5gData
        )
        CoroutineScope(Dispatchers.IO).launch {
            saveSa5gDownlinkCarrierLogsListToDatabase(downLinkCarrierLogsEntityList)
        }
    }


    /**
     * DlCarrierLog object contains the data for one carrier component. In Carrier Aggregation mode,
     * multiple DlCarrierLog objects are expected for the same number of the carrier components
     * active at the time of API call.
     *
     * For instance, if the device is in 2CA mode where one carrier is n71 and the other carrier
     * is n41, getDlCarrierLog() shall return 2 DlCarrierLog objects in the list. One DlCarrierLog
     * instance shall contain the data for the band n71 carrier while the other DlCarrierLog object
     * shall contain the data for the n41 carrier. Hence, listDlCarrierLog[0].bandNumber is n71 and
     * listDlCarrierLog[1].bandNumber is n41.
     */
    private fun generateDownlinkCarrierLogsEntityList(
        source: List<Any?>,
        baseSa5gData: BaseSa5gData
    ): List<Sa5gDownlinkCarrierLogsEntity> {

        val downlinkCarrierLogsEntityList = mutableListOf<Sa5gDownlinkCarrierLogsEntity>()

        for (downlinkCarrierLogs in source) {
            EchoLocateLog.eLogD(
                "\tDiagnostic :Sa5g source size for DownlinkCarrierLogs:\n" +
                        "\t : ${source.size}\n" +
                        "\t at TimeStamp: ${EchoLocateDateUtils.convertToShemaDateFormat(
                            System.currentTimeMillis().toString()
                        )}\n"
            )
            val sa5gDownlinkCarrierLogsToEntity =
                convertDownlinkCarrierLogsToEntity(downlinkCarrierLogs)

            if (sa5gDownlinkCarrierLogsToEntity != null) {
                sa5gDownlinkCarrierLogsToEntity.sessionId = baseSa5gData.sessionId
                sa5gDownlinkCarrierLogsToEntity.uniqueId = UUID.randomUUID().toString()
                downlinkCarrierLogsEntityList.add(sa5gDownlinkCarrierLogsToEntity)
            }
        }
        return downlinkCarrierLogsEntityList
    }

    /**
     * saves the sa5gDownlinkCarrierLogsEntityList object to database
     * @param sa5gDownlinkCarrierLogsEntityList: List<Sa5gDownlinkCarrierLogsEntity> to save
     */
    private fun saveSa5gDownlinkCarrierLogsListToDatabase(sa5gDownlinkCarrierLogsEntityList: List<Sa5gDownlinkCarrierLogsEntity>) {
        sa5gRepository.insertAllSa5gDownlinkCarrierLogsEntity(sa5gDownlinkCarrierLogsEntityList)
    }
}