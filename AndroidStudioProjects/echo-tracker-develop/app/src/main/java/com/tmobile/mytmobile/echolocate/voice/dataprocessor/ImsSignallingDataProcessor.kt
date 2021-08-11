package com.tmobile.mytmobile.echolocate.voice.dataprocessor

import android.content.Context
import android.content.Intent
import com.tmobile.echolocate.CallSessionProto
import com.tmobile.mytmobile.echolocate.voice.model.RadioHandoverData

/**
 * Handler class that gets called when the app receives diagandroid.phone.imsSignallingMessage
 * broadcast. It captures intent data about  detailed call state data every time an IMSSIP
 * message is delivered or sent by the device during an active packet switched (PS) call.
 * This intent will only be delieverd for VoLTE and Wi-Fi calls.
 */
class ImsSignallingDataProcessor(context: Context) : BaseIntentProcessor(context) {

    /**
     * This method is called when the intent is received, to handle each voice action individually
     * which gets the extras from the intent and generated object specific to the listener
     * @param intent : [Intent] intent received from the broad cast
     *
     * @param eventTimeStamp The timestamp at which the intent was received by the application.
     *
     * @return [RadioHandoverData] object after converting the intent to DetailedCallStateData
     */
    override suspend fun processIntent(intent: Intent, eventTimeStamp: Long): Boolean {


        val voiceDataStore = getVoiceDataStore(intent)
        voiceDataStore.updateData { cellSessionProto ->
            val deviceIntents = cellSessionProto.deviceIntents
            val newImsSignallingDataBuilder =
                CallSessionProto.DeviceIntents.ImsSignallingData.newBuilder()

            newImsSignallingDataBuilder.sipCallId = intent.getStringExtra(SIP_CALL_ID_EXTRA) ?: ""
            newImsSignallingDataBuilder.sipCseq = intent.getStringExtra(SIP_CSEQ_EXTRA) ?: ""
            newImsSignallingDataBuilder.sipLine1 = intent.getStringExtra(SIP_LINE1_EXTRA) ?: ""
            newImsSignallingDataBuilder.sipOrigin = intent.getStringExtra(SIP_ORIGIN_EXTRA) ?: ""
            newImsSignallingDataBuilder.sipReason = intent.getStringExtra(SIP_REASON_EXTRA) ?: ""
            newImsSignallingDataBuilder.sipSDP = intent.getStringExtra(SIP_SDP_EXTRA) ?: ""
            newImsSignallingDataBuilder.oemTimestamp =
                intent.getStringExtra(OEM_TIMESTAMP_EXTRA) ?: eventTimeStamp.toString()
            newImsSignallingDataBuilder.eventTimestamp = eventTimeStamp.toString()

            val newEventInfoBuilder = CallSessionProto.DeviceIntents.EventInfo.newBuilder()
            newEventInfoBuilder.setCellInfo(CellInfoDataProcessor(context).getCellInfoBuilder(intent))
            newEventInfoBuilder.setLocationData(VoiceLocationDataProcessor(context).fetchLocationDataBuilderSync())
            newImsSignallingDataBuilder.setEventInfo(newEventInfoBuilder)


            val addImsSignallingData =
                deviceIntents.toBuilder().addImsSignallingData(newImsSignallingDataBuilder)

            cellSessionProto.toBuilder().setDeviceIntents(addImsSignallingData).build()
        }

        return true
    }


}