package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.dataprocessor

import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.nr5g.core.oemdata.Nr5gBaseDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.lte.reportprocessor.LteDataStatus
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.EchoLocateNr5gDatabase
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.dao.Nr5gDao
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.BaseEchoLocateNr5gEntity
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.EndcLteLogEntity
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.BaseNr5gData
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.BaseNr5gMetricsData
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils.Companion.getTriggerTimeStamp
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class EndcLteLogProcessorTest {

    private lateinit var nr5gDao: Nr5gDao
    private lateinit var db: EchoLocateNr5gDatabase
    lateinit var context: Context

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, EchoLocateNr5gDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        nr5gDao = EchoLocateNr5gDatabase.getEchoLocateNr5gDatabase(context).nr5gDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testInsertEndcLteLogEntity() {

        val latch = CountDownLatch(1)
        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateNr5gEntity =
            BaseEchoLocateNr5gEntity(
                201,
                LteDataStatus.STATUS_RAW,
                getTriggerTimeStamp(),
                sessionId
            )

        nr5gDao.insertBaseEchoLocateNr5gEntity(baseEchoLocateNr5gEntity)
        val endcLteLogEntity1 = EndcLteLogEntity(
            getTriggerTimeStamp(),
            2,
            1,
            1,
            1,
            3
        )

        val baseNr5gMetricsData = BaseNr5gMetricsData(endcLteLogEntity1, "", Nr5gBaseDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION, "")
        val baseNr5gData = BaseNr5gData(sessionId, UUID.randomUUID().toString())

        Nsa5gEndcLteLogProcessor(context).processNr5gMetricsData(baseNr5gMetricsData, baseNr5gData)
        latch.await(1, TimeUnit.SECONDS)

        val endcLteLogEntity2 = nr5gDao.getEndcLteLogEntity(sessionId)
        latch.await(1, TimeUnit.SECONDS)
        Assert.assertNotNull(endcLteLogEntity2)
    }
}