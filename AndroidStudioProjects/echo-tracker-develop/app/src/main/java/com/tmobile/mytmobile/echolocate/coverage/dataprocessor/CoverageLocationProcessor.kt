package com.tmobile.mytmobile.echolocate.coverage.dataprocessor

/**
 *  This class captures the location details to correlate the coverage metrics/schema.
 */
import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabaseCorruptException
import android.text.format.DateUtils
import com.google.android.gms.location.LocationRequest
import com.tmobile.mytmobile.echolocate.coverage.database.entity.CoverageLocationEntity
import com.tmobile.mytmobile.echolocate.coverage.model.BaseCoverageData
import com.tmobile.mytmobile.echolocate.locationmanager.LocationManager
import com.tmobile.mytmobile.echolocate.location.model.LocationRequestParameters
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.utils.FirebaseUtils
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
//import kotlinx.serialization.ImplicitReflectionSerializer
import java.util.*
import java.util.concurrent.TimeUnit

class CoverageLocationProcessor(val context: Context) : BaseCoverageDataProcessor(context) {

    companion object {
        //internal const val TIMEOUT_FIFTEEN_SECONDS = 15 * DateUtils.SECOND_IN_MILLIS
        internal const val DEFAULT_LOCATION_UPDATE_INTERVAL_MS = 5 * DateUtils.SECOND_IN_MILLIS
        internal const val DEFAULT_LOCATION_FASTEST_UPDATE_INTERVAL_MS = DateUtils.SECOND_IN_MILLIS
        internal const val DEFAULT_LOCATION_ACCURACY =
            LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    }

     var locationManager = LocationManager.getInstance(context)
    private var fetchDisposable: Disposable? = null

//    @ImplicitReflectionSerializer
    override suspend fun processCoverageData(
        baseCoverageData: BaseCoverageData
    ): Disposable? {
        return fetchLocationDataAsync(
                LocationRequestParameters(
                        DEFAULT_LOCATION_UPDATE_INTERVAL_MS,
                        DEFAULT_LOCATION_FASTEST_UPDATE_INTERVAL_MS,
                        DEFAULT_LOCATION_ACCURACY
                ), baseCoverageData
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
        locationRequestParameters: LocationRequestParameters, baseCoverageData: BaseCoverageData
    ): Disposable? {
        val calendar = Calendar.getInstance()
        val timestamp =
            EchoLocateDateUtils.convertToShemaDateFormat(calendar.timeInMillis.toString())

        fetchLocationDataSync(locationRequestParameters, baseCoverageData)

        val locationAsyncParams =
            locationManager.getLocationAsync(locationRequestParameters)

        fetchDisposable = locationAsyncParams
            //.timeout(TIMEOUT_FIFTEEN_SECONDS, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({ response ->
                if (response != null) {
                    EchoLocateLog.eLogD("Diagnostic : Coverage Location: Async response received")
                    val coverageLocationEntity = CoverageLocationEntity(
                        latitude = response.latitude.toString(),
                        longitude = response.longitude.toString(),
                        accuracy = response.accuracy.toString(),
                        altitude = response.altitude.toString(),
                        bearing = response.bearing.toString(),
                        bearingAccuracy = response.bearingAccuracy.toString(),
                        activityType = response.type.toString(),
                        activityConfidence = response.confidence.toString(),
                        speed = response.speed.toString(),
                        provider = response.provider.toString(),
                        locationAge = response.locationAge.toString(),
                        speedAccuracyMetersPerSecond = response.speedAccuracyMetersPerSecond.toString(),
                        verticalAccuracyMeters = response.verticalAccuracyMeters.toString(),
                        locationStatus = response.locationStatus.toString(),
                        timestamp = timestamp
                    )
                    coverageLocationEntity.sessionId = baseCoverageData.sessionId
                    coverageLocationEntity.uniqueId = baseCoverageData.uniqueId

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            saveCoverageLocationEntityToDatabase(coverageLocationEntity)
                            EchoLocateLog.eLogD(
                                "Diagnostic : Coverage Location: success in getLocationAsync: data fetched and saved to db "
                            )
                        } catch (ex: SQLiteDatabaseCorruptException) {
                            EchoLocateLog.eLogE("CoverageLocationProcessor : fetchLocationDataAsync() :: Exception : $ex")
                            FirebaseUtils.logCrashToFirebase(
                                "Exception in BaseCoverageDataProcessor :: fetchLocationDataAsync()",
                                ex.localizedMessage,
                                "SQLiteDatabaseCorruptException"
                            )
                        }
                    }
                 //fetchDisposable?.dispose()
                }
            }, { error ->
                EchoLocateLog.eLogI(
                    "Diagnostic : Coverage Location: error in getLocationAsync: " + error.stackTrace
                )
            })
        return fetchDisposable
    }

    /**
     * Synchronous
     * fetch location data from location module at [LocationManager]
     * and save it to the database for the given sessionId and uniqueId.
     *
     * If the location is not available, empty string will be saved to the database.
     */
//    @ImplicitReflectionSerializer
    fun fetchLocationDataSync(
        locationRequestParameters: LocationRequestParameters, baseCoverageData: BaseCoverageData
    ) {
        val calendar = Calendar.getInstance()
        val timestamp =
            EchoLocateDateUtils.convertToShemaDateFormat(calendar.timeInMillis.toString())

        val locationSyncParams =
            locationManager.getLocationSync(locationRequestParameters, 5500)

        if (locationSyncParams != null) {
            val coverageLocationEntity = CoverageLocationEntity(
                latitude = locationSyncParams.latitude.toString(),
                longitude = locationSyncParams.longitude.toString(),
                accuracy = locationSyncParams.accuracy.toString(),
                altitude = locationSyncParams.altitude.toString(),
                bearing = locationSyncParams.bearing.toString(),
                bearingAccuracy = locationSyncParams.bearingAccuracy.toString(),
                activityType = locationSyncParams.type.toString(),
                activityConfidence = locationSyncParams.confidence.toString(),
                speed = locationSyncParams.speed.toString(),
                provider = locationSyncParams.provider.toString(),
                locationAge = locationSyncParams.locationAge.toString(),
                speedAccuracyMetersPerSecond = locationSyncParams.speedAccuracyMetersPerSecond.toString(),
                verticalAccuracyMeters = locationSyncParams.verticalAccuracyMeters.toString(),
                locationStatus = locationSyncParams.locationStatus.toString(),
                timestamp = timestamp
            )
            coverageLocationEntity.sessionId = baseCoverageData.sessionId
            coverageLocationEntity.uniqueId = baseCoverageData.uniqueId

            CoroutineScope(Dispatchers.IO).launch {
                saveCoverageLocationEntityToDatabase(coverageLocationEntity)
                EchoLocateLog.eLogD("Diagnostic : Coverage Location: success in fetchLocationDataSync: data fetched and saved to db ")

            }
        }
    }

    /**
     * saves the CoverageLocationEntity object to database
     * @param coverageLocationEntity: [CoverageLocationEntity] the object to save
     */
    private fun saveCoverageLocationEntityToDatabase(coverageLocationEntity: CoverageLocationEntity) {
        if (coverageRepository.isBaseEchoLocateCoverageEntityAvailable(coverageLocationEntity.sessionId)) {
            EchoLocateLog.eLogD("Diagnostic : BaseEchoLocateCoverageEntity is available for session ID: ${coverageLocationEntity.sessionId} ")
            coverageRepository.insertCoverageLocationEntity(coverageLocationEntity)
        } else {
            EchoLocateLog.eLogD("Diagnostic : BaseEchoLocateCoverageEntity is not available for session ID: ${coverageLocationEntity.sessionId} ")
        }
    }
}
