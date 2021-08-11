//package com.tmobile.mytmobile.echolocate.voice.location
//
//import android.Manifest
//import android.content.Context
//import android.location.Location
//import android.location.LocationManager
//import android.location.LocationProvider
//import android.os.Build
//import android.os.SystemClock
//import androidx.test.core.app.ApplicationProvider.getApplicationContext
//import androidx.test.platform.app.InstrumentationRegistry
//import androidx.test.rule.GrantPermissionRule
//import com.tmobile.mytmobile.echolocate.location.model.LocationRequestParameters
//import com.tmobile.mytmobile.echolocate.location.model.LocationResponseParameters
//import com.tmobile.mytmobile.echolocate.voice.intentprocessor.VoiceLocationDataProcessor
//import com.tmobile.mytmobile.echolocate.voice.model.BaseVoiceData
//import junit.framework.TestCase.assertEquals
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//
//
//class VoiceLocationDataProcessorTest {
//    @Rule
//    @JvmField
//    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)
//
//    val context = InstrumentationRegistry.getInstrumentation().targetContext!!
//
//    @Before
//    fun before() {
//        InstrumentationRegistry.getInstrumentation().uiAutomation
//            .grantRuntimePermission(
//                context.packageName,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            )
//    }
//
//
//    private fun setMock(latitude: Double, longitude: Double, accuracy: Float) {
//
//        val locMgr = getApplicationContext<Context>().getSystemService(Context.LOCATION_SERVICE) as LocationManager
//
//        locMgr.addTestProvider(
//                LocationManager.GPS_PROVIDER,
//                "requiresNetwork" === "",
//                "requiresSatellite" === "",
//                "requiresCell" === "",
//                "hasMonetaryCost" === "",
//                "supportsAltitude" === "",
//                "supportsSpeed" === "",
//                "supportsBearing" === "",
//                android.location.Criteria.POWER_LOW,
//                android.location.Criteria.ACCURACY_FINE
//        )
//
//        val newLocation = Location(LocationManager.GPS_PROVIDER)
//
//        newLocation.latitude = latitude
//        newLocation.longitude = longitude
//        newLocation.accuracy = accuracy
//        newLocation.altitude = 0.0
//        newLocation.accuracy = 500F
//        newLocation.time = System.currentTimeMillis()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            newLocation.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
//        }
//        locMgr.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true)
//
//        locMgr.setTestProviderStatus(
//                LocationManager.GPS_PROVIDER,
//                LocationProvider.AVAILABLE,
//                null, System.currentTimeMillis()
//        )
//        locMgr.setTestProviderLocation(LocationManager.GPS_PROVIDER, newLocation)
//    }
//
//    @Test
//    @kotlinx.serialization.ImplicitReflectionSerializer
//    fun fusedGetLocationSyncTimeOutTest() {
//        setMock(10.00, 10.00, 2F)
//
//        val locationDataProcessor = VoiceLocationDataProcessor(getApplicationContext())
//        val locationResponseParamters1 = LocationResponseParameters(0.0, 0F, 10.00, 10.00, 0F, "1234", 0)
//        val testResquestParameters = LocationRequestParameters(0L, 0L, 100)
//        val locationResponseParameters2 = locationDataProcessor.fetchLocationDataSync(testResquestParameters)
//        assertEquals(locationResponseParamters1.latitude, locationResponseParameters2?.latitude)
//        assertEquals(locationResponseParamters1.longitude, locationResponseParameters2?.longitude)
//
//        val voiceLocationEntity = locationDataProcessor.getLocationEntityFromLocationData(locationResponseParameters2, BaseVoiceData("123", "123"))
//        assertEquals(voiceLocationEntity?.latitude, locationResponseParameters2?.latitude)
//        assertEquals(voiceLocationEntity?.longitude, locationResponseParameters2?.longitude)
//        assertEquals(voiceLocationEntity?.callId, "123")
//        assertEquals(voiceLocationEntity?.uniqueId, "123")
//    }
//}