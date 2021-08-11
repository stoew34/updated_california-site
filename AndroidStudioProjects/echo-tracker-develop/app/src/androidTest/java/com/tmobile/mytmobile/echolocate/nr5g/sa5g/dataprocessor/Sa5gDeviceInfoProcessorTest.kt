package com.tmobile.mytmobile.echolocate.nr5g.sa5g.dataprocessor

import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.nr5g.core.oemdata.Sa5gDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.EchoLocateSa5gDatabase
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.dao.Sa5gDao
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity.BaseEchoLocateSa5gEntity
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity.Sa5gDeviceInfoEntity
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.model.BaseSa5gData
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.model.BaseSa5gMetricsData
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.reportprocessor.Sa5gReportProcessor
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class Sa5gDeviceInfoProcessorTest {

    private lateinit var sa5gDao: Sa5gDao
    private lateinit var db: EchoLocateSa5gDatabase
    private lateinit var context: Context
    lateinit var sa5gReportProcessor: Sa5gReportProcessor


    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, EchoLocateSa5gDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        sa5gDao = EchoLocateSa5gDatabase.getEchoLocateSa5gDatabase(context).sa5gDao()
        sa5gReportProcessor = Sa5gReportProcessor.getInstance(context)
    }


    @Test
    suspend fun testSa5gDeviceInfoProcessor() {
        val latch = CountDownLatch(1)
        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateNr5gEntity =
                BaseEchoLocateSa5gEntity(
                        301,
                        "",
                        EchoLocateDateUtils.getTriggerTimeStamp(),
                        sessionId
                )
        sa5gDao.insertBaseEchoLocateSa5gEntity(baseEchoLocateNr5gEntity)
        val nr5gDeviceInfoEntity1 = Sa5gDeviceInfoEntity(
                "imei",
                "imsi",
                "msisdn",
                "uuid",
                "testSessionID12",
                "modelCode",
                "oem"
        )
        val processor = Sa5gDeviceInfoProcessor(context)

//        deviceInfoDataCollector = mockkClass(DeviceInfoDataCollector::class)
//        processor.deviceInfoDataCollector = deviceInfoDataCollector
//        every {
//            deviceInfoDataCollector.getDeviceInformation(context)
//        } returns DeviceInfo("", "imei", "imsi", "", "")

        val baseNr5gMetricsData = BaseSa5gMetricsData(
                nr5gDeviceInfoEntity1,
                "",
                Sa5gDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION,
                sessionId
        )
        val baseNr5gData = BaseSa5gData(sessionId, UUID.randomUUID().toString())

        processor.processSa5gMetricsData(baseNr5gMetricsData, baseNr5gData)
        latch.await(1, TimeUnit.SECONDS)

        val nr5gDeviceInfoEntity2 = sa5gDao.getSa5gDeviceInfoEntity(sessionId)
        Assert.assertNotNull(nr5gDeviceInfoEntity2)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
        db.clearAllTables()
    }
}