package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.ConnectedWifiStatusEntity
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.BaseNr5gData
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.BaseNr5gMetricsData
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.utils.Nsa5gConstants
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.utils.Nsa5gDataCollectionService
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.utils.Nsa5gEntityConverter
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Nsa5gConnectedWifiStatusProcessor(var context: Context) : Nsa5gBaseDataProcessor(context) {

    var nsa5gDataCollectionService =
        Nsa5gDataCollectionService()
    private var nsa5gConnectedWifiStatusDisposable: Disposable? = null
    /**
     * Sets the expected size of the source received from data metrics
     * If define 0 - not from metrics
     * @return Int returns the size of the source
     */
    override fun getExpectedSourceSize(): Int {
        return Nsa5gConstants.WIFI_DATA_EXPECTED_SIZE
    }

    /**
     *  This function processes Connected Wifi Status and return disposable
     *  @param baseNr5gMetricsData: BaseNr5gMetricsData
     *  @param baseNr5gData: BaseNr5gData
     */
    override suspend fun processNr5gMetricsData(
        baseNr5gMetricsData: BaseNr5gMetricsData,
        baseNr5gData: BaseNr5gData
    ): Disposable? {
        nsa5gConnectedWifiStatusDisposable =  Observable.just(saveNsa5gConnectedWifiStatusData(baseNr5gMetricsData,
            baseNr5gData)).subscribeOn(
            Schedulers.io()).subscribe()
        return nsa5gConnectedWifiStatusDisposable
    }

    /**
     *  This function processes the list, converts it to ConnectedWifiStatusEntity and saves it in database
     *  @param baseNr5gMetricsData: total metrics data to be processed
     *  @param baseNr5gData: [BaseNr5gData]
     */
    private fun saveNsa5gConnectedWifiStatusData(baseNr5gMetricsData: BaseNr5gMetricsData, baseNr5gData: BaseNr5gData){
        val connectedWifiStatus = nsa5gDataCollectionService.processWifiData(context)
        if (connectedWifiStatus != null) {
            val connectedWifiStatusEntity =
                Nsa5gEntityConverter.convertWifiStatusEntity(connectedWifiStatus)

            connectedWifiStatusEntity.sessionId = baseNr5gData.sessionId
            connectedWifiStatusEntity.uniqueId = baseNr5gData.uniqueId

            CoroutineScope(Dispatchers.IO).launch {
                saveConnectedWifiStatusToDatabase(connectedWifiStatusEntity)
            }
        }
    }

    /**
     * saves Wifi state object to database
     * @param connectedWifiStatusEntity: [ConnectedWifiStatusEntity] the object to save
     */
    private fun saveConnectedWifiStatusToDatabase(connectedWifiStatusEntity: ConnectedWifiStatusEntity) {
        nr5gRepository.insertConnectedWifiStatusEnitity(connectedWifiStatusEntity)
    }
}



