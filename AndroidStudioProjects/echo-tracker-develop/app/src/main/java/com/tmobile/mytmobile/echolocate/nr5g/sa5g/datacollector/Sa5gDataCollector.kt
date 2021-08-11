package com.tmobile.mytmobile.echolocate.nr5g.sa5g.datacollector

import android.content.Context
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.PostTicket
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticActions
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticsEvent
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELModulesEnum
import com.tmobile.mytmobile.echolocate.nr5g.core.oemdata.Sa5gDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.EchoLocateSa5gDatabase
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.dao.Sa5gDao
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity.BaseEchoLocateSa5gEntity
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity.Sa5gTriggerEntity
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.dataprocessor.*
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.model.BaseSa5gMetricsData
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.reportprocessor.Sa5gDataStatus
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gUtils
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.utils.Sa5gConstants.SA5G_BASE_ENTITY_NULL
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.utils.Sa5gConstants.SA5G_TRIGGER_NULL
import com.tmobile.mytmobile.echolocate.voice.dataprocessor.BaseIntentProcessor
import java.util.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull

/**
 * This class is responsible for init processing and storing data for Sa5g module
 */
class Sa5gDataCollector(val context: Context) {

    private val sa5gDataMetricsWrapper =
        Sa5gDataMetricsWrapper(
            context
        )

    /**
     * Store Sa5g entity data into room database
     * Logs for triggers info
     * @param triggerCode: trigger code when app focus gain, screen on events called
     */
    @ExperimentalCoroutinesApi
    fun storeSa5gEntity(
        triggerCode: Int,
        packageName: String,
        triggerDelay: Int
    ) {
        EchoLocateLog.eLogD("Nr5g CMS Limit-ApplicationState $triggerCode")

        val sessionId = UUID.randomUUID().toString()
        val uniqueId = UUID.randomUUID().toString()

        /** Saved BaseEchoLocateSa5gEntity in to the database*/
        val baseEchoLocateSa5gEntity =
            prepareEchoLocateSa5gEntity(triggerCode, sessionId)
        val sa5gDao = EchoLocateSa5gDatabase.getEchoLocateSa5gDatabase(context).sa5gDao()
        val isDataInserted = sa5gDao.insertBaseEchoLocateSa5gEntity(baseEchoLocateSa5gEntity)

        /** Saved Nr5gTriggerEntity in to the database*/
        val sa5gTriggerEntity = prepareSa5gTriggerEntity(
            baseEchoLocateSa5gEntity.triggerTimestamp,
            triggerCode,
            packageName,
            triggerDelay
        )
        sa5gTriggerEntity.sessionId = sessionId
        sa5gTriggerEntity.uniqueId = uniqueId
        val isSa5gTriggerEntityInserted = sa5gDao.insertSa5gTriggerEntity(sa5gTriggerEntity)
        if (isSa5gTriggerEntityInserted <= 0) {
            postAnalyticsEventForSa5gFailed(
                SA5G_TRIGGER_NULL,
                ELAnalyticActions.EL_DATA_COLLECTION_FAILED
            )
            return
        }

        /** Log for verify a triggers*/
        EchoLocateLog.eLogD(
            "Trigger Invoked SA5G: " +
                    " TriggerTimestamp: ${baseEchoLocateSa5gEntity.triggerTimestamp}" +
                    " TriggerCode: $triggerCode" +
                    " TriggerApplication: $packageName",
            System.currentTimeMillis()
        )

        /** Execute processor classes to save a data to DataBase */
        val apiVersion = sa5gDataMetricsWrapper.getApiVersion()
        val disposables = CompositeDisposable()
        if (isDataInserted > 0) {
            runBlocking<Any> {
                withTimeoutOrNull(Nr5gUtils.TWENTY_ONE_SECONDS) {
                    sa5gProcessors(
                        sessionId,
                        apiVersion,
                        baseEchoLocateSa5gEntity,
                        context,
                        sa5gDataMetricsWrapper
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
                updateBaseEchoLocateSa5gEntityStatus(baseEchoLocateSa5gEntity, sa5gDao)
            }

        } else {
            postAnalyticsEventForSa5gFailed(
                SA5G_BASE_ENTITY_NULL,
                ELAnalyticActions.EL_DATA_COLLECTION_FAILED
            )
            EchoLocateLog.eLogD("BaseEchoLocateSa5gEntity not inserted")
        }
    }

    /**
     * This function is used to update base echo locate lte entity
     */
    private fun updateBaseEchoLocateSa5gEntityStatus(
        baseEchoLocateSa5gEntity: BaseEchoLocateSa5gEntity,
        sa5gDao: Sa5gDao
    ) {
        baseEchoLocateSa5gEntity.status = Sa5gDataStatus.STATUS_RAW
        sa5gDao.updateBaseEchoLocateSa5gEntityStatus(baseEchoLocateSa5gEntity)
    }

    /**
     * This is used for kotlin coroutines flow setting
     */
    private suspend fun sa5gProcessors(
        sessionId: String,
        apiVersion: Sa5gDataMetricsWrapper.ApiVersion,
        baseEchoLocateSa5gEntity: BaseEchoLocateSa5gEntity,
        context: Context,
        sa5gDataMetricsWrapper: Sa5gDataMetricsWrapper
    ): Flow<Disposable?> = listOf( Sa5gOEMSVProcessor(context).execute(
        BaseSa5gMetricsData(
            ArrayList<String>(),
            baseEchoLocateSa5gEntity.triggerTimestamp,
            apiVersion,
            sessionId
        )
    ),

        Sa5gDeviceInfoProcessor(context).execute(
            BaseSa5gMetricsData(
                ArrayList<String>(),
                baseEchoLocateSa5gEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        ),

        Sa5gLocationProcessor(context).execute(
            BaseSa5gMetricsData(
                ArrayList<String>(),
                baseEchoLocateSa5gEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        ),

        Sa5gDownlinkCarrierLogsProcessor(context).execute(
            BaseSa5gMetricsData(
                sa5gDataMetricsWrapper.getDlCarrierLog(),
                baseEchoLocateSa5gEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        ),

        Sa5gUplinkCarrierLogsProcessor(context).execute(
            BaseSa5gMetricsData(
                sa5gDataMetricsWrapper.getUlCarrierLog(),//UplinkCarrierLogs per schema = UlCarrierLog per dataMatrix
                baseEchoLocateSa5gEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        ),

        Sa5gRrcLogProcessor(context).execute(
            BaseSa5gMetricsData(
                sa5gDataMetricsWrapper.getRrcLog(),
                baseEchoLocateSa5gEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        ),

        Sa5gNetworkLogProcessor(context).execute(
            BaseSa5gMetricsData(
                sa5gDataMetricsWrapper.getNetworkLog(),
                baseEchoLocateSa5gEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        ),

        Sa5gSettingsLogProcessor(context).execute(
            BaseSa5gMetricsData(
                sa5gDataMetricsWrapper.getSettingsLog(),
                baseEchoLocateSa5gEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        ),

        Sa5gUiLogProcessor(context).execute(
            BaseSa5gMetricsData(
                sa5gDataMetricsWrapper.getUiLog(),
                baseEchoLocateSa5gEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        ),

        Sa5gConnectedWifiStatusProcessor(context).execute(
            BaseSa5gMetricsData(
                ArrayList<String>(),
                baseEchoLocateSa5gEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        ),

        Sa5gActiveNetworkProcessor(context).execute(
            BaseSa5gMetricsData(
                ArrayList<String>(),
                baseEchoLocateSa5gEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        ),

        Sa5gWiFiStateProcessor(context).execute(
            BaseSa5gMetricsData(
                ArrayList<String>(),
                baseEchoLocateSa5gEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        ),

        Sa5gCarrierConfigProcessor(context).execute(
            BaseSa5gMetricsData(
                sa5gDataMetricsWrapper.getCarrierConfig(),
                baseEchoLocateSa5gEntity.triggerTimestamp,
                apiVersion,
                sessionId
            )
        )).asFlow()

    /**
     * Returns base echo locate Sa5g entity with current system time
     * @param triggerCode: trigger code of the specific app state
     * */
    private fun prepareEchoLocateSa5gEntity(
        triggerCode: Int,
        sessionId: String
    ): BaseEchoLocateSa5gEntity {
        return BaseEchoLocateSa5gEntity(
            triggerCode,
            "", // While creating the record, the status will be empty (addressed DIA-6523)
            EchoLocateDateUtils.getTriggerTimeStamp(),
            sessionId
        )
    }

    /**
     * Returns Sa5gTriggerEntity with current system time
     * */
    private fun prepareSa5gTriggerEntity(
        timestamp: String,
        triggerId: Int,
        triggerApp: String,
        triggerDelay: Int
    ): Sa5gTriggerEntity {
        return Sa5gTriggerEntity(
            timestamp,
            triggerId,
            triggerApp,
            triggerDelay
        )
    }

    /**
     * This function is used to post new event to analytics manager
     * @param status-checks the status of cms config
     * @param payload-stores the status code based on api status
     */
    private fun postAnalyticsEventForSa5gFailed(payload: String, status: ELAnalyticActions) {
        val analyticsEvent = ELAnalyticsEvent(
            moduleName = ELModulesEnum.SA5G,
            action = status,
            payload = payload
        )
        analyticsEvent.timeStamp = System.currentTimeMillis()

        val postAnalyticsTicket = PostTicket(analyticsEvent)
        RxBus.instance.post(postAnalyticsTicket)
    }
}