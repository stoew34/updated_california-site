package com.tmobile.mytmobile.echolocate.nr5g.sa5g.dataprocessor

import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.nr5g.core.oemdata.Sa5gDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.EchoLocateSa5gDatabase
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.dao.Sa5gDao
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity.BaseEchoLocateSa5gEntity
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.model.BaseSa5gData
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.model.BaseSa5gMetricsData
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.model.Sa5gUplinkCarrierLogs
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import org.junit.*
import java.io.IOException
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class Sa5gUplinkCarrierLogsProcessorTest {
    private lateinit var sa5gDao: Sa5gDao
    private lateinit var db: EchoLocateSa5gDatabase
    private var context: Context? = null

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context!!, EchoLocateSa5gDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        sa5gDao = EchoLocateSa5gDatabase.getEchoLocateSa5gDatabase(context!!).sa5gDao()
    }


    @Test
    public fun testSa5gUplinkCarrierLogsProcessorTest() {
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

        //Sa5gUplinkCarrierLogsEntity
        val sa5gUplinkCarrierLogs = Sa5gUplinkCarrierLogs(
            "techType", "bandNumber", "afcn",
            "5g", "yes"
        )

        val sa5gUplinkCarrierLogsList : MutableList<Sa5gUplinkCarrierLogs> = mutableListOf()
        sa5gUplinkCarrierLogsList.add(sa5gUplinkCarrierLogs)

        val sa5gUplinkCarrierLogsProcessor = Sa5gUplinkCarrierLogsProcessor(context!!)


        val baseSa5gMetricsData = BaseSa5gMetricsData(
                sa5gUplinkCarrierLogsList,
            "",
            Sa5gDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION,
            sessionId
        )
        val baseSa5gData = BaseSa5gData(sessionId, UUID.randomUUID().toString())

        sa5gUplinkCarrierLogsProcessor.processSa5gMetricsData(baseSa5gMetricsData, baseSa5gData)
        latch.await(1, TimeUnit.SECONDS)

        val sa5gUplinkCarrierLogsEntity2 = sa5gDao.getSa5gUplinkCarrierLogsEntity(sessionId)
        latch.await(1, TimeUnit.SECONDS)
        Assert.assertNotNull(sa5gUplinkCarrierLogsEntity2)
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
        db.clearAllTables()
    }
}