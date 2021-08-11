package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gDeviceInfoDataCollector
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.Nr5gDeviceInfoEntity
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.BaseNr5gData
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.BaseNr5gMetricsData
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.utils.Nsa5gConstants
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.utils.Nsa5gEntityConverter
import com.tmobile.mytmobile.echolocate.voice.repository.database.entity.DeviceInfoEntity
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Nsa5gDeviceInfoProcessor(var context: Context) : Nsa5gBaseDataProcessor(context) {

    var deviceInfoDataCollector = Nr5gDeviceInfoDataCollector()
    private var nsa5gDeviceInfoDisposable: Disposable? = null

    /**
     * Sets the expected size of the source received from data metrics
     * If define 0 - not from metrics
     * @return Int returns the size of the source
     */
    override fun getExpectedSourceSize(): Int {
        return Nsa5gConstants.DEVICE_INFO_EXPECTED_SIZE
    }

    /**
     *  This function processes device info and return disposable
     *  @param baseNr5gMetricsData: BaseNr5gMetricsData
     *  @param baseNr5gData: BaseNr5gData
     */
    override suspend fun processNr5gMetricsData(
        baseNr5gMetricsData: BaseNr5gMetricsData,
        baseNr5gData: BaseNr5gData
    ): Disposable? {
        nsa5gDeviceInfoDisposable =  Observable.just(saveNsa5gDeviceInfoData(baseNr5gMetricsData,
            baseNr5gData)).subscribeOn(
            Schedulers.io()).subscribe()
        return nsa5gDeviceInfoDisposable
    }

    /**
     *  This function processes the list, converts it to DeviceInfoEntity and saves it in database
     *  @param baseNr5gMetricsData: total metrics data to be processed
     *  @param baseNr5gData: [BaseNr5gData]
     */
    private fun saveNsa5gDeviceInfoData(baseNr5gMetricsData: BaseNr5gMetricsData, baseNr5gData: BaseNr5gData){
        val deviceInfo = deviceInfoDataCollector.getDeviceInformation(context)

        val deviceInfoEntity = Nsa5gEntityConverter.convertNr5gDeviceInfoEntity(deviceInfo)

        deviceInfoEntity.sessionId = baseNr5gData.sessionId
        deviceInfoEntity.uniqueId = baseNr5gData.uniqueId

        CoroutineScope(Dispatchers.IO).launch {
            saveNr5gDeviceInfoToDatabase(deviceInfoEntity)
        }
    }

    /**
     * Stores device info in to a database
     * @param deviceInfoEntity: [DeviceInfoEntity] the object to save
     */
    private fun saveNr5gDeviceInfoToDatabase(deviceInfoEntity: Nr5gDeviceInfoEntity) {
        nr5gRepository.insertNr5gDeviceInfoEntity(deviceInfoEntity)
    }
}