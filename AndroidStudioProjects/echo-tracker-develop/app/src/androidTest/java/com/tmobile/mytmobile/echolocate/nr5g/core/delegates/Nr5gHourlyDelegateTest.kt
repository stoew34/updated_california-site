package com.tmobile.mytmobile.echolocate.nr5g.core.delegates

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.EchoLocateNr5gDatabase
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.BaseEchoLocateNr5gEntity
import com.tmobile.mytmobile.echolocate.nr5g.core.intentlisteners.BaseNr5gBroadcastReceiver
import com.tmobile.mytmobile.echolocate.nr5g.manager.Base5gDataManager
import com.tmobile.mytmobile.echolocate.nr5g.manager.Nsa5gDataManager
import com.tmobile.pr.androidcommon.system.reflection.TmoBaseReflection
import org.junit.*
import org.junit.runners.MethodSorters
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


/**
 * This test class is responsible to provide test logic hourly delegateNr5g
 */
@Ignore
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class Nr5gHourlyDelegateTest {

    private var delegateNr5g: Nr5gHourlyDelegate? = null
    private lateinit var context: Context
    private lateinit var nr5gDataManager: Base5gDataManager
    private lateinit var bus: RxBus

    @After
    fun tearDown() {
        bus.destroy()
    }

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        delegateNr5g = Nr5gHourlyDelegate.getInstance(context)
        nr5gDataManager = Nsa5gDataManager(context)

        nr5gDataManager.baseNr5gAppBroadcastReceiver = BaseNr5gBroadcastReceiver()
        nr5gDataManager.baseNr5gAppBroadcastReceiver!!.setListener(nr5gDataManager)
        nr5gDataManager.baseNr5gScreenBroadcastReceiver = BaseNr5gBroadcastReceiver()
        nr5gDataManager.baseNr5gScreenBroadcastReceiver!!.setListener((nr5gDataManager))

        val isNr5gSupported = TmoBaseReflection.findField(
            Base5gDataManager::class.java,
            "isNr5gSupported"
        )
        isNr5gSupported.isAccessible = true
        isNr5gSupported.setBoolean(nr5gDataManager, true)

        nr5gDataManager.initNr5gDataManager()

        bus = RxBus.instance
    }

    /**
     * BaseEchoLocateNR5GEntity insertion check
     */
    @Test
    fun storeBaseEntityCheck() {
        val latch = CountDownLatch(1)
        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateNr5gEntity = BaseEchoLocateNr5gEntity(
            100,
            "",
            "timeStamp",
            sessionId
        )
        val nr5gDao = EchoLocateNr5gDatabase.getEchoLocateNr5gDatabase(context).nr5gDao()
        nr5gDao.insertBaseEchoLocateNr5gEntity(baseEchoLocateNr5gEntity)
        latch.await(1, TimeUnit.SECONDS)
        val baseEchoLocateNr5gEntityDb = nr5gDao.getBaseEchoLocateNrGEntityBySessionID(sessionId)
        assert(baseEchoLocateNr5gEntityDb.sessionId == sessionId)
    }

    /**
     * In configuration, Nr5GHourlyDelegateTest enabled,disabled case
     */
    /*@Test
    fun testConfigChangesNR5G() {
        val latch = CountDownLatch(1)
        val nr5g = Nr5g(0,true, 6,6, listOf(),"", listOf())
        val nr5GConfigEvent = Nr5gConfigEvent(nr5g)
        val postTicket = PostTicket(nr5GConfigEvent)
        bus.post(postTicket)
        latch.await(5, TimeUnit.SECONDS)

        val intent = Intent(context, Nr5gHourlyDelegate.HourlyTriggerReceiver::class.java)
        intent.action = "NR5G_HOURLY_TRIGGER_ACTION"
        val alarmUp = PendingIntent.getBroadcast(
            context, 100, intent,
            PendingIntent.FLAG_NO_CREATE
        ) != null
        assertEquals(alarmUp, true)


        val nr5g1 = Nr5g(0,false, 6,6, listOf(),"", listOf())
        val nr5GConfigEvent1 = Nr5gConfigEvent(nr5g1)
        val postTicket1 = PostTicket(nr5GConfigEvent1)
        bus.post(postTicket1)
        latch.await(5, TimeUnit.SECONDS)

        val alarmUp1 = (PendingIntent.getBroadcast(
            context, 100, intent,
            PendingIntent.FLAG_NO_CREATE
        ) != null)
        assertEquals(alarmUp1, false)
    }*/

}

