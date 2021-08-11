package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.dataprocessor

import android.content.Context
import com.tmobile.mytmobile.echolocate.locationmanager.LocationManager
import com.tmobile.mytmobile.echolocate.location.model.LocationRequestParameters
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.Nr5gLocationEntity
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.BaseNr5gData
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.BaseNr5gMetricsData
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.utils.Nsa5gConstants
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
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
class Nsa5gLocationDataProcessor(val context: Context) : Nsa5gBaseDataProcessor(context) {

    var locationManager = LocationManager.getInstance(context)
    private var nsa5gLocationDataDisposable: Disposable? = null

    /**
     * Sets the expected size of the source received from data metrics
     * If define 0 - not from metrics
     * @return Int returns the size of the source
     */
    override fun getExpectedSourceSize(): Int {
        return Nsa5gConstants.LOCATION_EXPECTED_SIZE
    }

    /**
     *  This function processes location data and return disposable
     *  @param baseNr5gMetricsData: BaseNr5gMetricsData
     *  @param baseNr5gData: BaseNr5gData
     */
//    @ImplicitReflectionSerializer
    override suspend fun processNr5gMetricsData(
        baseNr5gMetricsData: BaseNr5gMetricsData,
        baseNr5gData: BaseNr5gData
    ): Disposable? {
        nsa5gLocationDataDisposable =  Observable.just(saveNsa5gLocationData(baseNr5gMetricsData,
            baseNr5gData)).subscribeOn(
            Schedulers.io()).subscribe()
        return nsa5gLocationDataDisposable
    }

//    @ImplicitReflectionSerializer
    private fun saveNsa5gLocationData(baseNr5gMetricsData: BaseNr5gMetricsData, baseNr5gData: BaseNr5gData){
        fetchLocationDataSync(
            LocationRequestParameters(
                LocationRequestParameters.DEFAULT_LOCATION_UPDATE_INTERVAL_MS,
                LocationRequestParameters.DEFAULT_LOCATION_FASTEST_UPDATE_INTERVAL_MS,
                LocationRequestParameters.DEFAULT_LOCATION_ACCURACY
            ), baseNr5gData)
    }

    /**
     * fetch location data from location module at [com.tmobile.mytmobile.echolocate.location.LocationManager]
     * and save it to the database for the given sessionId and uniqueId.
     *
     * If the location is not available, nothing will be saved to the database.
     */
//    @ImplicitReflectionSerializer
    private fun fetchLocationDataSync(
        locationRequestParameters: LocationRequestParameters,
        baseNr5GData: BaseNr5gData
    ) {
        val calendar = Calendar.getInstance()
        val timestamp =
            EchoLocateDateUtils.convertToShemaDateFormat(calendar.timeInMillis.toString())
        val locationSyncParams =
            locationManager.getLocationSync(locationRequestParameters, 5500)

        if (locationSyncParams != null) {
            val nr5GLocationEntity = Nr5gLocationEntity(
                locationSyncParams.altitude,
                locationSyncParams.altitudePrecision,
                locationSyncParams.latitude,
                locationSyncParams.longitude,
                locationSyncParams.precision,
                timestamp,
                locationSyncParams.locationAge
            )
            nr5GLocationEntity.sessionId = baseNr5GData.sessionId
            nr5GLocationEntity.uniqueId = baseNr5GData.uniqueId

            CoroutineScope(Dispatchers.IO).launch {
                saveNr5GLocationEntityToDatabase(nr5GLocationEntity)
            }
        }
    }

    /**
     * saves the nr5GLocationEntity object to database
     * @param nr5GLocationEntity: [LocationEntity] the object to save
     */
    private fun saveNr5GLocationEntityToDatabase(nr5GLocationEntity: Nr5gLocationEntity) {
        nr5gRepository.insertNr5GLocationEntity(nr5GLocationEntity)
    }
}