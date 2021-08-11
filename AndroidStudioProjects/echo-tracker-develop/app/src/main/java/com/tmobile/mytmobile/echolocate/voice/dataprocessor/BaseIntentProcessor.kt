package com.tmobile.mytmobile.echolocate.voice.dataprocessor

//import kotlinx.serialization.ImplicitReflectionSerializer
import android.content.Context
import android.content.Intent
import android.telephony.*
import android.text.TextUtils
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.tmobile.echolocate.CallSessionProto
import com.tmobile.echolocate.CallStatusProto
import com.tmobile.mytmobile.echolocate.BuildConfig
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.PostTicket
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticActions
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticsEvent
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELModulesEnum
import com.tmobile.mytmobile.echolocate.voice.model.*
import com.tmobile.mytmobile.echolocate.voice.repository.VoiceRepository
import com.tmobile.mytmobile.echolocate.voice.repository.datastore.CallSessionSerializer
import com.tmobile.mytmobile.echolocate.voice.utils.MccAccess
import com.tmobile.mytmobile.echolocate.voice.utils.MncAccess
import java.util.*
import java.util.concurrent.Semaphore


/**
 * This class the base class of all the intent listeners to handle all the common logic used by all the
 * voice listerners
 */
abstract class BaseIntentProcessor(val context: Context) {

    companion object {

        /**
         * Size of the collection for [VOICE_ACCESS_NETWORK_STATE_SIGNAL_EXTRA] as received in the intent.
         */
        const val NETWORK_STATE_SIGNAL_VALUES_SIZE = 8

        private const val SEMICOLON = ";"
        private const val EMPTY = ""
        const val OEM_TIMESTAMP_EXTRA = "oemIntentTimestamp"

        const val CALL_ID_EXTRA = "CallID"

        const val CALL_NUMBER_EXTRA = "CallNumber"

        /** CallSettingProcessor intents extra */
        const val CALL_SETTING_VO_LTE_EXTRA = "CallSettingVoLTE"
        const val CALL_SETTING_WFC_EXTRA = "CallSettingWFC"
        const val CALL_SETTING_WFC_PREFERENCE_EXTRA = "CallSettingWFCPreference"

        /** AppTriggeredCallProcessor intents extra */
        const val PACKAGE_NAME_EXTRA = "ApplicationPackageName"

        /** DetailedCallStateProcessor intents extra */
        const val CALL_CODE_EXTRA = "CallCode"
        const val CALL_STATE_EXTRA = "CallState"

        /** ImsSignallingDataProcessor intents extra */
        const val SIP_CALL_ID_EXTRA = "IMSSignallingMessageCallID"
        const val SIP_CSEQ_EXTRA = "IMSSignallingCSeq"
        const val SIP_LINE1_EXTRA = "IMSSignallingMessageLine1"
        const val SIP_ORIGIN_EXTRA = "IMSSignallingMessageOrigin"
        const val SIP_REASON_EXTRA = "IMSSignallingMessageReason"
        const val SIP_SDP_EXTRA = "IMSSignallingMessageSDP"
        private var resultSemaphore: Semaphore? = null
        private const val ACQUIRE_TIMEOUT_SECONDS = 5

        /** RadioHandOverStateProcessor intents extra */
        const val HAND_OVER_STATE_EXTRA = "VoiceRadioBearerHandoverState"

        /** RtpdlCallStateProcessor intents extra */
        const val OEM_DELAY_EXTRA = "RTPDownlinkStatusDelay"
        const val OEM_JITTER_EXTRA = "RTPDownlinkStatusJitter"
        const val OEM_LOSS_RATE_EXTRA = "RTPDownlinkStatusLossRate"
        const val OEM_MEASURED_PERIOD_EXTRA = "RTPDownlinkStatusMeasuredPeriod"

        /** UiCallStateProcessor intents extra */
        const val UI_CALL_STATE_EXTRA = "UICallState"

        /** Carrier Config intents extra*/
        const val CARRIER_VOICE_CONFIG = "carrierVoiceConfig"
        const val CARRIER_VO_WIFI_CONFIG = "carrierVoWiFiConfig"
        const val CARRIER_SA_5G_BAND_CONFIG = "carrierSa5gBandConfig"
        const val STANDALONE_BAND_5G_KEYS = "standaloneBands5gKeys"
        const val STANDALONE_BAND_5G_VALUES = "standaloneBands5gValues"
        const val CARRIER_CONFIG_VERSION = "carrierConfigVersion"

        /**
         * Extra delivered with every EchoLocateIntent. Usually should have values: VOLTE, WFC2, WFC1,
         * 3G, 2G, SEARCHING, AIRPLANE, VIDEO.
         */
        const val VOICE_ACCESS_NETWORK_STATE_TYPE = "VoiceAccessNetworkStateType"

        /**
         * Extra delivered with every EchoLocateIntent. Usually will contain data about bands states
         * divided by commas: i.e. 2, 4, 12
         */
        const val VOICE_ACCESS_NETWORK_STATE_BAND = "VoiceAccessNetworkStateBand"

        /**
         * VoiceAccessNetworkStateSignal. Voice Access Network State Signal sets
         * to ;<RSSI>;<RSCP>;<ECIO>;<RSRP>;<RSRQ>;<SINR>;<SNR>;
         * NOTE:
         * If the carrier is a NR carrier, use
         * SS-RSRP, SS-RSRQ, SS-RSSI, and SS-SINR values for <RSRP>;<RSRQ>;<RSSI>; and<SINR>;.
         */
        const val VOICE_ACCESS_NETWORK_STATE_SIGNAL_EXTRA = "VoiceAccessNetworkStateSignal"

        /** EmergencyCallTimerStateProcessor intents extra */
        const val EMERGENCY_CALL_TIMER_NAME = "TimerName"
        const val EMERGENCY_CALL_TIMER_STATE = "TimerState"

        const val VOICE_CALL_ID_NULL = "callId null"
        const val VOICE_CALL_NUMBER_NULL = "callNumber null"
        const val VOICE_BASE_ENTITY_NULL = "baseEntity null"
    }

    /**
     * Instance for repository for voice module
     */
    var voiceRepository = VoiceRepository(context)



    /**
     * abstract function that enables individual intent listeners to process the received intent
     * @param intent: [Intent]
     *
     * @param eventTimeStamp The timestamp at which the intent was received by the application.
     */
    abstract suspend fun processIntent(intent: Intent, eventTimeStamp: Long): Any

    /**
     * This method is called from the individual intent listeners to process the received intent
     * @param intent: [Intent]
     *
     * @param eventTimeStamp The timestamp at which the intent was received by the application.
     */
    suspend fun execute(intent: Intent, eventTimeStamp: Long): Any? {

        // Before executing intent specific processor, check if the call id already exists,
        // if not create new base entity for that callId
        if (isValidIntent(intent)) {
            // If failed to add base entity, do not process that intent (jira# DIA-7630)
            if (getVoiceDataStore(intent) != null) {
                return processIntent(intent, eventTimeStamp)
            } else {
                postAnalyticsEventForVoiceFailed(
                    VOICE_BASE_ENTITY_NULL,
                    ELAnalyticActions.EL_DATA_COLLECTION_FAILED
                )
            }
        }
        return null
    }


    suspend fun getVoiceDataStore(intent: Intent): DataStore<CallSessionProto> {
        val callID = intent.getStringExtra(CALL_ID_EXTRA)
        if (callID == null) {
            val error =
                "This is a error scenario, very intent should have callID as per Echolocate TRD"
            throw IllegalStateException(error)
        }

        var voiceDataStore: DataStore<CallSessionProto>? =
            VoiceRepository.getVoiceDataStoreFor(callID)

        if (voiceDataStore == null) {
            val pbFileName = callID.plus(".pb")
            voiceDataStore = DataStoreFactory.create(CallSessionSerializer) {
                context.dataStoreFile(pbFileName)
            }
            /**
             * Make sure all the threads use same DataStore object
             */
            VoiceRepository.setVoiceDataStoreFor(callID, voiceDataStore)

            /**
             * Collection of calls & status is recorded. Sometime Call Ended intent is not received
             */

            VoiceRepository.gcallStatusDataStore?.updateData { callStatusProto: CallStatusProto ->
                var newCallDataBuilder = CallStatusProto.Call.newBuilder()
                newCallDataBuilder.callID = callID
                newCallDataBuilder.status = CallStatusProto.Call.Status.STARTED
                callStatusProto.toBuilder().addCall(newCallDataBuilder).build()
            }
            /*
            For the new File add all the required fields
            Oemsv oem = 1;
            string callId = 2;
            string callNumber = 3;
            string clientVersion = 4;
            DeviceIntents deviceIntents = 5; [ Note : Intents will be added as they arrive ]
            NetworkIdentity networkIdentity = 6;
            */

            OEMSoftwareVersionProcessor(context).processIntent(intent, System.currentTimeMillis())

            val voiceDataStore = getVoiceDataStore(intent)
            voiceDataStore.updateData { cellSessionProto ->
                val newCellSessionDataBuilder = cellSessionProto.toBuilder()
                newCellSessionDataBuilder.callId = callID
                newCellSessionDataBuilder.callNumber =
                    intent.getStringExtra(CALL_NUMBER_EXTRA) ?: EMPTY
                newCellSessionDataBuilder.clientVersion = BuildConfig.VERSION_NAME

                val newNetworkIdentityDataBuilder = cellSessionProto.networkIdentity.toBuilder()
                newNetworkIdentityDataBuilder.mcc = MccAccess.getMCC(context)
                newNetworkIdentityDataBuilder.mnc = MncAccess.getMNC(context)
                newCellSessionDataBuilder.setNetworkIdentity(newNetworkIdentityDataBuilder)

                newCellSessionDataBuilder.build()
            }
        }
        return voiceDataStore
    }

    /**
     * abstract function that enables individual intent listeners to validate the received intent
     * @param intent: [Intent]
     */
    open fun isValidIntent(intent: Intent): Boolean {
        /**checking if intent null or empty and do nothing in this case*/
        if (!intent.hasExtra(CALL_ID_EXTRA) || TextUtils.isEmpty(intent.getStringExtra(CALL_ID_EXTRA))) {
            postAnalyticsEventForVoiceFailed(
                VOICE_CALL_ID_NULL,
                ELAnalyticActions.EL_DATA_COLLECTION_FAILED
            )
            return false
        }
        if (!intent.hasExtra(CALL_NUMBER_EXTRA) || intent.getStringExtra(CALL_NUMBER_EXTRA) == null) {
            postAnalyticsEventForVoiceFailed(
                VOICE_CALL_NUMBER_NULL,
                ELAnalyticActions.EL_DATA_COLLECTION_FAILED
            )
            return false
        }
        return true
    }

    /**
     * Returns [EventInfo] object based on the intent passed.
     * This function extracts the extras from the intent using [getCellInfo] and location.
     * Then it assigns it to the respective attributes.
     *
     * @param intent: [Intent]
     * @return [EventInfo] generated object
     */
    fun getEventInfo(intent: Intent): EventInfo {
        val cellInfo = CellInfoDataProcessor(context).getCellInfo(intent)
        val location = VoiceLocationDataProcessor(context).fetchLocationDataSync()
        return EventInfo(cellInfo, location)
    }


    /**
     * Returns [BaseVoiceData] object based on the intent passed by extracting the extras from the intent and
     * assigning to it respective attributes
     * @param intent: [Intent]
     * @return [BaseVoiceData] generated object
     */
    fun getBaseVoiceData(intent: Intent): BaseVoiceData {

        return BaseVoiceData(
            intent.getStringExtra(CALL_ID_EXTRA).toString(),
            UUID.randomUUID().toString()
        )
    }


    /**
     * This function is used to post new event to analytics manager
     * @param status-checks the status of cms config
     * @param payload-stores the status code based on api status
     */
    fun postAnalyticsEventForVoiceFailed(payload: String, status: ELAnalyticActions) {
        val analyticsEvent = ELAnalyticsEvent(
            moduleName = ELModulesEnum.VOICE,
            action = status,
            payload = payload
        )
        analyticsEvent.timeStamp = System.currentTimeMillis()

        val postAnalyticsTicket = PostTicket(analyticsEvent)
        RxBus.instance.post(postAnalyticsTicket)
    }

}