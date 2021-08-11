//package com.tmobile.mytmobile.echolocate.lte.location
//
//import android.content.Context
//import android.location.Location
//import android.location.LocationManager
//import android.location.LocationProvider
//import android.os.Build
//import android.os.SystemClock
//import androidx.test.core.app.ApplicationProvider.getApplicationContext
//import androidx.test.platform.app.InstrumentationRegistry
//import androidx.test.rule.ActivityTestRule
//import androidx.test.rule.GrantPermissionRule
//import androidx.work.Configuration
//import androidx.work.testing.SynchronousExecutor
//import androidx.work.testing.WorkManagerTestInitHelper
//import com.tmobile.mytmobile.echolocate.TestActivity
//import com.tmobile.mytmobile.echolocate.scheduler.events.SchedulerResponseEvent
//import com.tmobile.mytmobile.echolocate.lte.utils.LteSharedPrefs
//import com.tmobile.mytmobile.echolocate.reporting.utils.ReportingModuleSharedPrefs
//import io.reactivex.observers.TestObserver
//import org.junit.Before
//import org.junit.Rule
//import java.util.concurrent.Executor
//import java.util.concurrent.Executors
//
//
//class LocationLteSingleSessionReportProcessorTest {
//
//    private val testObserver = TestObserver<SchedulerResponseEvent>()
//    var context = InstrumentationRegistry.getInstrumentation().targetContext
//    private lateinit var executor: Executor
//
//    @Rule @JvmField
//    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)
//
//    @get:Rule
//    private val activityRule = ActivityTestRule(
//        TestActivity::class.java, false, false
//    )
//
//    @Before
//    fun setUp() {
//        context = InstrumentationRegistry.getInstrumentation().targetContext
//        LteSharedPrefs.init(context)
//        ReportingModuleSharedPrefs.init(context)
//        activityRule.launchActivity(null)
//        ReportingModuleSharedPrefs.interval = 0
//        ReportingModuleSharedPrefs.scheduledWorkId = 0L
//        val config = Configuration.Builder()
//            // Use a SynchronousExecutor here to make it easier to write tests
//            .setExecutor(SynchronousExecutor())
//            .build()
//
//
//        // Initialize WorkManager for instrumentation tests.
//        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
//        executor = Executors.newSingleThreadExecutor()
//    }
//
//
//    fun setMock(latitude: Double, longitude: Double, accuracy: Float) {
//
//        val locMgr =
//            getApplicationContext<Context>().getSystemService(Context.LOCATION_SERVICE) as LocationManager
//
//        locMgr.addTestProvider(
//            LocationManager.GPS_PROVIDER,
//            "requiresNetwork" === "",
//            "requiresSatellite" === "",
//            "requiresCell" === "",
//            "hasMonetaryCost" === "",
//            "supportsAltitude" === "",
//            "supportsSpeed" === "",
//            "supportsBearing" === "",
//            android.location.Criteria.POWER_LOW,
//            android.location.Criteria.ACCURACY_FINE
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
//            LocationManager.GPS_PROVIDER,
//            LocationProvider.AVAILABLE,
//            null, System.currentTimeMillis()
//        )
//        locMgr.setTestProviderLocation(LocationManager.GPS_PROVIDER, newLocation)
//    }
//
////    @Test
////    @kotlinx.serialization.ImplicitReflectionSerializer
////    fun getLocationSyncNewTest() {
////        setMock(10.00, 10.00, 2F)
////
////        val uniqueId = UUID.randomUUID().toString()
////        val sessionId = UUID.randomUUID().toString()
////
////        val locationDataProcessor = LteLocationDataProcessor(getApplicationContext())
////        val locationResponseParamters1 =
////            LocationResponseParameters(0.0, 0F, 10.00, 10.00, 0F, "1234", 0)
////        val testResquestParameters = LocationRequestParameters(0L, 0L, 100)
////        val locationResponseParameters2 =
////            locationDataProcessor.fetchLocationDataSync(testResquestParameters, BaseLteData(sessionId, uniqueId))
////
////        TestCase.assertEquals(
////            locationResponseParamters1.latitude,
////            locationResponseParameters2.latitude
////        )
////        TestCase.assertEquals(
////            locationResponseParamters1.longitude,
////            locationResponseParameters2.longitude
////        )
////
////        val lteLocationEntity = locationDataProcessor.fetchLocationDataSync(
////            locationResponseParameters2,
////            BaseLteData(sessionId, uniqueId)
////        )
////        TestCase.assertEquals(lteLocationEntity.latitude, locationResponseParameters2.latitude)
////        TestCase.assertEquals(lteLocationEntity.longitude, locationResponseParameters2.longitude)
////        TestCase.assertEquals(lteLocationEntity.uniqueId, uniqueId)
////    }
//}