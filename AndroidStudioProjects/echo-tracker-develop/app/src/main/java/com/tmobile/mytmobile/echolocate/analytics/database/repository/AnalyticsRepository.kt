package com.tmobile.mytmobile.echolocate.analytics.database.repository

import android.content.Context
import com.tmobile.mytmobile.echolocate.analytics.database.EchoLocateAnalyticsDatabase
import com.tmobile.mytmobile.echolocate.analytics.database.dao.AnalyticsDao
import com.tmobile.mytmobile.echolocate.analytics.database.entity.AnalyticsEventEntity

/**
 * The [AnalyticsRepository] repository class will be responsible for interacting with the Room database
 * and will need to provide methods that use the DAO to insert, delete and query product records.
 * @param context :Context the context passed from activity
 */
class AnalyticsRepository(context: Context) {

    /***
     * gets the Analytics DAO instance defined as a abstract class in [EchoLocateAnalyticsDatabase]
     */
    private val analyticsDao: AnalyticsDao =
        EchoLocateAnalyticsDatabase.getEchoLocateAnalyticsDatabase(context).analyticsDao()

    /**
     * insertAnalyticsEventEntity  function is defined in DAO to
     * insert [AnalyticsEventEntity] parameters into the database
     * @param analyticsEventEntity: DeviceInfoAnalyticsEntity object
     */
    fun insertAnalyticsEventEntity(analyticsEventEntity: AnalyticsEventEntity) {
        analyticsDao.insertAnalyticsEventEntity(analyticsEventEntity)
    }

    /**
     * Deletes all the raw data from all the analytics tables once it is processed for production build
     */
    fun deleteData(reportStatus: String) {
        analyticsDao.deleteProcessedReports(reportStatus)

    }

}