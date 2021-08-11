package com.tmobile.mytmobile.echolocate.nr5g.sa5g.dataprocessor

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.nr5g.core.oemdata.Sa5gDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.locationmanager.LocationManager
import com.tmobile.mytmobile.echolocate.location.model.LocationResponseParameters
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.EchoLocateSa5gDatabase
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.dao.Sa5gDao
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity.BaseEchoLocateSa5gEntity
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity.Sa5gLocationEntity
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.model.BaseSa5gData
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.model.BaseSa5gMetricsData
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import io.mockk.every
import io.mockk.mockkClass
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import kotlinx.serialization.ImplicitReflectionSerializer
import org.junit.*
import java.io.IOException
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class Sa5gLocationProcessorTest {

    private lateinit var sa5gDao: Sa5gDao
    private lateinit var db: EchoLocateSa5gDatabase
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, EchoLocateSa5gDatabase::class.java)
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
        sa5gDao = EchoLocateSa5gDatabase.getEchoLocateSa5gDatabase(context).sa5gDao()
    }

    @ImplicitReflectionSerializer
    @Test
    public suspend fun testSa5gLocationProcessor() {

        val locationSyncParams = LocationResponseParameters(
                10.0,
                0.0f,
                10.0,
                10.0,
                0.0f,
                "",
                10)

        val locationProvider = mockkClass(LocationManager::class)

        every {
            locationProvider.getLocationSync(any(), any())
        } returns locationSyncParams

        val createObserver = Observable.create(ObservableOnSubscribe<LocationResponseParameters> { emitter ->
            emitter.onNext(locationSyncParams)
            emitter.onComplete()
        })

        every {
              locationProvider.getLocationAsync(any())
        } returns createObserver

        val sa5gLocationProcessor = Sa5gLocationProcessor(context)
        sa5gLocationProcessor.locationManager = locationProvider

        val latch = CountDownLatch(1)
        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateSa5gEntity =
            BaseEchoLocateSa5gEntity(
                301,
                "",
                EchoLocateDateUtils.getTriggerTimeStamp(),
                sessionId
            )
        sa5gDao.insertBaseEchoLocateSa5gEntity(baseEchoLocateSa5gEntity)

        val sa5gLocationEntity = Sa5gLocationEntity(
            10.0,
            0.0f,
            10.0,
            10.0,
            0.0f,
            EchoLocateDateUtils.getTriggerTimeStamp(),
            10
        )

        val baseSa5gMetricsData = BaseSa5gMetricsData(
            sa5gLocationEntity,
            "",
            Sa5gDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION,
            sessionId
        )
        val baseSa5gData = BaseSa5gData(sessionId, UUID.randomUUID().toString())

        sa5gLocationProcessor.processSa5gMetricsData(baseSa5gMetricsData, baseSa5gData)
        latch.await(1, TimeUnit.SECONDS)

        val sa5gLocationEntity2 = sa5gDao.getSa5gLocationEntity(sessionId)
        latch.await(1, TimeUnit.SECONDS)
        Assert.assertNotNull(sa5gLocationEntity2)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
        db.clearAllTables()
    }
}
