package com.tmobile.mytmobile.echolocate.playground.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.tmobile.mytmobile.echolocate.locationmanager.LocationManager
import com.tmobile.mytmobile.echolocate.location.model.LocationRequestParameters
import com.tmobile.mytmobile.echolocate.location.model.LocationResponseParameters
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
//import kotlinx.serialization.ImplicitReflectionSerializer

/**
 * View model class to handle all the location operations
 */
class LocationViewModel(application: Application) : AndroidViewModel(application) {

    var livedataParams: MutableLiveData<LocationResponseParameters> = MutableLiveData()


    /**
     *  Gets the location Asynchronoulsy by using the location API
     *  @param ctx: Context - context of the calling module
     *  @param locationRequestParams: LocationRequestParameters
     *  @return MutableLiveData<LocationResponseParameters> - returns the response as live data
     */
//    @ImplicitReflectionSerializer
    fun getLocationAsync(
        ctx: Context,
        locationRequestParams: LocationRequestParameters
    ): MutableLiveData<LocationResponseParameters> {
        val locationManager = LocationManager.getInstance(ctx)
        locationManager.getLocationAsync(locationRequestParams)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                val locationResponseParameters = response
                livedataParams.postValue(locationResponseParameters)
            }, { error ->
                EchoLocateLog.eLogE("error in getLocationAsync: " + error.stackTrace)
            })
        return livedataParams
    }

    /**
     *  Gets the location synchronoulsy by using the location API
     *  @param ctx: Context - context of the calling module
     *  @param locationRequestParams: LocationRequestParameters
     *  @return MutableLiveData<LocationResponseParameters> - returns the response as live data
     */
//    @ImplicitReflectionSerializer
    fun getLocationSync(
        ctx: Context,
        locationRequestParams: LocationRequestParameters
    ): MutableLiveData<LocationResponseParameters> {
        GlobalScope.launch(Dispatchers.Default) {
            val locationManager = LocationManager.getInstance(ctx)
            val locationResponseParams =
                locationManager.getLocationSync(locationRequestParams, 2000)

            livedataParams.postValue(locationResponseParams)
        }

        return livedataParams
    }

    /**
     *  Gets the location updates by using the location API
     *  @param ctx: Context - context of the calling module
     *  @param locationRequestParams: LocationRequestParameters
     *  @return MutableLiveData<LocationResponseParameters> - returns the response as live data
     */
    fun getLocationUpdates(
        ctx: Context,
        locationRequestParams: LocationRequestParameters
    ): MutableLiveData<LocationResponseParameters> {
        val locationManager = LocationManager.getInstance(ctx)
        locationManager.getLocationUpdates(locationRequestParams)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                livedataParams.postValue(response)
            }, { error ->
                EchoLocateLog.eLogE("error in getLocationUpdates: " + error.stackTrace)
            })
        return livedataParams
    }

    /**
     *  Stop the location module.
     *  @param ctx: Context - context of the calling module
     *
     */
    fun stopLocationModule(ctx: Context) {
        GlobalScope.launch(Dispatchers.Default) {
            LocationManager.getInstance(ctx).stopLocationModule()
        }
    }

    /**
     *  Start the location module.
     *  @param ctx: Context - context of the calling module
     *
     */
    fun startLocationModule(ctx: Context) {
        GlobalScope.launch(Dispatchers.Default) {
            LocationManager.getInstance(ctx).initLocationModule()
        }
    }
}