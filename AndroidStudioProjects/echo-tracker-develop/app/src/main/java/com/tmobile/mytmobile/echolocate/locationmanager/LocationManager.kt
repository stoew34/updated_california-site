package com.tmobile.mytmobile.echolocate.locationmanager

import android.content.Context
import com.tmobile.mytmobile.echolocate.location.ILocationProvider
import com.tmobile.mytmobile.echolocate.location.model.LocationRequestParameters
import com.tmobile.mytmobile.echolocate.location.model.LocationResponseParameters
import com.tmobile.mytmobile.echolocate.location.provider.FusedLocationProvider
import com.tmobile.mytmobile.echolocate.lte.delegates.YoutubeDelegate
import com.tmobile.mytmobile.echolocate.utils.SingletonHolder
import io.reactivex.Observable

/*
This class interacts with location library and provides the location methods
 */
class LocationManager(val context: Context) : ILocationProvider {


    companion object : SingletonHolder<LocationManager, Context>(::LocationManager)


    /**
     * Provides the instance of location provider from location library
     */
    val fusedLocationProvider = FusedLocationProvider.getInstance(context)


    override fun initLocationModule() {
        fusedLocationProvider.initLocationModule()
    }

    override fun isInitialized(): Boolean {
        return fusedLocationProvider.isInitialized()
    }

    override fun getLocationAsync(locationRequestParameters: LocationRequestParameters): Observable<LocationResponseParameters> {
        return fusedLocationProvider.getLocationAsync(locationRequestParameters)
    }

    override fun getLocationSync(
        locationRequestParameters: LocationRequestParameters,
        timeout: Long
    ): LocationResponseParameters? {
        return fusedLocationProvider.getLocationSync(locationRequestParameters, timeout)
    }

    override fun getLocationUpdates(locationRequestParameters: LocationRequestParameters): Observable<LocationResponseParameters> {
        return fusedLocationProvider.getLocationUpdates(locationRequestParameters)
    }

    override fun stopLocationModule() {
        fusedLocationProvider.stopLocationModule()
    }
}