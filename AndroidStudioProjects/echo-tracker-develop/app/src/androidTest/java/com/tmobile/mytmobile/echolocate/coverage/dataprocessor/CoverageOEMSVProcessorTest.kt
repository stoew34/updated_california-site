package com.tmobile.mytmobile.echolocate.coverage.dataprocessor

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
import com.tmobile.mytmobile.echolocate.coverage.delegates.TriggerSource
import com.tmobile.mytmobile.echolocate.coverage.model.BaseCoverageData
import com.tmobile.mytmobile.echolocate.coverage.reportprocessor.CoverageDataStatus
import com.tmobile.mytmobile.echolocate.coverage.utils.CoverageConstants
import com.tmobile.mytmobile.echolocate.coverage.utils.CoverageOEMSoftwareVersionCollector
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import io.mockk.every
import io.mockk.mockkClass
import org.junit.*
import org.junit.runner.RunWith
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
@RequiresApi(Build.VERSION_CODES.O)
@TargetApi(Build.VERSION_CODES.O)
class CoverageOEMSVProcessorTest {

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

    @Test
    fun testCoverageOEMSVProcessor() {

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

        val coverageOEMSV = OEMSV(
            "9",
            "PKQ1.181105.001",
            "G710TM20f",
            "MPSS.AT.4.0.c2.9-00061-SDM845_GEN_PACK-1.198357.15.211944.2",
            "09"
        )
        val oemSoftwareVersionCollector = mockkClass(CoverageOEMSoftwareVersionCollector::class)
        every {
            oemSoftwareVersionCollector.getOEMSoftwareVersion(context)
        } returns coverageOEMSV

        val processor = CoverageOEMSVProcessor(context)
        processor.oemsvCollector = oemSoftwareVersionCollector

        processor.processCoverageData(baseCovData)
        latch.await(1, TimeUnit.SECONDS)

        val coverageOEMSVEntity =
            coverageDao.getCoverageOEMSVEntityBySessionID(sessionId)
        Assert.assertNotNull(coverageOEMSVEntity)
    }
}