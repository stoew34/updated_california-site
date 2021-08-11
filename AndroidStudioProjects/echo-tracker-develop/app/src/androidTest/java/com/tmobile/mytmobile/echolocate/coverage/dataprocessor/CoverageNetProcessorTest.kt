package com.tmobile.mytmobile.echolocate.coverage.dataprocessor

import android.content.Context
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.tmobile.mytmobile.echolocate.TestActivity
import com.tmobile.mytmobile.echolocate.coverage.database.EchoLocateCoverageDatabase
import com.tmobile.mytmobile.echolocate.coverage.database.dao.CoverageDao
import com.tmobile.mytmobile.echolocate.coverage.database.entity.BaseEchoLocateCoverageEntity
import com.tmobile.mytmobile.echolocate.coverage.delegates.TriggerSource
import com.tmobile.mytmobile.echolocate.coverage.model.BaseCoverageData
import com.tmobile.mytmobile.echolocate.coverage.model.CoverageConnectedWifiStatus
import com.tmobile.mytmobile.echolocate.coverage.reportprocessor.CoverageDataStatus
import com.tmobile.mytmobile.echolocate.coverage.utils.CoverageConstants
import com.tmobile.mytmobile.echolocate.coverage.utils.CoverageNetworkDataCollector
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import io.mockk.every
import io.mockk.mockkClass
import kotlinx.serialization.ImplicitReflectionSerializer
import org.junit.*
import org.junit.runner.RunWith
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class CoverageNetProcessorTest {

    private lateinit var context: Context
    private lateinit var coverageDao: CoverageDao
    private lateinit var db: EchoLocateCoverageDatabase
    private lateinit var sessionId: String

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
        coverageDao =
            EchoLocateCoverageDatabase.getEchoLocateCoverageDatabase(context).coverageDao()
        sessionId = UUID.randomUUID().toString()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @ImplicitReflectionSerializer
    @Test
    fun testCoverageNetProcessorTest() {

        val baseEchoLocateCoverageEntity =
            BaseEchoLocateCoverageEntity(
                TriggerSource.SCREEN_ACTIVITY.name,
                CoverageDataStatus.STATUS_RAW,
                EchoLocateDateUtils.getTriggerTimeStamp(),
                CoverageConstants.SCHEMA_VERSION,
                sessionId
            )
        coverageDao.insertBaseEchoLocateCoverageEntity(baseEchoLocateCoverageEntity)

        val latch = CountDownLatch(1)
        val baseCovData = BaseCoverageData(sessionId, UUID.randomUUID().toString())

        val coverageConnectedWifiStatus = CoverageConnectedWifiStatus(
            "WIFI_STATE_ENABLED",
            "00:19:92:50:ba:21",
            "bssLoad",
            "[WPA2-PSK-CCMP][ESS]",
            "0",
            "0",
            "",
            "mode",
            "2462",
            "-78",
            "tmobile",
            "tmobile",
            "tmobile",
            "tmobile",
            "2018-05-16T16:14:10.456-0700"
        )
        val coverageNetworkDataCollector = mockkClass(CoverageNetworkDataCollector::class)
        every {
            coverageNetworkDataCollector.getConnectedWifiStatus(context)
        } returns coverageConnectedWifiStatus
        every {
            coverageNetworkDataCollector.getActiveNetwork(context)
        } returns "TRANSPORT_WIFI"
        every {
            coverageNetworkDataCollector.checkIsRoaming(context)
        } returns false

        val processor = CoverageNetProcessor(context)
        processor.networkDataCollector = coverageNetworkDataCollector

        processor.processCoverageData(baseCovData)
        latch.await(1, TimeUnit.SECONDS)

        val coverageConnectedWifiStatusEntity2 =
            coverageDao.getCoverageConnectedWifiStatusEntityEntityBySessionID(sessionId)
        Assert.assertNotNull(coverageConnectedWifiStatusEntity2)

        val coverageNetEntity2 = coverageDao.getCoverageNetEntityBySessionID(sessionId)
        Assert.assertNotNull(coverageNetEntity2)

    }
}