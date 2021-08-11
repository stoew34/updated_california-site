//package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.dataprocessor
//
//import android.Manifest
//import android.content.Context
//import android.os.Build
//import androidx.room.Room
//import androidx.test.platform.app.InstrumentationRegistry
//import com.tmobile.mytmobile.echolocate.nr5g.core.oemdata.Nr5gBaseDataMetricsWrapper
//import com.tmobile.mytmobile.echolocate.lte.reportprocessor.LteDataStatus
//import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.EchoLocateNr5gDatabase
//import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.dao.Nr5gDao
//import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.BaseEchoLocateNr5gEntity
//import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.EndcUplinkLogEntity
//import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.BaseNr5gData
//import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.BaseNr5gMetricsData
//import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gUtils
//import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils.Companion.getTriggerTimeStamp
//import org.junit.After
//import org.junit.Assert
//import org.junit.Before
//import org.junit.Test
//import java.util.*
//import java.util.concurrent.CountDownLatch
//import java.util.concurrent.TimeUnit
//
///**
// * this class is responsible to creates the EndcUplinkLog object and asserts the data
// */
//class EndcUplinkLogProcessorTest {
//
//    private lateinit var nr5gDao: Nr5gDao
//    private lateinit var db: EchoLocateNr5gDatabase
//    lateinit var context: Context
//
//    @Before
//    fun setUp() {
//        context = InstrumentationRegistry.getInstrumentation().targetContext
//        db = Room.inMemoryDatabaseBuilder(context, EchoLocateNr5gDatabase::class.java)
//            .allowMainThreadQueries()
//            .build()
//        nr5gDao = EchoLocateNr5gDatabase.getEchoLocateNr5gDatabase(context).nr5gDao()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            InstrumentationRegistry.getInstrumentation().uiAutomation
//                .grantRuntimePermission(
//                    context.packageName,
//                    Manifest.permission.READ_PHONE_STATE
//                )
//        } else {
//            InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(
//                "pm grant " + context.packageName
//                        + " android.permission.READ_PHONE_STATE"
//            )
//        }
//    }
//
//    @After
//    fun tearDown() {
//        db.close()
//    }
//
//    @Test
//    fun testInsertEndcUpLinkLog() {
//
//        val latch = CountDownLatch(1)
//        val sessionId = UUID.randomUUID().toString()
//        val baseEchoLocateNr5gEntity =
//            BaseEchoLocateNr5gEntity(
//                201,
//                LteDataStatus.STATUS_RAW,
//                getTriggerTimeStamp(),
//                sessionId
//            )
//
//        nr5gDao.insertBaseEchoLocateNr5gEntity(baseEchoLocateNr5gEntity)
//        val time = Nr5gUtils.getFormattedTime(Calendar.getInstance().timeInMillis)
//        val endcUplinkLogEntity1 = EndcUplinkLogEntity(
//            time,
//            10,
//            1
//        )
//
//        val baseNr5gMetricsData = BaseNr5gMetricsData(endcUplinkLogEntity1, "", DataMetricsWrapper.ApiVersion.UNKNOWN_VERSION, "")
//        val baseNr5gData = BaseNr5gData(sessionId, UUID.randomUUID().toString())
//
//        Nsa5gEndcUplinkLogProcessor(context).processNr5gMetricsData(baseNr5gMetricsData, baseNr5gData)
//        latch.await(1, TimeUnit.SECONDS)
//
//
//        val endcUplinkLogEntity2 = nr5gDao.getEndcUplinkLogEntity(sessionId)
//        latch.await(1, TimeUnit.SECONDS)
//        Assert.assertNotNull(endcUplinkLogEntity2)
//    }
//
//    @Test
//    fun testDateFormat(){
//       assert(Nr5gUtils.getFormattedTime(Calendar.getInstance().timeInMillis).isNotEmpty())
//    }
//
//
//}