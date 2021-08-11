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
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class DownLinkRFConfigurationProcessorTest {

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
    fun testInsertDownLinkRFConfigurationEntity() {
        val latch = CountDownLatch(1)
        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateLteEntity =
            BaseEchoLocateLteEntity(
                201,
                EchoLocateDateUtils.getTriggerTimeStamp(),
                "1",
                "2",
                "1",
                sessionId
            )

        lteDao.insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity)
        val sourceList = mutableListOf<String>()
        sourceList.add(0, "1")
        sourceList.add(1, "1")
        sourceList.add(2, "1")
        sourceList.add(3, "1")
        sourceList.add(4, "1")
        sourceList.add(5, "1")
        sourceList.add(6, "1")
        sourceList.add(7, "1")
        sourceList.add(8, "1")
        sourceList.add(9, "1")
        sourceList.add(10, "1")
        sourceList.add(11, "1")
        sourceList.add(12, "")
        sourceList.add(13, "1")
        sourceList.add(14, "1")
        sourceList.add(15, "1")
        sourceList.add(16, "1")

        DownLinkRFConfigurationProcessor(context).execute(
            LteMetricsData(
                sourceList,
                baseEchoLocateLteEntity.triggerTimestamp,
                LteBaseDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION,
                sessionId
            )
        )
        latch.await(1, TimeUnit.SECONDS)
        val downLinkRFConfigurationEntityList = lteDao.getDownLinkRFConfigurationInfoEntity()
        assert(downLinkRFConfigurationEntityList.isNotEmpty())
        val caEntityList = lteDao.getCAEntity()
        assert(caEntityList.isNotEmpty())
    }

    @Test
    fun testInsertDownLinkRFConfigurationEntityV1() {
        val latch = CountDownLatch(1)
        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateLteEntity =
            BaseEchoLocateLteEntity(
                201,
                EchoLocateDateUtils.getTriggerTimeStamp(),
                "1",
                "2",
                "1",
                sessionId
            )

        lteDao.insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity)
        val sourceList = mutableListOf<String>()
        sourceList.add(0, "1")
        sourceList.add(1, "1")
        sourceList.add(2, "1")
        sourceList.add(3, "1")
        sourceList.add(4, "1")
        sourceList.add(5, "1")
        sourceList.add(6, "1")
        sourceList.add(7, "1")

        DownLinkRFConfigurationProcessor(context).execute(
            LteMetricsData(
                sourceList,
                baseEchoLocateLteEntity.triggerTimestamp,
                LteBaseDataMetricsWrapper.ApiVersion.VERSION_1,
                sessionId
            )
        )
        latch.await(1, TimeUnit.SECONDS)
        val downLinkRFConfigurationEntityList = lteDao.getDownLinkRFConfigurationInfoEntity()
        assert(downLinkRFConfigurationEntityList.isNotEmpty())
        val caEntityList = lteDao.getCAEntity()
        assert(caEntityList.isNotEmpty())
    }

    @Test
    fun testInsertDownLinkRFConfigurationEntityV3() {
        val latch = CountDownLatch(1)
        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateLteEntity =
            BaseEchoLocateLteEntity(
                201,
                EchoLocateDateUtils.getTriggerTimeStamp(),
                "1",
                "2",
                "1",
                sessionId
            )

        lteDao.insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity)
        val sourceList = mutableListOf<String>()
        sourceList.add(0, "1")
        sourceList.add(1, "1")
        sourceList.add(2, "1")
        sourceList.add(3, "1")
        sourceList.add(4, "1")
        sourceList.add(5, "1")
        sourceList.add(6, "1")
        sourceList.add(7, "1")

        DownLinkRFConfigurationProcessor(context).execute(
            LteMetricsData(
                sourceList,
                baseEchoLocateLteEntity.triggerTimestamp,
                LteBaseDataMetricsWrapper.ApiVersion.VERSION_3,
                sessionId
            )
        )
        latch.await(1, TimeUnit.SECONDS)
        val downLinkRFConfigurationEntityList = lteDao.getDownLinkRFConfigurationInfoEntity()
        assert(downLinkRFConfigurationEntityList.isNotEmpty())
        val caEntityList = lteDao.getCAEntity()
        assert(caEntityList.isNotEmpty())
    }
}