package com.tmobile.mytmobile.echolocate.coverage.manager


import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import com.tmobile.mytmobile.echolocate.configuration.events.configurationevents.CoverageConfigEvent
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.PostTicket
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.SubscribeTicket
import com.tmobile.mytmobile.echolocate.configuration.ConfigKey
import com.tmobile.mytmobile.echolocate.configmanager.ConfigProvider
import com.tmobile.mytmobile.echolocate.configuration.model.Coverage
import com.tmobile.mytmobile.echolocate.coverage.CoverageModuleProvider
import com.tmobile.mytmobile.echolocate.coverage.CoverageReportProvider
import com.tmobile.mytmobile.echolocate.coverage.database.EchoLocateCoverageDatabase
import com.tmobile.mytmobile.echolocate.coverage.database.dao.CoverageDao
import com.tmobile.mytmobile.echolocate.coverage.database.entity.CoverageSingleSessionReportEntity
import com.tmobile.mytmobile.echolocate.coverage.delegates.CallStateTrigger
import com.tmobile.mytmobile.echolocate.coverage.delegates.DataSessionTrigger
import com.tmobile.mytmobile.echolocate.coverage.events.CoverageTriggerCountListenerEvent
import com.tmobile.mytmobile.echolocate.coverage.delegates.ScreenStateDelegate
import com.tmobile.mytmobile.echolocate.coverage.model.CoverageSingleSessionReport
import com.tmobile.mytmobile.echolocate.coverage.reportprocessor.CoverageDataStatus
import com.tmobile.mytmobile.echolocate.coverage.reportprocessor.CoverageReportProcessor
import com.tmobile.mytmobile.echolocate.coverage.reportprocessor.CoverageReportScheduler
import com.tmobile.mytmobile.echolocate.coverage.utils.CoverageConstants
import com.tmobile.mytmobile.echolocate.reportingmanager.ReportProvider
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * This class is responsible to control the data collection for coverage module
 */
class CoverageDataManager(val context: Context) {

    @Volatile
    private var isCoverageInitialized = false
    private var isScreenTriggerRegistered: Boolean = false
    private var isCallStateTriggerRegistered: Boolean = false
    private var isDataSessionStateTriggerRegistered: Boolean = false

    private val bus = RxBus.instance
    private var coverageReportProcessor: CoverageReportProcessor? = null
    private var coverageReportScheduler: CoverageReportScheduler? = null

    private var coverageConfigUpdateDisposable: Disposable? = null
    private var triggerResetEventDisposable: Disposable? = null

    /** Initialization block */
    init {
        coverageReportProcessor = CoverageReportProcessor.getInstance(context)
    }


    /**
     * initialization data collection for Coverage by starting initCoverageDataManager
     * update(restart data collection) if CMS configurations changes by starting [updateCoverageModuleConfig]
     *
     * controlled by CMS configurations
     */
    fun initCoverageDataManager() {
        val configProvider = ConfigProvider.getInstance(context)
        val coverageConfig = configProvider.getConfigurationForKey(ConfigKey.COVERAGE, context)
        if (coverageConfig != null) {
            manageCoverageDataCollection(coverageConfig as Coverage)
        } else {
            EchoLocateLog.eLogD(
                "Diagnostic : Coverage Configuration is NULL, Data Collection not started")
        }
        updateCoverageModuleConfig()
    }

    /**
     * Manage data collection for Coverage by:
     * @param [coverageConfig] from CMS configurations
     *
     * define [isCoverageInitialized]
     * controlled by CMS configurations
     */
    private fun manageCoverageDataCollection(coverageConfig: Coverage): Boolean {
        EchoLocateLog.eLogD(
            "Diagnostic : CMS config status for Coverage: ${coverageConfig.isEnabled}")

        if (!coverageConfig.isEnabled) {
            stopCoverageDataCollection()
            return isCoverageInitialized
        }

        startCoverageDataCollection(coverageConfig)

        return isCoverageInitialized
    }

    /**
     * Start data collection for Coverage by:
     * - Register Report Types
     * - start Coverage Report Scheduler
     * - Register Receiver
     *
     * define [isCoverageInitialized] as true
     * controlled by CMS configurations
     */
    private fun startCoverageDataCollection(coverageConfig: Coverage) {
        updateStatusEmptyToRaw()

        if (!isCoverageInitialized) {
            EchoLocateLog.eLogD("Diagnostic : Function startCoverageDataCollection started")

            ReportProvider.getInstance(context).initReportingModule()
                .registerReportTypes(CoverageReportProvider.getInstance(context))

            listenResetTriggerAction()

            createPeriodicTriggerAlarm()

            isCoverageInitialized = true
        }

        if (coverageReportScheduler == null) {
            coverageReportScheduler = CoverageReportScheduler(context)
        }
        coverageReportScheduler?.schedulerJob(
            getSamplingInterval(coverageConfig),
            coverageConfig.isEnabled
        )

        // Receivers for all triggers need to re-register
        // Individual trigger will be responsible to check if they need to re-register
        registerCoverageReceiver(coverageConfig)
    }

    /**
     * This function will update empty status to RAW.
     */
    private fun updateStatusEmptyToRaw() {
        val coverageDao: CoverageDao = EchoLocateCoverageDatabase.getEchoLocateCoverageDatabase(context).coverageDao()
        val baseEchoCoverageEntityList = coverageDao.getBaseEchoLocateCoverageEntityByStatus("")
        baseEchoCoverageEntityList.forEach { f -> f.status = CoverageDataStatus.STATUS_RAW }
        coverageDao.updateAllBaseEchoLocateCoverageEntityStatus(*baseEchoCoverageEntityList.toTypedArray())
    }


    /**
     * Stop data collection for Coverage by:
     * - unRegister Report Types
     * - stop Scheduler
     * - unRegister Receiver
     *
     * define [isCoverageInitialized] as false
     * controlled by CMS configurations and Panic Mode
     */
    fun stopCoverageDataCollection() {

        ReportProvider.getInstance(context).unRegisterReportTypes(CoverageReportProvider.getInstance(context))

        if (isScreenTriggerRegistered) {
            ScreenStateDelegate.getInstance(context).dispose()
            isScreenTriggerRegistered = false
        }
        if (isCallStateTriggerRegistered) {
            CallStateTrigger.getInstance(context).dispose()
            isCallStateTriggerRegistered = false
        }
        if (isDataSessionStateTriggerRegistered) {
            DataSessionTrigger.getInstance(context).dispose()
            isDataSessionStateTriggerRegistered = false
        }

        isCoverageInitialized = false

        stopPeriodicActions()

        coverageReportScheduler?.stopScheduler()
    }

    /**
     *This fun listens Configuration module and passing new value to fun @runUpdatedConfigForDataCollection
     */
    private fun updateCoverageModuleConfig() {

        val subscribeTicket = SubscribeTicket(RxBus.SubjectType.PUBLISH_SUBJECT)

        coverageConfigUpdateDisposable?.dispose()
        coverageConfigUpdateDisposable =
            bus.register<CoverageConfigEvent>(subscribeTicket).subscribe {
                runUpdatedConfigForDataCollection(it)
            }
    }

    /**
     * This fun receive new value from Configuration module
     * and restart Coverage Data Collection with new configuration
     */
    private fun runUpdatedConfigForDataCollection(it: CoverageConfigEvent) {
        if (!it.configValue.isEnabled || it.configValue.panicMode) {
            stopCoverageDataCollection()
        } else {
            startCoverageDataCollection(it.configValue)
        }
        EchoLocateLog.eLogV("Diagnostic : Updated coverage config data")
    }

    /**
     * generates report by collecting data on mobile devices and
     * sending of OEM intents delivered to the application. These intents are the custom
     * intents implemented by the OEMS for the TMO Applications and can be listened only
     * by the system application. Those intents deliver detailed log data about Triggers made
     * from or to the device.
     */
    fun getCoverageReport(coverageSingleSessionReportEntityList: List<CoverageSingleSessionReportEntity>): List<CoverageSingleSessionReport> {
        return coverageReportProcessor!!.getCoverageMultiSessionReport(coverageSingleSessionReportEntityList)
    }

    /**
     * This function returns the list of coverage reports entity
     */
    fun getCoverageReportEntity(): List<CoverageSingleSessionReportEntity> {
        val coverageReportEntityList = coverageReportProcessor!!.getCoverageMultiSessionReportEntity()
        coverageReportEntityList.let {
            it.forEach {
                it.reportStatus = CoverageDataStatus.STATUS_REPORTING
            }
        }
        coverageReportProcessor!!.updateCoverageReportEntity(coverageReportEntityList)
        return coverageReportEntityList

    }

    /**
     * This function deletes the the report data from db based on data from coverageSingleSessionReportEntity
     */
    fun deleteProcessedReportsFromDatabase() {
        coverageReportProcessor?.deleteProcessedReports(CoverageDataStatus.STATUS_REPORTING)
    }

    /**
     * This function is responsible for getting the interval for processing the raw data.
     */
    private fun getSamplingInterval(coverageConfig: Coverage): Long {

        val intervalHours = coverageConfig.samplingInterval

        // Convert the hours from config to minutes
        return (intervalHours * 60).toLong()
    }

    /**
     * This fun registers the coverage triggers
     */
    private fun registerCoverageReceiver(coverageConfig: Coverage) {
        if (!coverageConfig.isEnabled) return

        EchoLocateLog.eLogD("Diagnostic : registerCoverageReceiver")

        // Initiate the screen triggers
        isScreenTriggerRegistered = ScreenStateDelegate.getInstance(context).initTrigger(coverageConfig)
        isCallStateTriggerRegistered =
            CallStateTrigger.getInstance(context).initTrigger(coverageConfig)
        isDataSessionStateTriggerRegistered =
            DataSessionTrigger.getInstance(context).initTrigger(coverageConfig)
    }

    /**
     * Creates periodic timer to reset the count of all coverage triggers
     */
    private fun createPeriodicTriggerAlarm() {
        EchoLocateLog.eLogV("Diagnostic : CMS Limit-scheduling periodic coverage trigger alarm")

        val intent = Intent(context, CoverageTriggerResetReceiver::class.java)
        intent.action = CoverageConstants.TRIGGER_COUNT_RESET_ACTION

        val timeoutPendingIntent = PendingIntent.getBroadcast(
            context, CoverageConstants.COVERAGE_TRIGGER_ALARM_CODE, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_HOUR,
            AlarmManager.INTERVAL_HOUR,
            timeoutPendingIntent
        )

    }

    /**
     * Stops periodic timer for coverage triggers
     */
    private fun stopPeriodicActions() {
        val intent = Intent(context, CoverageTriggerResetReceiver::class.java)
        intent.action = CoverageConstants.TRIGGER_COUNT_RESET_ACTION
        val timeoutPendingIntent = PendingIntent.getBroadcast(
            context, CoverageConstants.COVERAGE_TRIGGER_ALARM_CODE, intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(
            timeoutPendingIntent
        )

        timeoutPendingIntent?.cancel()

        bus.unregister(triggerResetEventDisposable)

        //allowing the disposable to register on the next event, thus making it null
        triggerResetEventDisposable = null

        EchoLocateLog.eLogV("Coverage hourly reset alarm canceled")

    }

    /**
     * Tells if the coverage manager is up and running.
     */
    fun isManagerInitialized(): Boolean {
        return isCoverageInitialized
    }

    /**
     * This is used to listen the actions for resetting trigger count in every hour.
     */
    private fun listenResetTriggerAction() {
        if (triggerResetEventDisposable != null) {
            triggerResetEventDisposable!!.dispose()
        }

        val subscribeTicket =
            SubscribeTicket(RxBus.SubjectType.PUBLISH_SUBJECT)
        triggerResetEventDisposable =
            bus.register<CoverageTriggerCountListenerEvent>(subscribeTicket)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe {
                    EchoLocateLog.eLogV("Diagnostic : Coverage : resetting the trigger")
                    resetCoverageTriggers()
                }
    }

    /**
     * Resets all the coverage triggers
     */
    private fun resetCoverageTriggers() {
        //reset the trigger count for screen triggers
        if(isScreenTriggerRegistered) ScreenStateDelegate.getInstance(context).resetTrigger()
        if (isCallStateTriggerRegistered) CallStateTrigger.getInstance(context).resetTrigger()
        if (isDataSessionStateTriggerRegistered) DataSessionTrigger.getInstance(context).resetTrigger()
    }

    /**
     * Receiver for the alarm manager that triggers every 24 hours to reset the trigger count
     */
    class CoverageTriggerResetReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val postTicket = PostTicket(
                CoverageTriggerCountListenerEvent(
                    intent
                )
            )
            RxBus.instance.post(postTicket)
        }
    }

}