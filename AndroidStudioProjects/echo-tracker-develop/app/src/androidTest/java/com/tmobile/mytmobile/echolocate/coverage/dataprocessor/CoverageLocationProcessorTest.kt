package com.tmobile.mytmobile.echolocate.coverage.dataprocessor

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.tmobile.mytmobile.echolocate.TestActivity
import com.tmobile.mytmobile.echolocate.coverage.database.EchoLocateCoverageDatabase
import com.tmobile.mytmobile.echolocate.coverage.database.dao.CoverageDao
import com.tmobile.mytmobile.echolocate.coverage.database.entity.BaseEchoLocateCoverageEntity
import com.tmobile.mytmobile.echolocate.coverage.model.BaseCoverageData
import com.tmobile.mytmobile.echolocate.coverage.reportprocessor.CoverageDataStatus
import com.tmobile.mytmobile.echolocate.locationmanager.LocationManager
import com.tmobile.mytmobile.echolocate.location.model.LocationResponseParameters
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils.Companion.getTriggerTimeStamp
import io.mockk.every
import io.mockk.mockkClass
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import kotlinx.serialization.ImplicitReflectionSerializer
import org.junit.*
import org.junit.runner.RunWith
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
@RequiresApi(Build.VERSION_CODES.O)
@TargetApi(Build.VERSION_CODES.O)
class CoverageLocationProcessorTest {

    private lateinit var context: Context
    private lateinit var coverageDao: CoverageDao
    private lateinit var db: EchoLocateCoverageDatabase

    @get:Rule
    private val activityRule = ActivityTestRule(
        TestActivity::class.java, false, false
    )

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, EchoLocateCoverageDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            InstrumentationRegistry.getInstrumentation().uiAutomation
                .grantRuntimePermission(
                    context.packageName,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
        } else {
            InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(
                "pm grant " + context.packageName
                        + " android.permission.ACCESS_FINE_LOCATION"
            )
        }
        coverageDao =
            EchoLocateCoverageDatabase.getEchoLocateCoverageDatabase(context).coverageDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @ImplicitReflectionSerializer
    @Test
    suspend fun testCoverageLocationProcessor() {
        val locationSyncParams = LocationResponseParameters(
            10.0,
            10F,
            10.0,
            10.0,
            10F,
            "timestamp",
            10L,
            10F,
            10F,
            10F,
            10F,
            "fused",
            10F,
            10F,
            "",
            100,
            "STILL"
        )

        val locationProvider = mockkClass(LocationManager::class)
        every {
            locationProvider.getLocationSync(any(), any())
        } returns locationSyncParams

        val observable =
            Observable.create<LocationResponseParameters> {
                    emitter: ObservableEmitter<LocationResponseParameters> ->
                locationSyncParams
            }
        every {
            locationProvider.getLocationAsync(any())
        } returns observable

        val processor = CoverageLocationProcessor(context)
        processor.locationManager = locationProvider

        val latch = CountDownLatch(1)
        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateCoverageEntity =
            BaseEchoLocateCoverageEntity(
                "100",
                CoverageDataStatus.STATUS_RAW,
                getTriggerTimeStamp(),
                "3",
                sessionId
            )
        coverageDao.insertBaseEchoLocateCoverageEntity(baseEchoLocateCoverageEntity)
//        coverageDao.insertCoverageLocationEntity(coverageLocationEntity1)
        val baseCoverageData = BaseCoverageData(sessionId, UUID.randomUUID().toString())

        processor.processCoverageData(baseCoverageData)
        latch.await(1, TimeUnit.SECONDS)

        val coverageLocationEntity2 = coverageDao.getCoverageLocationEntityBySessionID(sessionId)
        Assert.assertNotNull(coverageLocationEntity2)
    }
}