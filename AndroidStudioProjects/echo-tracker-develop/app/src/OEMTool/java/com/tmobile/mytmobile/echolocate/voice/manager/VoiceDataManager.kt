package com.tmobile.mytmobile.echolocate.voice.manager

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.sqlite.SQLiteDatabaseCorruptException
import androidx.annotation.VisibleForTesting
import com.tmobile.echolocate.CallStatusProto
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.SubscribeTicket
import com.tmobile.mytmobile.echolocate.configmanager.ConfigProvider
import com.tmobile.mytmobile.echolocate.configuration.ConfigKey
import com.tmobile.mytmobile.echolocate.configuration.events.configurationevents.VoiceConfigEvent
import com.tmobile.mytmobile.echolocate.configuration.model.Voice
import com.tmobile.mytmobile.echolocate.reportingmanager.ReportProvider
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.utils.FirebaseUtils
import com.tmobile.mytmobile.echolocate.voice.VoiceReportProvider
import com.tmobile.mytmobile.echolocate.voice.dataprocessor.*
import com.tmobile.mytmobile.echolocate.voice.intentlisteners.BaseVoiceBroadcastReceiver
import com.tmobile.mytmobile.echolocate.voice.intentlisteners.IIntentRegistrar
import com.tmobile.mytmobile.echolocate.voice.model.VoiceSingleSessionReport
import com.tmobile.mytmobile.echolocate.voice.reportprocessor.VoiceDataStatus
import com.tmobile.mytmobile.echolocate.voice.reportprocessor.VoiceReportProcessor
import com.tmobile.mytmobile.echolocate.voice.reportprocessor.VoiceReportScheduler
import com.tmobile.mytmobile.echolocate.voice.repository.VoiceRepository
import com.tmobile.mytmobile.echolocate.voice.repository.database.entity.VoiceReportEntity
import com.tmobile.mytmobile.echolocate.voice.utils.VoiceIntents
import com.tmobile.mytmobile.echolocate.voice.utils.VoiceUtils
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Manager class that mediates interaction with other components,
 * coordinates flow between component of voice data collection modules
 */
class VoiceDataManager(val context: Context) :
    IVoiceIntentHandler {

    companion object {
        const val SourceComponentVoiceModule: String = "VoiceModule"
    }

    private var callsPerSession = VoiceUtils.defaultCallsPerSession

    @Volatile
    private var isVoiceInitialized: Boolean = false

    @Volatile
    private var isBroadcastRegistered: Boolean = false

    private var voiceReportProcessor: VoiceReportProcessor? = null
    private var voiceReportScheduler: VoiceReportScheduler? = null

    private val bus = RxBus.instance
    private var voiceConfigUpdateDisposable: Disposable? = null

    /** Instance of Broadcast Receiver,handle all the OEM intents delivered */
    private var baseVoiceBroadcastReceiver = BaseVoiceBroadcastReceiver(context)

    /** Initialization block */
    init {
        baseVoiceBroadcastReceiver.setListener(this)
        voiceReportProcessor = VoiceReportProcessor.getInstance(context)
    }

    /**
     * initialization data collection for Voice by starting VoiceDataManager
     * update(restart data collection) if CMS configurations changes by starting [updateVoiceModuleConfig]
     *
     * controlled by CMS configurations
     */
    fun initVoiceDataManager() {
        val configProvider = ConfigProvider.getInstance(context)
        val voiceConfig = configProvider.getConfigurationForKey(ConfigKey.VOICE, context)

        if (voiceConfig != null) {
            manageVoiceDataCollection(voiceConfig as Voice)
        } else {
            EchoLocateLog.eLogD(
                "Diagnostic : NULL Voice Configuration, Data Collection not started"
            )
        }
        updateVoiceModuleConfig()


    }

    /**
     * Manage data collection for Voice by:
     * @param [voiceConfig] from CMS configurations
     *
     * define [isVoiceInitialized]
     * controlled by CMS configurations
     */
    private fun manageVoiceDataCollection(voiceConfig: Voice): Boolean {
        EchoLocateLog.eLogD("Diagnostic : CMS config status for Voice: ${voiceConfig.isEnabled}")
        if (!voiceConfig.isEnabled || VoiceUtils.isTmoAppVersionBlackListed(
                context,
                voiceConfig.blacklistedTMOAppVersion
            ) ||
            VoiceUtils.checkTacInList(context, voiceConfig.blacklistedTAC)
        ) {
            stopVoiceDataCollection()
            return isVoiceInitialized
        }
        startVoiceDataCollection(voiceConfig)

        return isVoiceInitialized
    }

    /**
     * Start data collection for Voice by:
     * - Register Report Types
     * - start Voice Report Scheduler
     * - Register Receiver
     *
     * define [isVoiceInitialized] as true
     * controlled by CMS configurations
     */
    private fun startVoiceDataCollection(voiceConfig: Voice) {
        if (!isVoiceInitialized) {
            EchoLocateLog.eLogD(
                "Diagnostic :Voice Data Collection is started"
            )

            ReportProvider.getInstance(context).initReportingModule()
                .registerReportTypes(VoiceReportProvider.getInstance(context))

            registerReceiver()

            isVoiceInitialized = true
        }

        if (voiceReportScheduler == null)
            voiceReportScheduler = VoiceReportScheduler(context)

        voiceReportScheduler?.schedulerJob(
            getSamplingInterval(voiceConfig),
            voiceConfig.isEnabled
        )

        callsPerSession = voiceConfig.callsPerSession ?: VoiceUtils.defaultCallsPerSession

        handoverPbFileOnCallENDED()
    }

    private fun handoverPbFileOnCallENDED() {
        CoroutineScope(Dispatchers.IO).launch {
            VoiceRepository.gcallStatusDataStore?.data
                ?.collect {
                    var ack = false
                    val lastCall = it.callList.lastOrNull()
                    //if the last call is ended
                    if (lastCall?.status == CallStatusProto.Call.Status.ENDED) {
                        it.callList
                            .forEachIndexed { index, call ->
                                // grab all the calls which are started
                                if (call.status == CallStatusProto.Call.Status.STARTED) {
                                    EchoLocateLog.eLogD("DS:flow file given :" + call.callID.plus(".pb") + " INDEX = " + index)
                                    //send to report provider
                                    ack = ack || ReportProvider.getInstance(context)
                                        .idDropBox(mutableListOf(call.callID.plus(".pb")))
                                }
                            }
                        //Now that we have confirmation from report module
                        //clear complete list
                        if (ack) {
                            VoiceRepository.gcallStatusDataStore?.updateData {
                                it.toBuilder().clearCall().build()
                            }
                        }
                    }
                }
        }
    }

    /**
     * Stop data collection for Voice by:
     * - unRegister Report Types
     * - stop Scheduler
     * - unRegister Receiver
     *
     * define [isVoiceInitialized] as false
     * controlled by CMS configurations and Panic Mode
     */
    fun stopVoiceDataCollection() {
        if (isVoiceInitialized) {
            ReportProvider.getInstance(context)
                .unRegisterReportTypes(VoiceReportProvider.getInstance(context))

            voiceReportScheduler?.stopScheduler()

            unRegisterReceiver()

            isVoiceInitialized = false
            EchoLocateLog.eLogD(
                "Diagnostic : Voice Data Collection is stopped"
            )
        }
    }

    /**
     *This fun listens Configuration module and passing new value to fun @runUpdatedConfigDataCollection
     */
    private fun updateVoiceModuleConfig() {

        val subscribeTicket = SubscribeTicket(RxBus.SubjectType.PUBLISH_SUBJECT)
        voiceConfigUpdateDisposable?.dispose()
        voiceConfigUpdateDisposable = bus.register<VoiceConfigEvent>(subscribeTicket).subscribe {
            runUpdatedConfigDataCollection(it)
        }
    }

    /**
     * This fun receive new value from Configuration module
     * and restart Voice Data Collection with new configuration
     */
    private fun runUpdatedConfigDataCollection(it: VoiceConfigEvent) {
        if (!it.configValue.isEnabled || it.configValue.panicMode || VoiceUtils.isTmoAppVersionBlackListed(
                context,
                it.configValue.blacklistedTMOAppVersion
            ) || VoiceUtils.checkTacInList(context, it.configValue.blacklistedTAC)
        ) {
            stopVoiceDataCollection()
            EchoLocateLog.eLogD(
                "Diagnostic : Function stopVoiceDataCollection started with updated config"
            )
        } else {
            startVoiceDataCollection(it.configValue)
            EchoLocateLog.eLogD(
                "Diagnostic : Function startVoiceDataCollection started with updated config"
            )
        }
    }

    /**
     * This function is responsible for getting the interval for processing the raw data.
     */
    private fun getSamplingInterval(voiceConfig: Voice): Long {
        val intervalHours = voiceConfig.samplingInterval
        // Convert the hours from config to minutes
        return (intervalHours * 60).toLong()
    }

    /**
     * This function is responsible for getting the interval for processing the raw data.
     */
    fun getCombinedCallsSettings(): Int {
        return if (callsPerSession > 0) callsPerSession else VoiceUtils.defaultCallsPerSession
    }

    /**
     * generates report by collecting data on mobile devices and
     * sending of OEM intents delivered to the application. These intents are the custom
     * intents implemented by the OEMS for the TMO Applications and can be listened only
     * by the system application. Those intents deliver detailed log data about Calls made
     * from or to the device. Those include information about protocol messages, audio packet
     * loss status, network environment, call status, call application etc.
     */
    fun getVoiceReport(voiceReportEntityList: List<VoiceReportEntity>): VoiceSingleSessionReport {
        return voiceReportProcessor!!.getVoiceSingleSessionReport(voiceReportEntityList)
    }

    /**
     * This function returns the list of voice reports entity
     * @param startTime reports start date
     * @param endTime reports end date
     */
    fun getVoiceReportEntity(startTime: Long, endTime: Long): List<VoiceReportEntity> {
        val voiceReportEntityList = voiceReportProcessor!!.getVoiceReportEntity(startTime, endTime)
        voiceReportEntityList.let {
            it.forEach {
                it.reportStatus = VoiceDataStatus.STATUS_REPORTING
            }
        }
        voiceReportProcessor!!.updateVoiceReportEntity(voiceReportEntityList)
        return voiceReportEntityList
    }

    /**
     * Initializes the intent registrars by getting the list of the actions required for voice module.
     * converts the actions to intent filters to register reciever dynamically
     */
    private fun initVoiceRegistration(): IIntentRegistrar {
        return object :
            IIntentRegistrar {
            override fun registerVoiceReceiver(intentActions: MutableList<String>) {
                val intentFilter = convertStringToIntentFilter(intentActions)
                context.registerReceiver(baseVoiceBroadcastReceiver, intentFilter)
            }
        }
    }

    /**
     * Prepares the list of all actions required by voice module
     * @param intentRegister: [IIntentRegistrar] after preparing list of actions it calls registerVoiceReceiver
     * to register all the actions dynamically
     */
    private fun registerVoiceActions(intentRegister: IIntentRegistrar) {
        EchoLocateLog.eLogD("Registered all Voice Action intents")

        val intentActions: MutableList<String> = arrayListOf()

        intentActions.add(VoiceIntents.DETAILED_CALL_STATE)
        intentActions.add(VoiceIntents.CALL_SETTING)
        intentActions.add(VoiceIntents.UI_CALL_STATE)
        intentActions.add(VoiceIntents.RADIO_HAND_OVER_STATE)
        intentActions.add(VoiceIntents.IMS_SIGNALING_MESSAGE)
        intentActions.add(VoiceIntents.APP_TRIGGERED_CALL)
        intentActions.add(VoiceIntents.RTPDL_STAT)
        intentActions.add(VoiceIntents.EMERGENCY_CALL_TIMER_STATE)
        intentActions.add(VoiceIntents.CARRIER_CONFIG)

        intentRegister.registerVoiceReceiver(intentActions)
    }

    /**
     * Converts the list of actions wwhich is string to intent filter
     * @param intentActions: MutableList<String> - list of all actions supported by voice module
     * @return MutableList<IntentFilter> list of converted intent filters
     */
    private fun convertStringToIntentFilter(intentActions: MutableList<String>): IntentFilter {
        val intentFilter = IntentFilter()

        for (action in intentActions) {
            intentFilter.addAction(action)
        }
        return intentFilter
    }

    /**
     * This method is called when the base broadcast receiver receives the broad cast.
     * The intents are passed to this class to handle each intent individually based on the action received
     * @param intent : Intent received from broad cast receiver
     */
    override suspend fun onHandleIntent(intent: Intent?, eventTimestamp: Long) {
        try {
            when (intent?.action) {
                VoiceIntents.DETAILED_CALL_STATE -> {
                    val detailedCallStateListener = DetailedCallStateProcessor(context)
                    detailedCallStateListener.execute(intent, eventTimestamp)
                }

                VoiceIntents.CALL_SETTING -> {
                    val callSettingProcessor = CallSettingProcessor(context)
                    callSettingProcessor.execute(intent, eventTimestamp)
                }

                VoiceIntents.UI_CALL_STATE -> {
                    val uiCallStateProcessor = UiCallStateProcessor(context)
                    uiCallStateProcessor.execute(intent, eventTimestamp)
                }

                VoiceIntents.RADIO_HAND_OVER_STATE -> {
                    val radioHandOverStateProcessor = RadioHandOverStateProcessor(context)
                    radioHandOverStateProcessor.execute(intent, eventTimestamp)
                }

                VoiceIntents.RTPDL_STAT -> {
                    val rtpdlCallStateProcessor = RtpdlCallStateProcessor(context)
                    rtpdlCallStateProcessor.execute(intent, eventTimestamp)
                }

                VoiceIntents.IMS_SIGNALING_MESSAGE -> {
                    val imsSignallingDataProcessor = ImsSignallingDataProcessor(context)
                    imsSignallingDataProcessor.execute(intent, eventTimestamp)
                }

                VoiceIntents.APP_TRIGGERED_CALL -> {
                    val appTriggeredCallListener = AppTriggeredCallProcessor(context)
                    appTriggeredCallListener.execute(intent, eventTimestamp)
                }

                VoiceIntents.EMERGENCY_CALL_TIMER_STATE -> {
                    val emergencyCallTimerStateListener = EmergencyCallTimerStateProcessor(context)
                    emergencyCallTimerStateListener.execute(intent, eventTimestamp)
                }

                VoiceIntents.CARRIER_CONFIG -> {
                    val carrierConfigListener = CarrierConfigProcessor(context)
                    carrierConfigListener.execute(intent, eventTimestamp)
                }
            }
        } catch (ex: SQLiteDatabaseCorruptException) {
            FirebaseUtils.logCrashToFirebase(
                "Exception in VoiceDataManager : onHandleIntent() ${intent?.action}",
                ex.localizedMessage,
                "SQLiteDatabaseCorruptException"
            )
        }
    }

    /**
     * This function unregisters the broadcast
     */
    private fun unRegisterReceiver() {
        if (isBroadcastRegistered) {
            context.unregisterReceiver(baseVoiceBroadcastReceiver)
            setBroadCastRegistered(false)
        }
    }

    /**
     * This function registers the broadcast
     */
    fun registerReceiver() {

        if (!isBroadcastRegistered) {

            val intentRegistrar = initVoiceRegistration()
            registerVoiceActions(intentRegistrar)
            setBroadCastRegistered(true)
        }
    }

    /**
     * Sets the boolean to true if the broadcast is registered
     * @param flag: Boolean
     */
    @VisibleForTesting
    fun setBroadCastRegistered(flag: Boolean) {
        isBroadcastRegistered = flag
    }

    /**
     * Returns true/false based on the broadcast registered state
     * @return [Boolean]
     */
    fun isBroadCastRegistered(): Boolean {
        return isBroadcastRegistered
    }

    /**
     * Tells if the voice manager is up and running.
     */
    fun isManagerInitialized(): Boolean {
        return isVoiceInitialized
    }

    /**
     * This function deletes the processed report data from db
     */
    fun deleteProcessedReportsFromDatabase() {
        voiceReportProcessor?.deleteProcessedReports(VoiceDataStatus.STATUS_REPORTING)
    }

}