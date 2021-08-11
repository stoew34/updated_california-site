package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.dataprocessor

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.tmobile.mytmobile.echolocate.TestActivity
import com.tmobile.mytmobile.echolocate.nr5g.core.oemdata.Nr5gBaseDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.lte.reportprocessor.LteDataStatus
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.EchoLocateNr5gDatabase
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.dao.Nr5gDao
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.BaseEchoLocateNr5gEntity
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.ConnectedWifiStatusEntity
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.BaseNr5gData
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.BaseNr5gMetricsData
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.ConnectedWifiStatus
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.utils.Nsa5gDataCollectionService
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils.Companion.getTriggerTimeStamp
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
class ConnectedWifiStatusProcessorTest {

    private lateinit var context: Context
    private lateinit var nr5gDao: Nr5gDao
    private lateinit var db: EchoLocateNr5gDatabase

    @get:Rule
    private val activityRule = ActivityTestRule(
        TestActivity::class.java, false, false
    )

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
    fun testGetNr5gStatusProcessor() {
        val connectedWifiStatus = ConnectedWifiStatus(
            "bssid",
            "bssLoad",
            "ssId",
            3,
            "capabil",
            0,
            1,
            "mode",
            2,
            3,
            "name",
            1,
            1
        )
        val nsa5gDataCollectionService = mockkClass(Nsa5gDataCollectionService::class)
        every {
            nsa5gDataCollectionService.processWifiData(context)
        } returns connectedWifiStatus
        val processor = Nsa5gConnectedWifiStatusProcessor(context)
        processor.nsa5gDataCollectionService = nsa5gDataCollectionService


        val latch = CountDownLatch(1)
        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateNr5gEntity =
            BaseEchoLocateNr5gEntity(
                301,
                LteDataStatus.STATUS_RAW,
                getTriggerTimeStamp(),
                sessionId
            )
        nr5gDao.insertBaseEchoLocateNr5gEntity(baseEchoLocateNr5gEntity)
        val connectedWifiStatusEntity1 = ConnectedWifiStatusEntity(
            "bssid",
            "bssLoad",
            "ssId",
            3,
            "capabil",
            0,
            1,
            "mode",
            2,
            3,
            "name",
            1,
            1
        )

        val baseNr5gMetricsData = BaseNr5gMetricsData(
            connectedWifiStatusEntity1,
            "",
            Nr5gBaseDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION,
            sessionId
        )
        val baseNr5gData = BaseNr5gData(sessionId, UUID.randomUUID().toString())

        processor.processNr5gMetricsData(baseNr5gMetricsData, baseNr5gData)
        latch.await(1, TimeUnit.SECONDS)
        val connectedWifiStatusEntity2 = nr5gDao.getConnectedWifiStatusEntity(sessionId)

        Assert.assertNotNull(connectedWifiStatusEntity2)

    }
}

