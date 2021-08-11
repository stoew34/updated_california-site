package com.tmobile.mytmobile.echolocate.voice.dataprocessor

import android.content.Context
import android.content.Intent
import com.tmobile.echolocate.CallSessionProto
import com.tmobile.mytmobile.echolocate.voice.model.RadioHandoverData

/**
 * Capture intent data when OS broadcast diagandroid.phone.VoiceRadioBearerHandoverState about the radio handover
 * data everytime an ongoing call transfers or handovers from one channel connected to the core network to another channel.
 * Message will only be broadcasted during an (CS/PS) active voice call session.
 */
class RadioHandOverStateProcessor(context: Context) : BaseIntentProcessor(context) {

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
            val newRadioHandoverDataBuilder =
                CallSessionProto.DeviceIntents.RadioHandoverData.newBuilder()

            newRadioHandoverDataBuilder.handoverState =
                intent.getStringExtra(HAND_OVER_STATE_EXTRA) ?: ""
            newRadioHandoverDataBuilder.oemTimestamp =
                intent.getStringExtra(OEM_TIMESTAMP_EXTRA) ?: eventTimeStamp.toString()
            newRadioHandoverDataBuilder.eventTimestamp = eventTimeStamp.toString()

            val newEventInfoBuilder = CallSessionProto.DeviceIntents.EventInfo.newBuilder()
            newEventInfoBuilder.setCellInfo(CellInfoDataProcessor(context).getCellInfoBuilder(intent))
            newEventInfoBuilder.setLocationData(VoiceLocationDataProcessor(context).fetchLocationDataBuilderSync())
            newRadioHandoverDataBuilder.setEventInfo(newEventInfoBuilder)


            val addRadioHandoverData =
                deviceIntents.toBuilder().addRadioHandoverData(newRadioHandoverDataBuilder)

            cellSessionProto.toBuilder().setDeviceIntents(addRadioHandoverData).build()
        }

        return true
    }


}