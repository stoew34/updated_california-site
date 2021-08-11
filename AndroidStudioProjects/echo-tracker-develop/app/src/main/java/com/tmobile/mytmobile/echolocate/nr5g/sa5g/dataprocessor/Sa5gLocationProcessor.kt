package com.tmobile.mytmobile.echolocate.nr5g.sa5g.dataprocessor

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateUtils
import com.google.android.gms.location.LocationRequest
import com.tmobile.mytmobile.echolocate.locationmanager.LocationManager
import com.tmobile.mytmobile.echolocate.location.model.LocationRequestParameters
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity.Sa5gLocationEntity
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.model.BaseSa5gData
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.model.BaseSa5gMetricsData
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.utils.Sa5gConstants
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
//import kotlinx.serialization.ImplicitReflectionSerializer
import java.util.*
import java.util.concurrent.TimeUnit

class Sa5gLocationProcessor(var context: Context) : Sa5gBaseDataProcessor(context) {

    companion object {
        internal const val TIMEOUT_FIFTEEN_SECONDS = 15 * DateUtils.SECOND_IN_MILLIS
        internal const val DEFAULT_LOCATION_UPDATE_INTERVAL_MS = 5 * DateUtils.SECOND_IN_MILLIS
        internal const val DEFAULT_LOCATION_FASTEST_UPDATE_INTERVAL_MS = DateUtils.SECOND_IN_MILLIS
        internal const val DEFAULT_LOCATION_ACCURACY =
            LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    }

     var locationManager = LocationManager.getInstance(context)
    private var fetchDisposable: Disposable? = null

    /**
     * Sets the expected size of the source received from data metrics
     * If define 0 - not from metrics
     * @return Int returns the size of the source
     */
    override fun getExpectedSourceSize(): Int {
        return Sa5gConstants.SA5G_LOCATION_EXPECTED_SIZE
    }

    /**
     *  This function processes the list, converts it to Sa5gDeviceInfoEntity and saves it in database
     *  @param baseSa5gMetricsData: total metrics data to be processed
     *  @param baseSa5gData: [BaseSa5gData]
     */
//    @ImplicitReflectionSerializer
    override suspend fun processSa5gMetricsData(
        baseSa5gMetricsData: BaseSa5gMetricsData,
        baseSa5gData: BaseSa5gData
    ): Disposable? {
       return fetchLocationDataAsync(
            LocationRequestParameters(
                DEFAULT_LOCATION_UPDATE_INTERVAL_MS,
                DEFAULT_LOCATION_FASTEST_UPDATE_INTERVAL_MS,
                DEFAULT_LOCATION_ACCURACY
            ), baseSa5gData
        )
    }

    /**
     * Asynchronous
     * fetch location data from location module at [FusedLocationProvider]
     * and save it to the database for the given sessionId and uniqueId.
     *
     * If the location is not available, empty string will be saved to the database.
     */
    @SuppressLint("CheckResult")
//    @ImplicitReflectionSerializer
    fun fetchLocationDataAsync(
        locationRequestParameters: LocationRequestParameters,
        baseSa5gData: BaseSa5gData
    ) : Disposable? {
        val calendar = Calendar.getInstance()
        val timestamp =
            EchoLocateDateUtils.convertToShemaDateFormat(calendar.timeInMillis.toString())

        fetchLocationDataSync(locationRequestParameters, baseSa5gData)

        val locationAsyncParams = locationManager.getLocationAsync(locationRequestParameters)

        fetchDisposable = locationAsyncParams
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({ response ->
                if (response != null) {
                    EchoLocateLog.eLogD("Diagnostic : Coverage Location: Async response received")
                    val sa5gLocationEntity = Sa5gLocationEntity(
                        response.altitude,
                        response.altitudePrecision,
                        response.latitude,
                        response.longitude,
                        response.precision,
                        timestamp,
                        response.locationAge
                    )
                    sa5gLocationEntity.sessionId = baseSa5gData.sessionId
                    sa5gLocationEntity.uniqueId = baseSa5gData.uniqueId

                    CoroutineScope(Dispatchers.IO).launch {
                        saveSa5gLocationToDatabase(sa5gLocationEntity)

                        EchoLocateLog.eLogD(
                            "Diagnostic : Sa5g Location: success in getLocationAsync: " +
                                    "data fetched and saved to db "
                        )
                    }
                    fetchDisposable?.dispose()

                }
            }, { error ->
                EchoLocateLog.eLogD(
                    "Diagnostic : Sa5g Location: error in getLocationAsync: " + error.stackTrace
                )
            })
        return fetchDisposable
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
        baseSa5gData: BaseSa5gData
    ) {
        val calendar = Calendar.getInstance()
        val timestamp =
            EchoLocateDateUtils.convertToShemaDateFormat(calendar.timeInMillis.toString())

        val locationSyncParams =
            locationManager.getLocationSync(locationRequestParameters, 5500)

        if (locationSyncParams != null) {
            val sa5gLocationEntity = Sa5gLocationEntity(
                locationSyncParams.altitude,
                locationSyncParams.altitudePrecision,
                locationSyncParams.latitude,
                locationSyncParams.longitude,
                locationSyncParams.precision,
                timestamp,
                locationSyncParams.locationAge
            )
            sa5gLocationEntity.sessionId = baseSa5gData.sessionId
            sa5gLocationEntity.uniqueId = baseSa5gData.uniqueId

            CoroutineScope(Dispatchers.IO).launch {
                saveSa5gLocationToDatabase(sa5gLocationEntity)

                EchoLocateLog.eLogD("Diagnostic : Sa5g Location: success in fetchLocationDataSync: data fetched and saved to db ")
            }
        }
    }

    /**
     * saves DeviceInfo object to database
     * @param sa5gLocationEntity: [Sa5gLocationEntity] the object to save
     */
    private fun saveSa5gLocationToDatabase(sa5gLocationEntity: Sa5gLocationEntity) {
        sa5gRepository.insertSa5gLocationEntity(sa5gLocationEntity)
    }
}