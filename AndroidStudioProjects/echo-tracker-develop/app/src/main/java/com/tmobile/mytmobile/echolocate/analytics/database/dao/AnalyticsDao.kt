package com.tmobile.mytmobile.echolocate.analytics.database.dao

import androidx.room.*
import com.tmobile.mytmobile.echolocate.analytics.database.AnalyticsDatabaseConstants
import com.tmobile.mytmobile.echolocate.analytics.database.entity.AnalyticsEventEntity
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticActions


@Dao
interface AnalyticsDao {
    /**
     * Inserts [AnalyticsEventEntity] parameters into the database in a single transaction.
     * @param analyticsEventEntity :AnalyticsEventEntity object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(analyticsEventEntity: AnalyticsEventEntity)

    /**
     * Gets list of [AnalyticsEventEntity] parameters from the database in a single transaction.
     */
    @Query("SELECT * FROM " + AnalyticsDatabaseConstants.ANALYTICS_EVENT_TABLE_NAME + " ORDER BY timeStamp ASC LIMIT :limit ")
    fun getAllAnalyticsEventEntity(limit: Int): List<AnalyticsEventEntity>

    /**
     * Update status of AnalyticsBaseEntity
     */
    @Query("SELECT * FROM " + AnalyticsDatabaseConstants.ANALYTICS_EVENT_TABLE_NAME + " WHERE status = :status ")
    fun getAnalyticsEventEntityByStatus(status: String): List<AnalyticsEventEntity>

    /**
     * Deletes all the data from [AnalyticsEventEntity] with status REPORTING
     * @param status
     */
    @Query("DELETE FROM " + AnalyticsDatabaseConstants.ANALYTICS_EVENT_TABLE_NAME + " WHERE status = :status")
    fun deleteProcessedReports(status: String)

    /**
     * Update status for all AnalyticsBaseEntity
     * @param analyticsEventEntity :AnalyticsBaseEntity to update all the AnalyticsBaseEntity items
     */
    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun updateAllAnalyticsEventEntityStatus(vararg analyticsEventEntity: AnalyticsEventEntity)

    /**
     * Deletes all the raw data from all the analytics tables
     * @param analyticsEventEntityList of entity classes that needs to be deleted.
     */
    @Delete
    fun deleteAnalyticsEventEntityList(analyticsEventEntityList: List<AnalyticsEventEntity>)

    /**
     * query to get oldest entry from analytics event table
     */
    @Query("SELECT * from ${AnalyticsDatabaseConstants.ANALYTICS_EVENT_TABLE_NAME} ORDER BY timeStamp ASC LIMIT :limit")
    fun getOldestAnalyticsEventEntityList(limit: Int): List<AnalyticsEventEntity>

    /**
     * Query to get dataMetrics oldest entry from analytics event table
     */
    @Query("SELECT * FROM " + AnalyticsDatabaseConstants.ANALYTICS_EVENT_TABLE_NAME + " WHERE action = :action")
    fun getOldestAnalyticsDataMetricsList(action: String): List<AnalyticsEventEntity>

    /**
     *  limits db
     *  gets number of records in db table
     */
    @Query("SELECT COUNT() FROM ${AnalyticsDatabaseConstants.ANALYTICS_EVENT_TABLE_NAME}")
    fun getCount(): Int

    @Transaction
    fun insertAnalyticsEventEntity(analyticsEventEntity: AnalyticsEventEntity) {
        val count = getCount()

        if (count >= AnalyticsDatabaseConstants.ANALYTICS_EVENT_LIMIT) {
            val oldAnalyticsEventEntityList = getOldestAnalyticsEventEntityList(AnalyticsDatabaseConstants.ANALYTICS_EVENT_NUM_FOR_DELETE)
            deleteAnalyticsEventEntityList(oldAnalyticsEventEntityList)
        }

        if (analyticsEventEntity.action == ELAnalyticActions.EL_DATAMETRICS_AVAILABILITY.name) {
            val oldAnalyticsDataMetricsData = getOldestAnalyticsDataMetricsList(ELAnalyticActions.EL_DATAMETRICS_AVAILABILITY.name)
            deleteAnalyticsEventEntityList(oldAnalyticsDataMetricsData)
        }

        if (analyticsEventEntity.action == ELAnalyticActions.EL_PERMISSION_AVAILABILITY.name) {
            val oldAnalyticsDataMetricsData = getOldestAnalyticsDataMetricsList(ELAnalyticActions.EL_PERMISSION_AVAILABILITY.name)
            deleteAnalyticsEventEntityList(oldAnalyticsDataMetricsData)
        }
        insert(analyticsEventEntity)
    }
}