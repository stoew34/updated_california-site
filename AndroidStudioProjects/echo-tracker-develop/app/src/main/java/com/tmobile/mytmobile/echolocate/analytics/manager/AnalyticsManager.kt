package com.tmobile.mytmobile.echolocate.analytics.manager

import android.content.Context
import android.os.health.HealthStats
import android.os.health.ProcessHealthStats
import android.os.health.SystemHealthManager
import android.os.health.UidHealthStats
import com.tmobile.mytmobile.echolocate.analytics.AnalyticsReportProvider
import com.tmobile.mytmobile.echolocate.analytics.scheduler.AnalyticsScheduler
import com.tmobile.mytmobile.echolocate.analytics.database.entity.AnalyticsEventEntity
import com.tmobile.mytmobile.echolocate.analytics.database.repository.AnalyticsRepository
import com.tmobile.mytmobile.echolocate.analytics.model.AnalyticsReport
import com.tmobile.mytmobile.echolocate.analytics.reportprocessor.AnalyticsReportProcessor
import com.tmobile.mytmobile.echolocate.analytics.utils.AnalyticsConstants
import com.tmobile.mytmobile.echolocate.analytics.utils.AnalyticsConstants.DATAMETRIC_SCHEDULER_INTERVAL_IN_MINS
import com.tmobile.mytmobile.echolocate.analytics.utils.AnalyticsEntityConverter
import com.tmobile.mytmobile.echolocate.analytics.utils.AnalyticsSharedPreference
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticActions
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticsEvent
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELModulesEnum
import com.tmobile.mytmobile.echolocate.configuration.events.configurationevents.AnalyticsConfigEvent
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.PostTicket
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.SubscribeTicket
import com.tmobile.mytmobile.echolocate.configuration.ConfigKey
import com.tmobile.mytmobile.echolocate.configmanager.ConfigProvider
import com.tmobile.mytmobile.echolocate.configuration.model.Analytics
import com.tmobile.mytmobile.echolocate.coverage.reportprocessor.CoverageDataStatus
import com.tmobile.mytmobile.echolocate.reportingmanager.ReportProvider
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Manager class that mediates interaction with other components,
 * coordinates flow between component of analytics data collection modules
 */
class AnalyticsManager(val context: Context) {

    companion object {
        const val SourceComponentAnalyticsModule: String = "AnalyticsModule"
    }

    @Volatile
    private var isAnalyticsInitialized: Boolean = false

    private var analyticsScheduler: AnalyticsScheduler? = null
    private var analyticsRepository = AnalyticsRepository(context)
    private var analyticsReportProcessor: AnalyticsReportProcessor? = null

    private val bus = RxBus.instance
    private var analyticsConfigUpdateDisposable: Disposable? = null
    private var analyticsEventDisposable: Disposable? = null

    /** Initialization block */
    init {
        analyticsReportProcessor = AnalyticsReportProcessor.getInstance(context)
    }

    /**
     * initialization data collection for Analytics by starting AnalyticsDataManager
     */
    fun initAnalyticsDataManager() {
        val configProvider = ConfigProvider.getInstance(context)
        val analyticsConfig = configProvider.getConfigurationForKey(ConfigKey.ANALYTICS, context)

        if (analyticsConfig != null) {
            manageAnalyticsDataCollection(analyticsConfig as Analytics)
        }
        updateAnalyticsModuleConfig()
    }

    /**
     * Manage data collection for Analytics by:
     * @param [analyticsConfig] from CMS configurations
     *
     * define [isAnalyticsInitialized]
     * controlled by CMS configurations
     */
    private fun manageAnalyticsDataCollection(analyticsConfig: Analytics): Boolean {

        if (!analyticsConfig.isEnabled) {

            stopAnalyticsDataCollection()

            return isAnalyticsInitialized
        }
        startAnalyticsDataCollection(analyticsConfig)
        return isAnalyticsInitialized
    }

    /**
     * Start data collection for Analytics by:
     * - Register Report Types [ANALYTICS_TYPE]
     * - start Analytics Report Scheduler
     * - Register Receiver
     *
     * define [isAnalyticsInitialized] as true
     * controlled by CMS configurations
     */
    private fun startAnalyticsDataCollection(analyticsConfig: Analytics) {
        if (!isAnalyticsInitialized) {

            if (analyticsScheduler == null) {
                analyticsScheduler= AnalyticsScheduler(context)
            }
            analyticsScheduler?.schedulerJob(DATAMETRIC_SCHEDULER_INTERVAL_IN_MINS.toLong())

            ReportProvider.getInstance(context).initReportingModule()
                .registerReportTypes(AnalyticsReportProvider.getInstance(context))

            subscribeRxBusForAnalyticsEvent()
            EchoLocateLog.eLogD("Echolocate Analytics module: Subscribed to analytics event")
            processCrashStatisticsData()
            isAnalyticsInitialized = true
        }
        if (analyticsConfig.numEventsBundled >= 0)
            analyticsReportProcessor?.numEventsBundled = analyticsConfig.numEventsBundled
    }

    /**
     * Stop data collection for Analytics by:
     * - unRegister Report Types
     * - stop Scheduler
     * - unRegister Receiver
     *
     * define [isAnalyticsInitialized] as false
     * controlled by CMS configurations and Panic Mode
     */
    fun stopAnalyticsDataCollection() {
        if (isAnalyticsInitialized) {

            ReportProvider.getInstance(context).unRegisterReportTypes(AnalyticsReportProvider.getInstance(context))

            analyticsEventDisposable?.dispose()

            isAnalyticsInitialized = false
        }
    }

    /**
     *This fun listens Configuration module and passing new value to fun @runUpdatedConfigForDataCollection
     */
    private fun updateAnalyticsModuleConfig() {

        val subscribeTicket = SubscribeTicket(RxBus.SubjectType.PUBLISH_SUBJECT)

        analyticsConfigUpdateDisposable?.dispose()
        analyticsConfigUpdateDisposable = bus.register<AnalyticsConfigEvent>(subscribeTicket).subscribe {
            runUpdatedConfigForDataCollection(it)
        }
    }

    /**
     * This fun receive new value from Configuration module
     * and restart Analytics Data Collection with new configuration
     */
    private fun runUpdatedConfigForDataCollection(it: AnalyticsConfigEvent) {
        if (!it.configValue.isEnabled || it.configValue.panicMode) {
            stopAnalyticsDataCollection()
        } else {
            startAnalyticsDataCollection(it.configValue)
        }
    }

    /**
     * generates report by collecting data from other modules
     */
    fun getAnalyticsReport(): AnalyticsReport {
        return analyticsReportProcessor!!.getAnalyticsReport()
    }

    /**
     * This fun listens several modules and passing new data once it available
     * Function starts when module starts from [startAnalyticsDataCollection]
     */
    private fun subscribeRxBusForAnalyticsEvent() {

        val subscribeTicket = SubscribeTicket(RxBus.SubjectType.PUBLISH_SUBJECT)

        analyticsEventDisposable?.dispose()
        analyticsEventDisposable = bus.register<ELAnalyticsEvent>(subscribeTicket).subscribe {
            processAnalyticsEventData(it)
        }
    }

    /**
     *  This function processes the obj, converts it to AnalyticsEventEntity and saves it in database
     */
    fun processAnalyticsEventData(elAnalyticsEvent: ELAnalyticsEvent) {

        val analyticsEventEntity =
            AnalyticsEntityConverter.convertAnalyticsEventEntity(elAnalyticsEvent)

        if (analyticsEventEntity.action == ELAnalyticActions.EL_DATAMETRICS_AVAILABILITY.name) {
            CoroutineScope(Dispatchers.IO).launch {
                saveAnalyticsEventToDatabase(analyticsEventEntity)
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            saveAnalyticsEventToDatabase(analyticsEventEntity)
        }
    }

    /**
     * Used to fetch app crash statistics
     */
     private fun processCrashStatisticsData() {
        EchoLocateLog.eLogD("Echolocate Analytics module: Fetching crash statistics..")
        val healthStats: HealthStats?
        var crashCount: Long? = 0L
        var anrCount: Long? = 0L
        var osCrashCount: Long? = 0L
        val systemHealthManager = context.getSystemService(Context.SYSTEM_HEALTH_SERVICE)
                as SystemHealthManager?
        healthStats = systemHealthManager?.takeMyUidSnapshot()
        if (healthStats != null && healthStats.hasStats(UidHealthStats.STATS_PROCESSES)) {
            try {
                crashCount =
                    healthStats.getStats(UidHealthStats.STATS_PROCESSES)[context.packageName]
                        ?.getMeasurement(ProcessHealthStats.MEASUREMENT_CRASHES_COUNT) ?: 0
                anrCount =
                    healthStats.getStats(UidHealthStats.STATS_PROCESSES)[context.packageName]
                        ?.getMeasurement(ProcessHealthStats.MEASUREMENT_ANR_COUNT) ?: 0
            }
            catch(exception: Exception) {
                EchoLocateLog.eLogE("error: ${exception.localizedMessage}")
            }
            AnalyticsSharedPreference.numOfStarts =
                AnalyticsSharedPreference.numOfStarts + 1
            AnalyticsSharedPreference.numOfCrashes =
                AnalyticsSharedPreference.numOfCrashesAtReboot + (crashCount?:0)
            AnalyticsSharedPreference.numOfAnrs =
                AnalyticsSharedPreference.numOfAnrsAtReboot + (anrCount?:0)
            osCrashCount = AnalyticsSharedPreference.numOfStarts.minus(AnalyticsSharedPreference.numOfCrashes)
                    .minus(AnalyticsSharedPreference.numOfAnrs)
                    .minus(AnalyticsSharedPreference.numOfReboots).minus(1)
            /**
             * post analytics event if previous crash was due to OS
             * //TODO Possible implementation change - Post a single event at the end of a time period
             */

            if(osCrashCount.minus(AnalyticsSharedPreference.numOfOSCrashes) > 0) {
                val analyticsEvent = ELAnalyticsEvent(
                    ELModulesEnum.ANALYTICS,
                    ELAnalyticActions.EL_NUMBER_OF_TIMES_APP_KILLED_BY_OS,
                    AnalyticsConstants.OS_CRASH_PAYLOAD
                )
                analyticsEvent.timeStamp = System.currentTimeMillis()

                val postAnalyticsTicket = PostTicket(analyticsEvent)
                RxBus.instance.post(postAnalyticsTicket)
                AnalyticsSharedPreference.numOfOSCrashes = osCrashCount
                AnalyticsSharedPreference.timestampOfAppStartAfterLastKill = EchoLocateDateUtils.getTriggerTimeStamp()
                EchoLocateLog.eLogD("Echolocate Analytics module: OS current crash count from " +
                        "app install: $osCrashCount")
                EchoLocateLog.eLogD("Echolocate Analytics module: OS previous crash count from " +
                        "app install: "+AnalyticsSharedPreference.numOfOSCrashes)
            }
        }
    }

    /**
     * This function deletes the the report data from db based on STATUS_REPORTING
     */
    fun deleteProcessedReportsFromDatabase() {
        analyticsReportProcessor?.deleteProcessedData(CoverageDataStatus.STATUS_REPORTING)
    }

    /**
     * Inserts the data in database
     * @param analyticsEventEntity:[AnalyticsEventEntity]
     */
    private fun saveAnalyticsEventToDatabase(analyticsEventEntity: AnalyticsEventEntity) {
        analyticsRepository.insertAnalyticsEventEntity(analyticsEventEntity)
    }

    /**
     * Tells if the analytics manager is up and running.
     */
    fun isManagerInitialized(): Boolean {
        return isAnalyticsInitialized
    }


}