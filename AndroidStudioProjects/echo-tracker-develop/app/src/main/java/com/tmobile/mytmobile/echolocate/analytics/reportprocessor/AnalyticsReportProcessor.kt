package com.tmobile.mytmobile.echolocate.analytics.reportprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.BuildConfig
import com.tmobile.mytmobile.echolocate.analytics.database.EchoLocateAnalyticsDatabase
import com.tmobile.mytmobile.echolocate.analytics.database.dao.AnalyticsDao
import com.tmobile.mytmobile.echolocate.analytics.database.entity.AnalyticsEventEntity
import com.tmobile.mytmobile.echolocate.analytics.database.repository.AnalyticsRepository
import com.tmobile.mytmobile.echolocate.analytics.model.AnalyticsEventModel
import com.tmobile.mytmobile.echolocate.analytics.model.AnalyticsReport
import com.tmobile.mytmobile.echolocate.analytics.reportprocessor.AnalyticsDataStatus.Companion.STATUS_PROCESSED
import com.tmobile.mytmobile.echolocate.analytics.reportprocessor.AnalyticsDataStatus.Companion.STATUS_RAW
import com.tmobile.mytmobile.echolocate.analytics.utils.AnalyticsSharedPreference
import com.tmobile.mytmobile.echolocate.analytics.utils.AnalyticsDeviceInfoDataCollector
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticActions
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELModulesEnum
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import com.tmobile.mytmobile.echolocate.utils.SingletonHolder
import com.tmobile.mytmobile.echolocate.voice.reportprocessor.VoiceDataStatus


/**
 * Generated reports from raw data collected from other modules into Analytics reports.
 * Generate JSON file from DataBase
 */
class AnalyticsReportProcessor private constructor(val context: Context) {

    // Only single instance of report processor is needed to avoid report/data duplication
    companion object : SingletonHolder<AnalyticsReportProcessor, Context>(::AnalyticsReportProcessor)

    /***
     * gets the Analytics DAO instance defined as a abstract class in [EchoLocateAnalyticsDatabase]
     */
    private val analyticsDao: AnalyticsDao =
        EchoLocateAnalyticsDatabase.getEchoLocateAnalyticsDatabase(context).analyticsDao()

    private var analyticsRepository = AnalyticsRepository(context)
    var deviceInfoDataCollector = AnalyticsDeviceInfoDataCollector()
    var numEventsBundled = 100

    /**
     * Generated [AnalyticsReport] in accordance which Schema JSON, data get status PROCESSED
     * Params of each Analytics Report in accordance with [AnalyticsReport]
     */
    fun getAnalyticsReport(): AnalyticsReport {
        val analyticsEventEntityList = analyticsDao.getAllAnalyticsEventEntity(numEventsBundled)
        analyticsEventEntityList.let {
            it.forEach {
                it.status = AnalyticsDataStatus.STATUS_REPORTING
            }
        }
        markRawDataAsProcessed(analyticsEventEntityList)
        val analyticsReport = processAnalyticsReport(analyticsEventEntityList)

        /** Do not delete data in debug so that it can be tested and data can be viewed.*/
//        if (BuildConfig.DEBUG) {
//            analyticsEventEntityList.forEach { f -> f.status = STATUS_PROCESSED }
//            markRawDataAsProcessed(analyticsEventEntityList)
//           //deleteProcessedData() // use for testing of delete logic
//        } else {
//            deleteRawData(analyticsEventEntityList)
//        }
        return analyticsReport
    }

    /**
     * Generated [AnalyticsReport] in accordance which Schema JSON, data get status PROCESSED
     * Params of each Analytics Report in accordance with [AnalyticsReport]
     */
    private fun processAnalyticsReport(analyticsEventEntityList: List<AnalyticsEventEntity>): AnalyticsReport {
        val deviceInfo = deviceInfoDataCollector.getDeviceInformation(context)
        return AnalyticsReport(
            analytics = getAnalyticsEventList(analyticsEventEntityList),
            deviceInfo = deviceInfo
        )
    }

    /**
     * Generated an object of [AnalyticsEventModel]
     * Based on data class [AnalyticsEventModel]
     * Entity class AnalyticsEventEntity
     * Data from other modules
     * @return list of [AnalyticsEventModel]
     */
    private fun getAnalyticsEventList(analyticsEventEntityList: List<AnalyticsEventEntity>): List<AnalyticsEventModel?>? {
        val elAnalyticsEventList = ArrayList<AnalyticsEventModel>()

        return if (analyticsEventEntityList == null) {
            null
        } else {
            var osCrashCount = 0
            for (analyticsEventEntity in analyticsEventEntityList) {
                if (analyticsEventEntity.action != ELAnalyticActions.EL_NUMBER_OF_TIMES_APP_KILLED_BY_OS.key) {
                    val elAnalyticsEvent = AnalyticsEventModel(
                        ELModulesEnum.valueOf(analyticsEventEntity.moduleName),
                        ELAnalyticActions.valueOf(analyticsEventEntity.action),
                        analyticsEventEntity.payload,
                        analyticsEventEntity.timestamp
                    )
                    elAnalyticsEventList.add(elAnalyticsEvent)
                }
                else {
                    osCrashCount++
                }
            }
            if (osCrashCount > 0) {
                val elAnalyticsEvent = AnalyticsEventModel(
                    ELModulesEnum.ANALYTICS,
                    ELAnalyticActions.EL_NUMBER_OF_TIMES_APP_KILLED_BY_OS,
                    osCrashCount.toString(),
                    if (AnalyticsSharedPreference.timestampOfAppStartAfterLastKill != "")
                        AnalyticsSharedPreference.timestampOfAppStartAfterLastKill
                    else
                        EchoLocateDateUtils.getFormattedTime(System.currentTimeMillis())
                )
                elAnalyticsEventList.add(elAnalyticsEvent)
            }
            return elAnalyticsEventList
        }
    }

    /**
     * deletes the processed data for production build
     */
    fun deleteProcessedData(status: String) {
        analyticsRepository.deleteData(status)
    }

    /**
     * This fun updates record in database with new data by using updateAllAnalyticsEventEntityStatus
     * using Variable number of arguments (varargs) - getting latest value of data
     */
    private fun markRawDataAsProcessed(analyticsEventEntityList: List<AnalyticsEventEntity>) {
        analyticsDao.updateAllAnalyticsEventEntityStatus(*analyticsEventEntityList.toTypedArray())
    }
}