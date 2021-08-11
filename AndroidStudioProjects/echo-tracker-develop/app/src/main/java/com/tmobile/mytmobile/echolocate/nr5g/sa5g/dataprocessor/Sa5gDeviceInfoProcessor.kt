package com.tmobile.mytmobile.echolocate.nr5g.sa5g.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gDeviceInfoDataCollector
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity.Sa5gDeviceInfoEntity
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

class Sa5gDeviceInfoProcessor(var context: Context) : Sa5gBaseDataProcessor(context) {

    var deviceInfoDataCollector = Nr5gDeviceInfoDataCollector()
    private var sa5gDeviceInfoDisposable: Disposable? = null

    /**
     * Sets the expected size of the source received from data metrics
     * If define 0 - not from metrics
     * @return Int returns the size of the source
     */
    override fun getExpectedSourceSize(): Int {
        return Sa5gConstants.SA5G_DEVICE_INFO_EXPECTED_SIZE
    }

    /**
     * This function processes device info and return disposable
     *  @param baseSa5gMetricsData: BaseSa5gMetricsData
     *  @param baseSa5gData: BaseSa5gData
     */
    override suspend fun processSa5gMetricsData(
        baseSa5gMetricsData: BaseSa5gMetricsData,
        baseSa5gData: BaseSa5gData
    ): Disposable? {
        sa5gDeviceInfoDisposable =  Observable.just(saveDeviceInfoData(baseSa5gMetricsData,
            baseSa5gData)).subscribeOn(
            Schedulers.io()).subscribe()
        return sa5gDeviceInfoDisposable
    }

    /**
     *  This function processes the list, converts it to Sa5gDeviceInfoEntity and saves it in database
     *  @param baseSa5gMetricsData: total metrics data to be processed
     *  @param baseSa5gData: [BaseSa5gData]
     */
    private fun saveDeviceInfoData(baseSa5gMetricsData: BaseSa5gMetricsData, baseSa5gData: BaseSa5gData) {
        val deviceInfo = deviceInfoDataCollector.getDeviceInformation(context)

        val sa5gDeviceInfoEntity = Sa5gEntityConverter.convertSa5gDeviceInfoEntity(deviceInfo)

        sa5gDeviceInfoEntity.sessionId = baseSa5gData.sessionId
        sa5gDeviceInfoEntity.uniqueId = baseSa5gData.uniqueId

        CoroutineScope(Dispatchers.IO).launch {
            saveSa5gDeviceInfoToDatabase(sa5gDeviceInfoEntity)
        }
    }

    /**
     * saves DeviceInfo object to database
     * @param sa5gDeviceInfoEntity: [Sa5gDeviceInfoEntity] the object to save
     */
    private fun saveSa5gDeviceInfoToDatabase(sa5gDeviceInfoEntity: Sa5gDeviceInfoEntity) {
        sa5gRepository.insertSa5gDeviceInfoEntity(sa5gDeviceInfoEntity)
    }
}