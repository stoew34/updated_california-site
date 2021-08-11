package com.tmobile.mytmobile.echolocate.analytics.database.repository

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.analytics.database.EchoLocateAnalyticsDatabase
import com.tmobile.mytmobile.echolocate.analytics.database.dao.AnalyticsDao
import com.tmobile.mytmobile.echolocate.analytics.database.entity.AnalyticsEventEntity
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
@RunWith(AndroidJUnit4::class)
class AnalyticsRepositoryTest {

    lateinit var instrumentationContext: Context
    private lateinit var analyticsRepository: AnalyticsRepository
    private lateinit var analyticsDao: AnalyticsDao

    @Before
    fun setUp() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().targetContext
        analyticsRepository = AnalyticsRepository(instrumentationContext)
        EchoLocateAnalyticsDatabase.destroyDataBase()
        analyticsDao =
            EchoLocateAnalyticsDatabase.getEchoLocateAnalyticsDatabase(instrumentationContext)
                .analyticsDao()
    }

    /**
     * THis method is used to test data insertion
     */
    @Test
    fun testDataInsertion() {
        val latch = CountDownLatch(1)
        val analyticsEventEntity =
            AnalyticsEventEntity(EchoLocateDateUtils.getTriggerTimeStamp(), "analytics", "", "")
        analyticsRepository.insertAnalyticsEventEntity(analyticsEventEntity)
        latch.await(2, TimeUnit.SECONDS)
        assert(analyticsDao.getAllAnalyticsEventEntity(1).isNotEmpty())
    }


}

