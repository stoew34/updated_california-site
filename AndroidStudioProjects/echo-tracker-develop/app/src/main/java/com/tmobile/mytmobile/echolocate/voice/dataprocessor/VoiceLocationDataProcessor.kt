package com.tmobile.mytmobile.echolocate.voice.dataprocessor

import android.content.Context
import com.tmobile.echolocate.CallSessionProto
import com.tmobile.mytmobile.echolocate.location.model.LocationRequestParameters
import com.tmobile.mytmobile.echolocate.locationmanager.LocationManager
import com.tmobile.mytmobile.echolocate.voice.model.BaseVoiceData
import com.tmobile.mytmobile.echolocate.voice.model.LocationData
import com.tmobile.mytmobile.echolocate.voice.repository.database.entity.VoiceLocationEntity

//import kotlinx.serialization.ImplicitReflectionSerializer

/**
 *  This class captures the location details to correlate the voice metrics and improvement needed.
 */
class VoiceLocationDataProcessor(val context: Context) {

    private val locationManager = LocationManager.getInstance(context)

    /**
     * fetch location data from location module at
     * [com.tmobile.mytmobile.echolocate.location.LocationManager]
     */
//    @ImplicitReflectionSerializer
    fun fetchLocationDataSync(
        locationRequestParameters: LocationRequestParameters = LocationRequestParameters(
            LocationRequestParameters.DEFAULT_LOCATION_UPDATE_INTERVAL_MS,
            LocationRequestParameters.DEFAULT_LOCATION_FASTEST_UPDATE_INTERVAL_MS,
            LocationRequestParameters.DEFAULT_LOCATION_ACCURACY
        )
    ): LocationData? {
        val locationSyncParams =
            locationManager.getLocationSync(locationRequestParameters, 1000)
        return if (locationSyncParams != null) {
            LocationData(
                locationSyncParams.altitude,
                locationSyncParams.altitudePrecision,
                locationSyncParams.latitude,
                locationSyncParams.longitude,
                locationSyncParams.precision,
                locationSyncParams.locationAge
            )
        } else {
            LocationData()
        }
    }

    fun fetchLocationDataBuilderSync(): CallSessionProto.DeviceIntents.EventInfo.LocationData.Builder? {
        val locationRequestParameters = LocationRequestParameters(
            LocationRequestParameters.DEFAULT_LOCATION_UPDATE_INTERVAL_MS,
            LocationRequestParameters.DEFAULT_LOCATION_FASTEST_UPDATE_INTERVAL_MS,
            LocationRequestParameters.DEFAULT_LOCATION_ACCURACY
        )

        val locationSyncParams =
            locationManager.getLocationSync(locationRequestParameters, 1000)
        return if (locationSyncParams != null) {
            var newLocationDataOrBuilder =
                CallSessionProto.DeviceIntents.EventInfo.LocationData.newBuilder()
            locationSyncParams.altitude?.let { newLocationDataOrBuilder.setAltitude(it) }
            locationSyncParams.altitudePrecision?.let {
                newLocationDataOrBuilder.setAltitudePrecision(
                    it
                )
            }
            locationSyncParams.latitude?.let { newLocationDataOrBuilder.setLatitude(it) }
            locationSyncParams.longitude?.let { newLocationDataOrBuilder.setLongitude(it) }
            locationSyncParams.precision?.let { newLocationDataOrBuilder.setPrecision(it) }
            locationSyncParams.locationAge?.let { newLocationDataOrBuilder.setLocationAge(it) }

        } else {
            CallSessionProto.DeviceIntents.EventInfo.LocationData.newBuilder()
        }
    }

}