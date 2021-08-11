package com.tmobile.mytmobile.echolocate.voice.dataprocessor

import android.content.Context
import android.content.Intent
import com.tmobile.echolocate.CallSessionProto
import com.tmobile.mytmobile.echolocate.voice.model.CallSettingData

/**
 * Handler class that gets called when the app receives diagandroid.phone.CallSetting
 * broadcast. The intent carries data of the call settings such as, Voice over LTE setting
 * Status of Wi-Fi call state and WFCPreference for intent set
 * to WIFIONLY | WIFIPREFFERED | CELLULAREPREFERRED | NA
 * oemtimestamp, event timestamp,call id,event info and location detials
 */
class CallSettingProcessor(context: Context) : BaseIntentProcessor(context) {

    /**
     * This method is called when the intent is received, to handle each voice action individually
     * which gets the extras from the intent and generated object specific to the listener
     *
     * @param intent : [Intent] intent received from the broadcast
     * @param eventTimeStamp The timestamp at which the intent was received by the application.
     * @return [CallSettingData] object after converting the intent to CallSettingData
     */
    override suspend fun processIntent(intent: Intent, eventTimeStamp: Long): Boolean {

        val voiceDataStore = getVoiceDataStore(intent)
        voiceDataStore.updateData { cellSessionProto ->
            val deviceIntents = cellSessionProto.deviceIntents
            val newCallSettingDataBuilder =
                CallSessionProto.DeviceIntents.CallSettingData.newBuilder()

            newCallSettingDataBuilder.volteStatus =
                intent.getStringExtra(CALL_SETTING_VO_LTE_EXTRA) ?: ""
            newCallSettingDataBuilder.wfcStatus = intent.getStringExtra(CALL_SETTING_WFC_EXTRA) ?: ""
            newCallSettingDataBuilder.wfcPreference = intent.getStringExtra(
                CALL_SETTING_WFC_PREFERENCE_EXTRA
            ) ?: ""
            newCallSettingDataBuilder.oemTimestamp =
                intent.getStringExtra(OEM_TIMESTAMP_EXTRA) ?: eventTimeStamp.toString()
            newCallSettingDataBuilder.eventTimestamp = eventTimeStamp.toString()

            val newEventInfoBuilder = CallSessionProto.DeviceIntents.EventInfo.newBuilder()
            newEventInfoBuilder.setCellInfo(CellInfoDataProcessor(context).getCellInfoBuilder(intent))
            newEventInfoBuilder.setLocationData(VoiceLocationDataProcessor(context).fetchLocationDataBuilderSync())
            newCallSettingDataBuilder.setEventInfo(newEventInfoBuilder)


            val addCallSettingData =
                deviceIntents.toBuilder().addCallSettingData(newCallSettingDataBuilder)
            cellSessionProto.toBuilder().setDeviceIntents(addCallSettingData).build()
        }

        return true
    }

}