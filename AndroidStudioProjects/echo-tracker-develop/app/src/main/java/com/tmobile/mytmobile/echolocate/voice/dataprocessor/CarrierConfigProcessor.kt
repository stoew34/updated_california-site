package com.tmobile.mytmobile.echolocate.voice.dataprocessor

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.tmobile.echolocate.CallSessionProto
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticActions
import com.tmobile.mytmobile.echolocate.voice.model.CarrierConfigData


/**
 * OEM shall broadcast an "diagandroid.phone.carrierConfig" message only when
 * the device is ATTEMPTING to initiate a voice callsession, or has an INCOMING voice call.
 * This Intent would contain String Extras and Map Extras
 */
class CarrierConfigProcessor(context: Context) : BaseIntentProcessor(context) {

    /**
     * This method is called when the intent is received
     * @param intent : [Intent] intent received from the broad cast
     *
     * @param eventTimeStamp The timestamp at which the intent was received by the application.
     *
     * @return [CarrierConfigData] object after converting the intent to CarrierConfigData
     */
    override suspend fun processIntent(intent: Intent, eventTimeStamp: Long): Boolean {

        val voiceDataStore = getVoiceDataStore(intent)
        voiceDataStore.updateData { cellSessionProto ->
            val deviceIntents = cellSessionProto.deviceIntents
            val newCarrierConfigDataBuilder =
                CallSessionProto.DeviceIntents.CarrierConfigData.newBuilder()

            newCarrierConfigDataBuilder.carrierConfigVersion = intent.getStringExtra(
                CARRIER_VOICE_CONFIG
            ) ?: ""
            newCarrierConfigDataBuilder.carrierVoWiFiConfig = intent.getStringExtra(
                CARRIER_VO_WIFI_CONFIG
            ) ?: ""

            val carrier5GBandConfig =
                if (intent.hasExtra(CARRIER_SA_5G_BAND_CONFIG) && (intent.getSerializableExtra(
                        CARRIER_SA_5G_BAND_CONFIG
                    ) is HashMap<*, *>)
                ) (intent.getSerializableExtra(CARRIER_SA_5G_BAND_CONFIG) as HashMap<*, *>) else emptyMap<Any, Any>()

            if (carrier5GBandConfig.isNotEmpty()) {
                for ((key, value) in carrier5GBandConfig) {
                    val newBandConfigBuilder =
                        CallSessionProto.DeviceIntents.CarrierConfigData.BandConfig.newBuilder()
                    newBandConfigBuilder.key = key.toString()
                    newBandConfigBuilder.value = value.toString()
                    newCarrierConfigDataBuilder.addBandConfig(newBandConfigBuilder)
                }
            }
            newCarrierConfigDataBuilder.eventTimestamp = eventTimeStamp.toString()

            val carrierConfigData =
                deviceIntents.toBuilder().setCarrierConfigData(newCarrierConfigDataBuilder)

            cellSessionProto.toBuilder().setDeviceIntents(carrierConfigData).build()
        }

        return true
    }

    /**
     * To validate the received intent if it has call id only
     * @param intent: [Intent]
     */
    override fun isValidIntent(intent: Intent): Boolean {
        /**checking if intent null or empty and do nothing in this case*/
        if (!intent.hasExtra(CALL_ID_EXTRA) || TextUtils.isEmpty(intent.getStringExtra(CALL_ID_EXTRA))) {
            postAnalyticsEventForVoiceFailed(
                VOICE_CALL_ID_NULL,
                ELAnalyticActions.EL_DATA_COLLECTION_FAILED
            )
            return false
        }
        return true
    }
}