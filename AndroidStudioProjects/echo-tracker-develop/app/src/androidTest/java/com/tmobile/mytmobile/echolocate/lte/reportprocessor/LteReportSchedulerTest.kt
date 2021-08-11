package com.tmobile.mytmobile.echolocate.lte.reportprocessor

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.scheduler.database.dao.WorksParamsDao
import com.tmobile.mytmobile.echolocate.scheduler.events.SchedulerResponseEvent
import com.tmobile.mytmobile.echolocate.lte.utils.LteSharedPreference
import com.tmobile.mytmobile.echolocate.scheduler.WorkParameters
import com.tmobile.mytmobile.echolocate.scheduler.WorkScheduledStatus
import com.tmobile.mytmobile.echolocate.scheduler.database.EcholocateSchedulerDatabase
import com.tmobile.mytmobile.echolocate.scheduler.database.dao.SchedulerDao
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class LteReportSchedulerTest {
    lateinit var context: Context
    private lateinit var worksParamsDao: WorksParamsDao
    private lateinit var schedulerDao: SchedulerDao

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        worksParamsDao = EcholocateSchedulerDatabase.getEcholocateSchedulerDatabase(context).worksParamsDao()
        schedulerDao = EcholocateSchedulerDatabase.getEcholocateSchedulerDatabase(context).schedulerDao()
    }

    @After
    fun tearDown() {
        schedulerDao.deleteAll()
        worksParamsDao.deleteAll()
    }

    @Test
    fun schedulerJob() {
        val latch = CountDownLatch(1)
        val currentWorkParamListSize = worksParamsDao.getCount()
        val reportScheduler = LteReportScheduler(context = context)
        reportScheduler.schedulerJob(100,false)
        latch.await(3, TimeUnit.SECONDS)
        val workParamListSizeAfterInsert = worksParamsDao.getCount()
        assert(currentWorkParamListSize + 1 == workParamListSizeAfterInsert )
    }

    @Test
    fun stopScheduler() {
        runBlocking {
            val latch = CountDownLatch(1)
            val reportScheduler = LteReportScheduler(context = context)

            val workParameters = WorkParameters.Builder()
                .workId(12345L).oneTimeRequest(true).minimumLatency(1).initialBackoffMillis(1)
                .isPeriodic(false).jobState("")
                .maximumLatency(2).periodicInterval(0).shouldTriggerWhenCharging(false)
                .shouldTriggerWhenIdle(false)
                .shouldTriggerWhenWIFI(false)
                .sourceComponentName("LteModule")
            val androidWorkId = 12345L
            LteSharedPreference.scheduledWorkId = androidWorkId
            val schedulerResponseEvent = SchedulerResponseEvent(
                androidWorkId,
                androidWorkId.toString(),
                WorkScheduledStatus.SCHEDULED.state,
                workParameters.build()
            )
            schedulerResponseEvent.timeStamp = 123456
            schedulerResponseEvent.sourceComponent = "LteModule"
            schedulerDao.deleteAll()
            latch.await(3, TimeUnit.SECONDS)
            schedulerDao.insert(schedulerResponseEvent)
            latch.await(3, TimeUnit.SECONDS)
            val schedulerList = schedulerDao.getScheduler()
                .filter { it.sourceComponent != null && it.sourceComponent == "LteModule" }

            assertNotNull(schedulerList)
            reportScheduler.stopScheduler()
            latch.await(3, TimeUnit.SECONDS)
            val schedulerListAfterDelete = schedulerDao.getScheduler()
                .filter { it.sourceComponent != null && it.sourceComponent == "LteModule" }
            assertTrue(schedulerListAfterDelete.isEmpty())
        }
    }
}