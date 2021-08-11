package com.tmobile.mytmobile.echolocate.lte.delegates

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.lte.database.EchoLocateLteDatabase
import com.tmobile.mytmobile.echolocate.lte.database.entity.BaseEchoLocateLteEntity
import com.tmobile.mytmobile.echolocate.lte.intentlisteners.BaseLteBroadcastReceiver
import com.tmobile.mytmobile.echolocate.lte.manager.LteDataManager
import com.tmobile.pr.androidcommon.system.reflection.TmoBaseReflection
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


/**
 * This test class is responsible to provide test logic hourly delegateLte
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class LteHourlyDelegateTest {

    private var delegateLte: LteHourlyDelegate? = null
    private lateinit var context: Context
    private lateinit var lteDataManager: LteDataManager
    private lateinit var bus: RxBus

    @After
    fun tearDown() {
        bus.destroy()
    }

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        delegateLte = LteHourlyDelegate.getInstance(context)
        lteDataManager = LteDataManager(context)
        lteDataManager.baseLteBroadcastReceiver = BaseLteBroadcastReceiver()
        lteDataManager.baseLteBroadcastReceiver!!.setListener(lteDataManager)

        val isLteSupported = TmoBaseReflection.findField(
            LteDataManager::class.java,
            "isLteSupported"
        )
        isLteSupported.isAccessible = true
        isLteSupported.setBoolean(lteDataManager, true)

        lteDataManager.initLteDataManager()

        bus = RxBus.instance
    }

    /**
     * BaseEchoLocateLteEntity insertion check
     */
    @Test
    fun storeBaseEntityCheck() {
        val latch = CountDownLatch(1)
        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateLteEntity = BaseEchoLocateLteEntity(
            100,
            "",
            "timeStamp",
            "1",
            "1",
            sessionId
        )
        val lteDao = EchoLocateLteDatabase.getEchoLocateLteDatabase(context).lteDao()
        lteDao.insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity)
        latch.await(6, TimeUnit.SECONDS)
        val baseEchoLocateLteEntityDB = lteDao.getBaseEchoLocateLteEntityBySessionID(sessionId)
        assert(baseEchoLocateLteEntityDB.sessionId == sessionId)
    }


//    /**
//     * In configuration, Lte enabled,disabled case
//     */
//    @Test
//    fun testConfigChangesLte() {
//        val latch = CountDownLatch(1)
//        val lte = LTE(
//            true,
//            0,
//            listOf(),
//            6,
//            YoutubeRegex(listOf(), listOf()),
//            NetflixRegex(listOf(), listOf()),
//            SpeedTestRegex(listOf(), listOf(), listOf(), listOf(), listOf())
//        )
//
//        val lteConfigEvent = LteConfigEvent(lte)
//        val postTicket = PostTicket(lteConfigEvent)
//        bus.post(postTicket)
//        latch.await(5, TimeUnit.SECONDS)
//
//        val intent = Intent(context, LteHourlyDelegate.HourlyTriggerReceiver::class.java)
//        intent.action = "HOURLY_TRIGGER_ACTION"
//        val alarmUp = PendingIntent.getBroadcast(
//            context, 100, intent,
//            PendingIntent.FLAG_NO_CREATE
//        ) != null
//        assertEquals(alarmUp, true)
//
//        val lte1 = LTE(
//            false,
//            0,
//            listOf(),
//            6,
//            YoutubeRegex(listOf(), listOf()),
//            NetflixRegex(listOf(), listOf()),
//            SpeedTestRegex(listOf(), listOf(), listOf(), listOf(), listOf())
//        )
//        val lteConfigEvent1 = LteConfigEvent(lte1)
//        val postTicket1 = PostTicket(lteConfigEvent1)
//        bus.post(postTicket1)
//        latch.await(5, TimeUnit.SECONDS)
//
//        val alarmUp1 = (PendingIntent.getBroadcast(
//            context, 100, intent,
//            PendingIntent.FLAG_NO_CREATE
//        ) != null)
//        assertEquals(alarmUp1, false)
//    }

}

