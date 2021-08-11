package com.tmobile.mytmobile.echolocate.nr5g.sa5g.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity.Sa5gConnectedWifiStatusEntity
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.model.BaseSa5gData
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.model.BaseSa5gMetricsData
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.utils.Sa5gConstants
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.utils.Sa5gDataCollectionService
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.utils.Sa5gEntityConverter
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Sa5gConnectedWifiStatusProcessor(var context: Context) : Sa5gBaseDataProcessor(context) {

    var sa5gNetworkDataCollector = Sa5gDataCollectionService()
    private var sa5gConnectedWifiStatusDisposable: Disposable? = null
    /**
     * Sets the expected size of the source received from data metrics
     * If define 0 - not from metrics
     * @return Int returns the size of the source
     */
    override fun getExpectedSourceSize(): Int {
        return Sa5gConstants.SA5G_CONNECTED_WIFI_STATUS_EXPECTED_SIZE
    }

    /**
     * This function processes connected wifi status and return disposable
     *  @param baseSa5gMetricsData: BaseSa5gMetricsData
     *  @param baseSa5gData: BaseSa5gData
     */
    override suspend fun processSa5gMetricsData(
        baseSa5gMetricsData: BaseSa5gMetricsData,
        baseSa5gData: BaseSa5gData
    ): Disposable? {
        sa5gConnectedWifiStatusDisposable =  Observable.just(saveConnectedWifiStatusData(baseSa5gMetricsData,
            baseSa5gData)).subscribeOn(
            Schedulers.io()).subscribe()
        return sa5gConnectedWifiStatusDisposable
    }

    /**
     *  This function processes the list, converts it to ConnectedWifiStatusEntity and saves it in database
     *  @param baseSa5gMetricsData: total metrics data to be processed
     *  @param baseSa5gData: [BaseSa5gData]
     */
    private fun saveConnectedWifiStatusData(baseSa5gMetricsData: BaseSa5gMetricsData, baseSa5gData: BaseSa5gData) {
        val sa5gConnectedWifiStatus = sa5gNetworkDataCollector.getConnectedWifiStatus(context)
        if (sa5gConnectedWifiStatus != null) {
            val sa5gConnectedWifiStatusEntity =
                Sa5gEntityConverter.convertSa5gConnectedWifiStatusEntity(sa5gConnectedWifiStatus)

            sa5gConnectedWifiStatusEntity.sessionId = baseSa5gData.sessionId
            sa5gConnectedWifiStatusEntity.uniqueId = baseSa5gData.uniqueId

            CoroutineScope(Dispatchers.IO).launch {
                saveSa5gConnectedWifiStatusToDatabase(sa5gConnectedWifiStatusEntity)
            }
        }
    }

    /**
     * saves Wifi state object to database
     * @param sa5gConnectedWifiStatusEntity: [Sa5gConnectedWifiStatusEntity] the object to save
     */
    private fun saveSa5gConnectedWifiStatusToDatabase(sa5gConnectedWifiStatusEntity: Sa5gConnectedWifiStatusEntity) {
        sa5gRepository.insertSa5gConnectedWifiStatusEntity(sa5gConnectedWifiStatusEntity)
    }
}