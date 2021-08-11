package com.tmobile.mytmobile.echolocate.lte.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.locationmanager.LocationManager
import com.tmobile.mytmobile.echolocate.location.model.LocationRequestParameters
import com.tmobile.mytmobile.echolocate.lte.database.entity.LteLocationEntity
import com.tmobile.mytmobile.echolocate.lte.model.BaseLteData
import com.tmobile.mytmobile.echolocate.lte.model.LteMetricsData
import com.tmobile.mytmobile.echolocate.lte.utils.LteConstants
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils.Companion.convertToShemaDateFormat
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
//import kotlinx.serialization.ImplicitReflectionSerializer
import java.util.*

/**
 *  This class captures the location details to correlate the voice metrics and improvement needed.
 */
class LteLocationDataProcessor(val context: Context) : BaseLteDataProcessor(context) {

    private var lteLocationDisposable: Disposable? = null
    private val locationManager = LocationManager.getInstance(context)

    /**
     * Sets the expected size of the source received from data metrics
     * @return Int returns the size of the source
     */
    override fun getExpectedSourceSize(): Int {
        return LteConstants.LOCATION_EXPECTED_SIZE
    }

    /**
     *  This function processes Lte Location and return disposable
     *  @param lteMetricsData: LteMetricsData
     *  @param baseLteData: BaseLteData
     */
//    @ImplicitReflectionSerializer
    override suspend fun processLteMetricsData(
        lteMetricsData: LteMetricsData,
        baseLteData: BaseLteData
    ): Disposable? {
        lteLocationDisposable = Observable.just(
            saveLteLocationData(
                lteMetricsData,
                baseLteData
            )
        ).subscribeOn(
            Schedulers.io()
        ).subscribe()
        return lteLocationDisposable
    }

//    @ImplicitReflectionSerializer
    private fun saveLteLocationData(lteMetricsData: LteMetricsData, baseLteData: BaseLteData){
        fetchLocationDataSync(
            LocationRequestParameters(
                LocationRequestParameters.DEFAULT_LOCATION_UPDATE_INTERVAL_MS,
                LocationRequestParameters.DEFAULT_LOCATION_FASTEST_UPDATE_INTERVAL_MS,
                LocationRequestParameters.DEFAULT_LOCATION_ACCURACY
            ), baseLteData
        )
    }

    /**
     * fetch location data from location module at [FusedLocationProvider]
     */

//    @ImplicitReflectionSerializer
    fun fetchLocationDataSync(
        locationRequestParameters: LocationRequestParameters, baseLteData: BaseLteData
    ) {
        val calendar = Calendar.getInstance()

        val timestamp = convertToShemaDateFormat(calendar.timeInMillis.toString())
        val locationSyncParams =
            locationManager.getLocationSync(locationRequestParameters, 5500)

        val lteLocationEntity =
            if (locationSyncParams != null) {
                LteLocationEntity(
                    locationSyncParams.altitude,
                    locationSyncParams.altitudePrecision,
                    locationSyncParams.latitude,
                    locationSyncParams.longitude,
                    locationSyncParams.precision,
                    locationSyncParams.locationAge,
                    timestamp
                )
            } else {
                LteLocationEntity(
                    0.0,
                    0.0F,
                    0.0,
                    0.0,
                    0.0F,
                    0,
                    timestamp
                )
            }
        lteLocationEntity.sessionId = baseLteData.sessionId
        lteLocationEntity.uniqueId = baseLteData.uniqueId

        CoroutineScope(Dispatchers.IO).launch {
            saveLteLocationEntityToDatabase(lteLocationEntity)
        }
    }

    /**
     * saves the lteLocationEntity object to database
     * @param lteLocationEntity: [LteLocationEntity] the object to save
     */
    private fun saveLteLocationEntityToDatabase(lteLocationEntity: LteLocationEntity) {
        lteRepository.insertLteLocationEntity(lteLocationEntity)
    }
}