package com.tmobile.mytmobile.echolocate.playground.activities

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.tmobile.mytmobile.echolocate.R
import com.tmobile.mytmobile.echolocate.location.model.LocationRequestParameters
import com.tmobile.mytmobile.echolocate.location.model.LocationResponseParameters
import com.tmobile.mytmobile.echolocate.playground.viewmodel.LocationViewModel
//import kotlinx.serialization.ImplicitReflectionSerializer

/**
 * This Activity uses the location API's defined to display the location details on the playground
 */
class LocationActivity : AppCompatActivity() {
    /**
     * private variables
     */
    private val viewModel by lazy {
        ViewModelProviders.of(this).get(LocationViewModel::class.java)
    }
    private var gson: Gson = GsonBuilder().create()
    private var startLocationModule: Button? = null
    private var getLocationAsyncBtn: Button? = null
    private var getLocationSyncBtn: Button? = null
    private var getLocationUpdatesBtn: Button? = null
    private var stopLocationModuleBtn: Button? = null
    private var locationResponseText: TextView? = null
    private var locationAccuracySpinner: Spinner? = null
    private var updateIntervalSpinner: Spinner? = null
    private var fastestUpdateIntervalSpinner: Spinner? = null

//    @ImplicitReflectionSerializer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        initViews()
        setListenersForViews()
    }

    /**
     * Initialization section of the views used in the activity
     */
    fun initViews() {
        startLocationModule = findViewById(R.id.start_location_btn)
        getLocationAsyncBtn = findViewById(R.id.get_location_async_btn)
        getLocationSyncBtn = findViewById(R.id.get_location_sync_btn)
        getLocationUpdatesBtn = findViewById(R.id.get_location_updates_btn)
        stopLocationModuleBtn = findViewById(R.id.stop_location_module_btn)
        locationResponseText = findViewById(R.id.location_response_txt)
        locationAccuracySpinner = findViewById(R.id.location_accuracy_spinner)
        updateIntervalSpinner = findViewById(R.id.update_interval_spinner)
        fastestUpdateIntervalSpinner = findViewById(R.id.fastest_update_interval_spinner)

        locationResponseText?.movementMethod = ScrollingMovementMethod.getInstance()
    }

    /**
     * Sets listeners to the buttons
     */
//    @ImplicitReflectionSerializer
    fun setListenersForViews() {
        startLocationModule!!.setOnClickListener {
            startLocationModule()
        }

        getLocationAsyncBtn!!.setOnClickListener {
            addLocationASyncObserver()
        }

        getLocationSyncBtn!!.setOnClickListener {
            addLocationSyncObserver()
        }

        getLocationUpdatesBtn!!.setOnClickListener {
            addLocationUpdatesObserver()
        }

        stopLocationModuleBtn!!.setOnClickListener {
            stopLocationModule()
        }
    }

    /**
     * generates the initial request params
     * @return LocationRequestParams : generated request params
     */
    private fun getLocationRequestParams(): LocationRequestParameters {
        val locationAccuracy = locationAccuracySpinner!!.selectedItem
        val updateInterval = updateIntervalSpinner!!.selectedItem
        val fastestUpdateInterval = fastestUpdateIntervalSpinner!!.selectedItem
        return LocationRequestParameters(updateInterval.toString().toLong(), fastestUpdateInterval.toString().toLong(), locationAccuracy.toString().toInt())
    }

    /**
     * Adds observer to the locationSync api to observe location data
     */
//    @ImplicitReflectionSerializer
    fun addLocationSyncObserver() {
        viewModel.getLocationSync(this, getLocationRequestParams()).observe(this, Observer { locationResponseParams ->
            updateLocation(locationResponseParams)
        })
    }

    /**
     * Adds observer to the locationASync api to observe the location data
     */
//    @ImplicitReflectionSerializer
    private fun addLocationASyncObserver() {
        viewModel.getLocationAsync(this, getLocationRequestParams()).observe(this, Observer { locationResponseParams ->
            updateLocation(locationResponseParams)
        })
    }

    /**
     * Adds observer to the location updates api to observe the location data
     */
    private fun addLocationUpdatesObserver() {
        viewModel.getLocationUpdates(this, getLocationRequestParams()).observe(this, Observer { locationResponseParams ->
            updateLocation(locationResponseParams)
        })
    }

    private fun updateLocation(locationResponseParams: LocationResponseParameters?) {
        setLocationResponseTextVisibility()
        if (locationResponseParams == null) {
            locationResponseText!!.text = "Location Not available"
        } else {
            val locationResponse = gson.toJson(locationResponseParams)
            locationResponseText!!.text =
                if (!locationResponse.isNullOrEmpty()) beautifyLocationResponse(locationResponse) else ""
        }
    }

    /**
     * Stops the location module by location module API [FusedLocationProvider.stopLocationModule]
     */
    private fun stopLocationModule() {
        viewModel.stopLocationModule(this)
        setLocationResponseTextVisibility()
        locationResponseText!!.text = "Location Module Stopped"
    }

    /**
     * Starts the location module by location module API [FusedLocationProvider.initLocationModule]
     */
    private fun startLocationModule() {
        viewModel.startLocationModule(this)
    }

    /**
     * Formats the json to display in the activity
     */
    private fun beautifyLocationResponse(locationResponse: String): String {
        val parser = JsonParser()
        val json = parser.parse(locationResponse).asJsonObject
        val gson = GsonBuilder().setPrettyPrinting().create()
        return gson.toJson(json)
    }

    /**
     * sets the visibility of the response text
     */
    private fun setLocationResponseTextVisibility() {
        if (locationResponseText!!.visibility == View.GONE) {
            locationResponseText!!.visibility = View.VISIBLE
        }
    }
}