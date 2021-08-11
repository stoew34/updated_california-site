package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.dataprocessor

import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.nr5g.core.oemdata.Nr5gBaseDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.EchoLocateNr5gDatabase
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.dao.Nr5gDao
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.BaseEchoLocateNr5gEntity
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.BaseNr5gMetricsData
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.reportprocessor.Nsa5gDataStatus
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class Nr5gNetworkIdentityProcessorTest {

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

    private fun getTriggerTimeStamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        val triggerTimeStamp = sdf.format(Date())
        return triggerTimeStamp
    }

    @Test
    fun testInsertNetworkIdentityEntity(){
        val latch = CountDownLatch(1)
        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateNr5gEntity =
            BaseEchoLocateNr5gEntity(
                200,
                Nsa5gDataStatus.STATUS_RAW,
                getTriggerTimeStamp(),
                sessionId
            )

        nr5gDao.insertBaseEchoLocateNr5gEntity(baseEchoLocateNr5gEntity)

        val sourceList = mutableListOf<String>()
        sourceList.add(0, "1")
        sourceList.add(1,"310")
        sourceList.add(2, "260")
        sourceList.add(3,"11334")
        sourceList.add(4,"1")
        sourceList.add(5,"0")
        sourceList.add(6,"0")
        sourceList.add(7, "0")
        sourceList.add(8, "0")
        sourceList.add(9,"310")
        sourceList.add(10, "260")
        sourceList.add(11,"11334")
        sourceList.add(12,"1")
        sourceList.add(13,"0")

        Nsa5gNetworkIdentityProcessor(context).execute(
            BaseNr5gMetricsData(
                sourceList,
                baseEchoLocateNr5gEntity.triggerTimestamp,
                Nr5gBaseDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION,
                sessionId
            )
        )
        latch.await(1, TimeUnit.SECONDS)
        val n5gNetworkIdentityEntity = nr5gDao.getNr5gNetworkIdentityEntity(sessionId)
        Assert.assertNotNull(n5gNetworkIdentityEntity)
    }

    @Test
    fun testInsertNetworkIdentityEntityV1(){
        val latch = CountDownLatch(1)
        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateNr5gEntity =
            BaseEchoLocateNr5gEntity(
                200,
                Nsa5gDataStatus.STATUS_RAW,
                getTriggerTimeStamp(),
                sessionId
            )

        nr5gDao.insertBaseEchoLocateNr5gEntity(baseEchoLocateNr5gEntity)

        val sourceList = mutableListOf<String>()
        sourceList.add(0, "1")
        sourceList.add(1,"310")
        sourceList.add(2, "260")
        sourceList.add(3,"11334")
        sourceList.add(4,"1")
        sourceList.add(5,"0")
        sourceList.add(6,"0")
        sourceList.add(7, "0")
        sourceList.add(8, "0")
        sourceList.add(9,"310")
        sourceList.add(10, "260")


        Nsa5gNetworkIdentityProcessor(context).execute(
            BaseNr5gMetricsData(
                sourceList,
                baseEchoLocateNr5gEntity.triggerTimestamp,
                Nr5gBaseDataMetricsWrapper.ApiVersion.VERSION_1,
                sessionId
            )
        )
        latch.await(1, TimeUnit.SECONDS)
        val n5gNetworkIdentityEntity = nr5gDao.getNr5gNetworkIdentityEntity(sessionId)
        Assert.assertNotNull(n5gNetworkIdentityEntity)
    }

    @Test
    fun testInsertNetworkIdentityEntityV3(){
        val latch = CountDownLatch(1)
        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateNr5gEntity =
            BaseEchoLocateNr5gEntity(
                200,
                Nsa5gDataStatus.STATUS_RAW,
                getTriggerTimeStamp(),
                sessionId
            )

        nr5gDao.insertBaseEchoLocateNr5gEntity(baseEchoLocateNr5gEntity)

        val sourceList = mutableListOf<String>()
        sourceList.add(0, "1")
        sourceList.add(1,"310")
        sourceList.add(2, "260")
        sourceList.add(3,"11334")
        sourceList.add(4,"1")
        sourceList.add(5,"0")
        sourceList.add(6,"0")
        sourceList.add(7, "0")
        sourceList.add(8, "0")
        sourceList.add(9,"310")
        sourceList.add(10, "260")

        Nsa5gNetworkIdentityProcessor(context).execute(
            BaseNr5gMetricsData(
                sourceList,
                baseEchoLocateNr5gEntity.triggerTimestamp,
                Nr5gBaseDataMetricsWrapper.ApiVersion.VERSION_3,
                sessionId
            )
        )
        latch.await(1, TimeUnit.SECONDS)
        val n5gNetworkIdentityEntity = nr5gDao.getNr5gNetworkIdentityEntity(sessionId)
        Assert.assertNotNull(n5gNetworkIdentityEntity)
    }

}
