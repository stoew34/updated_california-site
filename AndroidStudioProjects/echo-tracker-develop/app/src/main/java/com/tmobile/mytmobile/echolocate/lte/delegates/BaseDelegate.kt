package com.tmobile.mytmobile.echolocate.lte.delegates

import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.PostTicket
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticActions
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticsEvent
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELModulesEnum
import com.tmobile.mytmobile.echolocate.configuration.model.LTE
import com.tmobile.mytmobile.echolocate.lte.database.EchoLocateLteDatabase
import com.tmobile.mytmobile.echolocate.lte.database.dao.LteDao
import com.tmobile.mytmobile.echolocate.lte.database.entity.BaseEchoLocateLteEntity
import com.tmobile.mytmobile.echolocate.lte.dataprocessor.*
import com.tmobile.mytmobile.echolocate.lte.lteevents.ApplicationTriggerLimitEvent
import com.tmobile.mytmobile.echolocate.lte.model.LteMetricsData
import com.tmobile.mytmobile.echolocate.lte.oemdata.LteBaseDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.lte.oemdata.LteDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.lte.reportprocessor.LteDataStatus
import com.tmobile.mytmobile.echolocate.lte.utils.*
import com.tmobile.mytmobile.echolocate.lte.utils.LteConstants.LTE_BASE_ENTITY_NULL
import com.tmobile.mytmobile.echolocate.lte.utils.logcat.LogcatListener
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import java.util.*
import kotlin.collections.ArrayList


/**
 * This class is responsible to provide the implementation logic for both stream and non steam delegates
 * This provides base functions to process the states and actions of a respective delegate
 * and invokes and logcat listener
 */
abstract class BaseDelegate(val context: Context) {

    val TIMEOUT_ID_EXTRA = "TIMEOUT_ID_EXTRA"
    internal val NO_INIT_DELAY = 0
    internal val DELAY_TEN_SECONDS = 10 * DateUtils.SECOND_IN_MILLIS
    internal val TIMEOUT_FIVE_SECONDS = 5 * DateUtils.SECOND_IN_MILLIS
    internal val TIMEOUT_TEN_MINUTES = 10 * DateUtils.MINUTE_IN_MILLIS
    internal val TIMEOUT_SEVENTY_MINUTES = 70 * DateUtils.MINUTE_IN_MILLIS
    private val NO_CODE_SPREAD = 0
    private val ONE_SHOT = emptyList<Int>()
    private val TIMEOUT_SET_KEY = "TIMEOUT_SET_KEY"
    private val STATE_FOCUSED = true
    private val STATE_UNFOCUSED = false
    protected var logcatListener: LogcatListener? = null
    protected var ltePeriodicTimer: LtePeriodicTimer? = null
    protected var timeoutId = UUID.randomUUID()
        .toString()

    private var applicationTrigger: ApplicationTrigger = ApplicationTrigger.getInstance(context)


    init {
        logcatListener = LogcatListener.getInstance()
    }

    /**
     * @param triggerApplication app for which timeout occur
     * @return timeout shared pref key
     */
    fun getTimeoutSetKey(triggerApplication: LTEApplications): String {
        return triggerApplication.getKey() + TIMEOUT_SET_KEY
    }

    /**
     * Returns base echo locate lte entity with current system time
     * @param triggerCode: trigger code of the specific app state
     * */
    fun prepareEchoLocateLteEntity(triggerCode: Int, sessionId: String): BaseEchoLocateLteEntity {
        return BaseEchoLocateLteEntity(
            triggerCode,
            "", // While creating the record, the status will be empty (addressed DIA-6523)
            EchoLocateDateUtils.getTriggerTimeStamp(),
            LteConstants.API_VERSION.toString(),
            LteConstants.SCHEMA_VERSION,
            sessionId
        )
    }

    /**
     * Store lte entity data into room database
     * @param triggerCode: trigger code when app focus gain , focus loss or screen off events called
     * @param yTLink: Optional parameter, For other applications it will be empty, you tube link.
     * @param yTContentId: Optional parameter, For other applications it will be empty, you tube content id.
     * handle all the db store logic here
     */
    @ExperimentalCoroutinesApi
    fun storeLteEntity(
        triggerCode: Int,
        yTLink: String = LteConstants.EMPTY,
        yTContentId: String = LteConstants.EMPTY
    ) {

        EchoLocateLog.eLogE("Diagnostic : ApplicationState $triggerCode")
        val count = applicationTrigger.getTriggerCount()
        EchoLocateLog.eLogV("Diagnostic : CMS Limit-base delegate -  before increase: $count")

        applicationTrigger.increaseTriggerCount()

        val increasedCount = applicationTrigger.getTriggerCount()
        EchoLocateLog.eLogV("Diagnostic : CMS Limit-base delegate -  after increase: $increasedCount")

        if (applicationTrigger.isCountWithinLimit()) {
            EchoLocateLog.eLogV("Diagnostic : CMS Limit-base delegate -  limit dint reach so collecting data")
            val sessionId = UUID.randomUUID().toString()
            val baseEchoLocateLteEntity = prepareEchoLocateLteEntity(triggerCode, sessionId)
            EchoLocateLog.eLogE("Diagnostic : ApplicationState ${baseEchoLocateLteEntity.sessionId}")
            val lteDao = EchoLocateLteDatabase.getEchoLocateLteDatabase(context).lteDao()
            val isDataInserted = lteDao.insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity)

            EchoLocateLog.eLogD(
                "Diagnostic : LTE Trigger Invoked: " +
                        " TriggerTimestamp: ${baseEchoLocateLteEntity.triggerTimestamp}" +
                        " TriggerCode: $triggerCode" +
                        " ApplicationState: ${getApplicationState(triggerCode)?.name}" +
                        " TriggerApplication: ${getTriggerApplication()?.getPackageName()}",
                System.currentTimeMillis()
            )

            /** Execute processor classes to save a data to DataBase */
            val lteDataMetricsWrapper = LteDataMetricsWrapper(context)
            val apiVersion = lteDataMetricsWrapper.getApiVersion()
            val disposables = CompositeDisposable()
            if (isDataInserted > 0) {
                runBlocking<Any> {
                    withTimeoutOrNull(LteUtils.TWENTY_ONE_SECONDS) {
                        lteProcessors(
                            sessionId,
                            apiVersion,
                            baseEchoLocateLteEntity,
                            lteDataMetricsWrapper,
                            yTLink,
                            yTContentId
                        )
                            .onCompletion {
                                EchoLocateLog.eLogD("Diagnostics: All LTE processors execution finished")
                            }
                            .collect { value ->
                                value?.let {
                                    disposables.add(it)
                                }
                            }
                    }
                    disposables.dispose()
                    updateBaseEchoLocateLteEntity(baseEchoLocateLteEntity, lteDao)
                }
            } else {
                postAnalyticsEventForLteFailed(
                    LTE_BASE_ENTITY_NULL,
                    ELAnalyticActions.EL_DATA_COLLECTION_FAILED
                )
                EchoLocateLog.eLogD("BaseEchoLocateSa5gEntity not inserted")
            }
        }

        if (applicationTrigger.isTriggerLimitReached()) {
            EchoLocateLog.eLogD("CMS Limit-limit reached so posting event")
            val postTicket = PostTicket(ApplicationTriggerLimitEvent(true))
            RxBus.instance.post(postTicket)
            return
        }
    }

    /**
     * This function is used to update base echo locate lte entity
     */
    private fun updateBaseEchoLocateLteEntity(baseEchoLocateLteEntity: BaseEchoLocateLteEntity, lteDao: LteDao){
        baseEchoLocateLteEntity.status = LteDataStatus.STATUS_RAW
        lteDao.updateBaseEchoLocateLteEntityStatus(baseEchoLocateLteEntity)
    }

    /**
     * This is used for kotlin coroutines flow setting
     */
    private suspend fun lteProcessors(
        sessionId: String,
        apiVersion: LteBaseDataMetricsWrapper.ApiVersion,
        baseEchoLocateLteEntity: BaseEchoLocateLteEntity,
        lteDataMetricsWrapper: LteDataMetricsWrapper,
        yTLink: String = LteConstants.EMPTY,
        yTContentId: String = LteConstants.EMPTY
    ): Flow<Disposable?> = listOf(
        UpLinkRFConfigurationProcessor(context).execute(
            LteMetricsData(
                lteDataMetricsWrapper.getUplinkRFConfiguration(),
                baseEchoLocateLteEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        ),

        BearerConfigurationProcessor(context).execute(
            LteMetricsData(
                lteDataMetricsWrapper.getBearerConfiguration(),
                baseEchoLocateLteEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        ),

        UpLinkCarrierInfoProcessor(context).execute(
            LteMetricsData(
                lteDataMetricsWrapper.getUplinkCarrierInfo(),
                baseEchoLocateLteEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        ),

        CommonRFConfigurationProcessor(context).execute(
            LteMetricsData(
                lteDataMetricsWrapper.getCommonRFConfiguration(),
                baseEchoLocateLteEntity.triggerTimestamp,
                apiVersion,
                sessionId, yTLink, yTContentId
            )
        ),

        SignalConditionProcessor(context).execute(
            LteMetricsData(
                lteDataMetricsWrapper.getSignalCondition(),
                baseEchoLocateLteEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        ),

        DataSettingsProcessor(context).execute(
            LteMetricsData(
                lteDataMetricsWrapper.getDataSetting(),
                baseEchoLocateLteEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        ),

        OEMSVProcessor(context).execute(
            LteMetricsData(
                ArrayList(),
                baseEchoLocateLteEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        ),

        LteLocationDataProcessor(context).execute(
            LteMetricsData(
                ArrayList(),
                baseEchoLocateLteEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        ),

        NetworkIdentityProcessor(context).execute(
            LteMetricsData(
                lteDataMetricsWrapper.getNetworkIdentity(),
                baseEchoLocateLteEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        ),

        DownLinkCarrierInfoProcessor(context).execute(
            LteMetricsData(
                lteDataMetricsWrapper.getDownlinkCarrierInfo(),
                baseEchoLocateLteEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        ),

        DownLinkRFConfigurationProcessor(context).execute(
            LteMetricsData(
                lteDataMetricsWrapper.getDownlinkRFConfiguration(),
                baseEchoLocateLteEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        )
    ).asFlow()

    /**
     * This function is used to post new event to analytics manager
     * @param status-checks the status of cms config
     * @param payload-stores the status code based on api status
     */
    private fun postAnalyticsEventForLteFailed(payload: String, status: ELAnalyticActions) {
        val analyticsEvent = ELAnalyticsEvent(
            moduleName = ELModulesEnum.LTE,
            action = status,
            payload = payload
        )
        analyticsEvent.timeStamp = System.currentTimeMillis()

        val postAnalyticsTicket = PostTicket(analyticsEvent)
        RxBus.instance.post(postAnalyticsTicket)
    }

    /**
     * This function prepares the intent from timeout action value and intent will be passed to alarm manager
     */
    fun prepareTimeoutIntent(action: String): Intent {
        return Intent(action)
    }


    /**
     * Invoked when application state changed
     *
     * @param state new state
     */
    abstract fun processApplicationState(state: ApplicationState)

    /**
     * Invoked on new intent
     *
     * @param intent action
     */
    abstract fun handleIntent(intent: Intent?)


    /**
     * @return trigger application
     */
    internal abstract fun getTriggerApplication(): LTEApplications?

    /**
     * @return logcat listeners ids
     */
    internal abstract fun getLogcatListenerIds(): List<String>

    /**
     * @return timeout intent action
     */
    internal abstract fun getTimeoutAction(): String

    /**
     * @return timeout intent request code
     */
    internal abstract fun getTimeoutRequestCode(): Int

    /**
     * @return trigger application
     */
    internal abstract fun getApplicationState(triggerCode: Int): ApplicationState?

    /**
     * @return BaseDelegate
     */
    internal abstract fun setRegexFromConfig(lteConfig: LTE): BaseDelegate?

}