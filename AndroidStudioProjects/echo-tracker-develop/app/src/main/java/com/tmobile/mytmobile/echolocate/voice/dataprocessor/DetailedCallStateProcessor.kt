package com.tmobile.mytmobile.echolocate.voice.dataprocessor

import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import com.tmobile.echolocate.CallSessionProto
import com.tmobile.echolocate.CallStatusProto
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.voice.model.DetailedCallStateData
import com.tmobile.mytmobile.echolocate.voice.repository.VoiceRepository
import com.tmobile.mytmobile.echolocate.voice.utils.CallState

/**
 * Handler class that gets called when the app receives diagandroid.phone.detailedCallState
 * broadcast. The intent carries data of the call state such as call code, call state,
 * oemtimestamp, event timestamp,call id,event info and location detials
 */
class DetailedCallStateProcessor(context: Context) : BaseIntentProcessor(context) {

    companion object {
        internal const val TIMEOUT_TWENTY_SECONDS = 20 * DateUtils.SECOND_IN_MILLIS
    }

    /**
     * This method is called when the intent is received, to handle each voice action individually
     * which gets the extras from the intent and generated object specific to the listener
     * @param intent : [Intent] intent received from the broad cast
     *
     * @param eventTimeStamp The timestamp at which the intent was received by the application.
     *
     * @return [DetailedCallStateData] object after converting the intent to DetailedCallStateData
     */
    override suspend fun processIntent(intent: Intent, eventTimeStamp: Long): Boolean {

        val oemTimestamp = intent.getStringExtra(OEM_TIMESTAMP_EXTRA) ?: eventTimeStamp.toString()
        val callCode = intent.getStringExtra(CALL_CODE_EXTRA) ?: ""
        val callState = intent.getStringExtra(CALL_STATE_EXTRA) ?: ""


        val voiceDataStore = getVoiceDataStore(intent)
        voiceDataStore.updateData { cellSessionProto ->
            val deviceIntents = cellSessionProto.deviceIntents
            val newDetailedCallStateDataBuilder =
                CallSessionProto.DeviceIntents.DetailedCallStateData.newBuilder()

            newDetailedCallStateDataBuilder.callCode = callCode
            newDetailedCallStateDataBuilder.callState = callState
            newDetailedCallStateDataBuilder.oemTimestamp = oemTimestamp
            newDetailedCallStateDataBuilder.eventTimestamp = eventTimeStamp.toString()

            val newEventInfoBuilder = CallSessionProto.DeviceIntents.EventInfo.newBuilder()
            newEventInfoBuilder.setCellInfo(CellInfoDataProcessor(context).getCellInfoBuilder(intent))
            newEventInfoBuilder.setLocationData(VoiceLocationDataProcessor(context).fetchLocationDataBuilderSync())
            newDetailedCallStateDataBuilder.setEventInfo(newEventInfoBuilder)


            val addDetailedCallStateData =
                deviceIntents.toBuilder().addDetailedCallStateData(newDetailedCallStateDataBuilder)
            cellSessionProto.toBuilder().setDeviceIntents(addDetailedCallStateData).build()
        }

        if (callState == CallState.ENDED.name) {

            /**
             * Print locally
             */
            EchoLocateLog.eLogD("DS: Call ENDED")

            /**
             * Collection of calls & status is recorded. Sometime Call Ended intent is not received
             */
            VoiceRepository.gcallStatusDataStore?.updateData { callStatusProto: CallStatusProto ->
                var newCallDataBuilder = CallStatusProto.Call.newBuilder()
                newCallDataBuilder.callID = intent.getStringExtra(CALL_ID_EXTRA)
                newCallDataBuilder.status = CallStatusProto.Call.Status.ENDED
                callStatusProto.toBuilder().addCall(newCallDataBuilder).build()
            }

        }

        return true
    }

}

