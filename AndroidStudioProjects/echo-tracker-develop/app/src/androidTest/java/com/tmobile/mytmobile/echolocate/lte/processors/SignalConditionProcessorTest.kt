package com.tmobile.mytmobile.echolocate.lte.processors

import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.lte.oemdata.LteBaseDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.lte.database.EchoLocateLteDatabase
import com.tmobile.mytmobile.echolocate.lte.database.dao.LteDao
import com.tmobile.mytmobile.echolocate.lte.database.entity.BaseEchoLocateLteEntity
import com.tmobile.mytmobile.echolocate.lte.dataprocessor.*
import com.tmobile.mytmobile.echolocate.lte.model.LteMetricsData
import com.tmobile.mytmobile.echolocate.lte.reportprocessor.LteDataStatus
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class SignalConditionProcessorTest {

    private lateinit var lteDao: LteDao
    private lateinit var db: EchoLocateLteDatabase
    lateinit var context: Context

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, EchoLocateLteDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        lteDao = EchoLocateLteDatabase.getEchoLocateLteDatabase(context).lteDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testInsertSignalConditionEntity() {
        val latch = CountDownLatch(1)
        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateLteEntity =
            BaseEchoLocateLteEntity(
                201,
                LteDataStatus.STATUS_RAW,
                EchoLocateDateUtils.getTriggerTimeStamp(),
                "1",
                "2.0",
                sessionId
            )

        lteDao.insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity)
        val sourceList = mutableListOf<String>()
        sourceList.add(0, "0")
        sourceList.add(1, "0")
        sourceList.add(2, "66")
        sourceList.add(3, "20")
        sourceList.add(4, "1")

        sourceList.add(5, "66786")
        sourceList.add(6, "66")
        sourceList.add(7, "2018-05-14T08:54:52.693-0700")
        sourceList.add(8, "1")

        sourceList.add(9, "66786")
        sourceList.add(10, "66")
        sourceList.add(11, "20")
        sourceList.add(12, "1")
        sourceList.add(13, "66786")
        sourceList.add(14, "66")
        sourceList.add(15, "20")

        SignalConditionProcessor(context).execute(
            LteMetricsData(
                sourceList,
                baseEchoLocateLteEntity.triggerTimestamp,
                LteBaseDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION,
                sessionId
            )
        )
        latch.await(1, TimeUnit.SECONDS)
        val signalConditionEntity = lteDao.getSignalConditionEntityList()
        assert(signalConditionEntity.isNotEmpty())
    }

    @Test
    fun testInsertSignalConditionEntityV1() {
        val latch = CountDownLatch(1)
        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateLteEntity =
            BaseEchoLocateLteEntity(
                201,
                LteDataStatus.STATUS_RAW,
                EchoLocateDateUtils.getTriggerTimeStamp(),
                "1",
                "2.0",
                sessionId
            )

        lteDao.insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity)
        val sourceList = mutableListOf<String>()
        sourceList.add(0, "")
        sourceList.add(1, "66786")
        sourceList.add(2, "66")
        sourceList.add(3, "20")
        sourceList.add(4, "1")

        sourceList.add(5, "66786")
        sourceList.add(6, "66")
        sourceList.add(7, "20")
        sourceList.add(8, "1")

        sourceList.add(9, "66786")
        sourceList.add(10, "66")
        sourceList.add(11, "20")
        sourceList.add(12, "1")
        sourceList.add(13, "66786")
        sourceList.add(14, "66")
        sourceList.add(15, "20")

        SignalConditionProcessor(context).execute(
            LteMetricsData(
                sourceList,
                baseEchoLocateLteEntity.triggerTimestamp,
                LteBaseDataMetricsWrapper.ApiVersion.VERSION_1,
                sessionId
            )
        )
        latch.await(1, TimeUnit.SECONDS)
        val signalConditionEntity = lteDao.getSignalConditionEntityList()
        assert(signalConditionEntity.isNotEmpty())
    }

    @Test
    fun testInsertSignalConditionEntityV3() {
        val latch = CountDownLatch(1)
        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateLteEntity =
            BaseEchoLocateLteEntity(
                201,
                LteDataStatus.STATUS_RAW,
                EchoLocateDateUtils.getTriggerTimeStamp(),
                "1",
                "2.0",
                sessionId
            )

        lteDao.insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity)
        val sourceList = mutableListOf<String>()
        sourceList.add(0, "")
        sourceList.add(1, "66786")
        sourceList.add(2, "66")
        sourceList.add(3, "20")
        sourceList.add(4, "1")

        sourceList.add(5, "66786")
        sourceList.add(6, "66")
        sourceList.add(7, "20")
        sourceList.add(8, "1")

        sourceList.add(9, "66786")
        sourceList.add(10, "66")
        sourceList.add(11, "20")
        sourceList.add(12, "1")
        sourceList.add(13, "66786")
        sourceList.add(14, "66")
        sourceList.add(15, "20")

        SignalConditionProcessor(context).execute(
            LteMetricsData(
                sourceList,
                baseEchoLocateLteEntity.triggerTimestamp,
                LteBaseDataMetricsWrapper.ApiVersion.VERSION_3,
                sessionId
            )
        )
        latch.await(1, TimeUnit.SECONDS)
        val signalConditionEntity = lteDao.getSignalConditionEntityList()
        assert(signalConditionEntity.isNotEmpty())
    }
}