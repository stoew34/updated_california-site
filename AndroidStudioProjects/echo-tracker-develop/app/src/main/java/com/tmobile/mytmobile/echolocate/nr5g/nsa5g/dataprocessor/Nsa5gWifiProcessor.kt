package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.dataprocessor

import android.content.Context
import android.net.wifi.WifiManager
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.Nr5gWifiStateEntity
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.BaseNr5gData
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.BaseNr5gMetricsData
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.utils.Nsa5gConstants
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.utils.Nsa5gEntityConverter
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * This Nsa5gWifiProcessor class saves the nr5g Nr5gWifiStateEntity in to the database
 */
class Nsa5gWifiProcessor(var context: Context) : Nsa5gBaseDataProcessor(context) {
    private var nsa5gWifiDisposable: Disposable? = null
    /**
     * Sets the expected size of the source received from data metrics
     * If define 0 - not from metrics
     * @return Int returns the size of the source
     */
    override fun getExpectedSourceSize(): Int {
        return Nsa5gConstants.WIFI_EXPECTED_SIZE
    }

    /**
     *  This function processes Wifi and return disposable
     *  @param baseNr5gMetricsData: BaseNr5gMetricsData
     *  @param baseNr5gData: BaseNr5gData
     */
    override suspend fun processNr5gMetricsData(
        baseNr5gMetricsData: BaseNr5gMetricsData,
        baseNr5gData: BaseNr5gData
    ): Disposable? {
        nsa5gWifiDisposable =  Observable.just(saveNsa5gWifiData(baseNr5gMetricsData,
            baseNr5gData)).subscribeOn(
            Schedulers.io()).subscribe()
        return nsa5gWifiDisposable
    }

    /**
     *  This function processes the list, converts it to Nr5gWifiStateEntity and saves it in database
     *  @param baseNr5gMetricsData: total metrics data to be processed
     *  @param baseNr5gData: [BaseNr5gData]
     */
    private fun saveNsa5gWifiData(baseNr5gMetricsData: BaseNr5gMetricsData, baseNr5gData: BaseNr5gData) {
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val nr5gWifiStateEntity = Nsa5gEntityConverter.convertNr5gWifiStateEntity(wifiManager)
        nr5gWifiStateEntity.sessionId = baseNr5gData.sessionId
        nr5gWifiStateEntity.uniqueId = baseNr5gData.uniqueId

        CoroutineScope(Dispatchers.IO).launch {
            saveNr5gWifiStateToDatabase(nr5gWifiStateEntity)
        }
    }

    /**
     * saves Wifi state object to database
     * @param nr5gWifiStateEntity: [Nr5gWifiStateEntity] the object to save
     */
    private fun saveNr5gWifiStateToDatabase(nr5gWifiStateEntity: Nr5gWifiStateEntity) {
        nr5gRepository.insertNr5gWifiStateEntity(nr5gWifiStateEntity)
    }
}