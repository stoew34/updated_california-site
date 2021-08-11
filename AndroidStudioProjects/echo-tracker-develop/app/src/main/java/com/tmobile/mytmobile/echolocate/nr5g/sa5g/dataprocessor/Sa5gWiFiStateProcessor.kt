package com.tmobile.mytmobile.echolocate.nr5g.sa5g.dataprocessor

import android.content.Context
import android.net.wifi.WifiManager
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity.Sa5gWiFiStateEntity
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.model.BaseSa5gData
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.model.BaseSa5gMetricsData
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.utils.Sa5gConstants
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.utils.Sa5gEntityConverter
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * This Sa5gWifiProcessor class saves the Sa5g Sa5gWifiStateEntity in to the database
 */
class Sa5gWiFiStateProcessor(var context: Context) : Sa5gBaseDataProcessor(context) {
    private var sa5gWiFiStateDisposable: Disposable? = null
    /**
     * Sets the expected size of the source received from data metrics
     * If define 0 - not from metrics
     * @return Int returns the size of the source
     */
    override fun getExpectedSourceSize(): Int {
        return Sa5gConstants.SA5G_WIFI_STATE_EXPECTED_SIZE
    }

    /**
     * This function processes WiFiState and return disposable
     *  @param baseSa5gMetricsData: BaseSa5gMetricsData
     *  @param baseSa5gData: BaseSa5gData
     */
    override suspend fun processSa5gMetricsData(
        baseSa5gMetricsData: BaseSa5gMetricsData,
        baseSa5gData: BaseSa5gData
    ): Disposable? {
        sa5gWiFiStateDisposable =  Observable.just(saveWiFiStateData(baseSa5gMetricsData,
            baseSa5gData)).subscribeOn(
            Schedulers.io()).subscribe()
        return sa5gWiFiStateDisposable
    }

    /**
     *  This function processes the list, converts it to Sa5gWiFiStateEntity and saves it in database
     *  @param baseSa5gMetricsData: total metrics data to be processed
     *  @param baseSa5gData: [BaseSa5gData]
     */
    private fun saveWiFiStateData(baseSa5gMetricsData: BaseSa5gMetricsData, baseSa5gData: BaseSa5gData){
        val wifiManager =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val sa5gWiFiStateEntity = Sa5gEntityConverter.convertSa5gWiFiStateEntity(wifiManager)

        sa5gWiFiStateEntity.sessionId = baseSa5gData.sessionId
        sa5gWiFiStateEntity.uniqueId = baseSa5gData.uniqueId

        CoroutineScope(Dispatchers.IO).launch {
            saveSa5gWiFiStateToDatabase(sa5gWiFiStateEntity)
        }
    }

    /**
     * saves Wifi state object to database
     * @param sa5gWiFiStateEntity: [Sa5gWiFiStateEntity] the object to save
     */
    private fun saveSa5gWiFiStateToDatabase(sa5gWiFiStateEntity: Sa5gWiFiStateEntity) {
        sa5gRepository.insertSa5gWiFiStateEntity(sa5gWiFiStateEntity)
    }
}