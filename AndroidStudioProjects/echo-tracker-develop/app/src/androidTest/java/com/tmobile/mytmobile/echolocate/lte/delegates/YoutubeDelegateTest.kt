package com.tmobile.mytmobile.echolocate.lte.delegates

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.configuration.events.configurationevents.LteConfigEvent
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.PostTicket
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.configuration.model.LTE
import com.tmobile.mytmobile.echolocate.configuration.model.lteregex.NetflixRegex
import com.tmobile.mytmobile.echolocate.configuration.model.lteregex.SpeedTestRegex
import com.tmobile.mytmobile.echolocate.configuration.model.lteregex.YoutubeRegex
import com.tmobile.mytmobile.echolocate.lte.database.EchoLocateLteDatabase
import com.tmobile.mytmobile.echolocate.lte.database.dao.LteDao
import com.tmobile.mytmobile.echolocate.lte.intentlisteners.BaseLteBroadcastReceiver
import com.tmobile.mytmobile.echolocate.lte.utils.logcat.LogcatListener
import com.tmobile.mytmobile.echolocate.lte.manager.LteDataManager
import com.tmobile.mytmobile.echolocate.lte.reportprocessor.LteDataStatus.Companion.STATUS_RAW
import com.tmobile.mytmobile.echolocate.lte.utils.*
import io.mockk.every
import io.mockk.mockkObject
import org.junit.*
import org.junit.runners.MethodSorters
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * This test class is responsible to provide test logic youtube delegate
 * which implements the test case logic for various actions such focus state etc..
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class YoutubeDelegateTest {

    private var delegate: YoutubeDelegate? = null
    private lateinit var context: Context
    private var logcatListener: LogcatListener? = null
    private val broadCastManager =
        LocalBroadcastManager.getInstance(InstrumentationRegistry.getInstrumentation().targetContext)
    private lateinit var lteDataManager: LteDataManager
    private lateinit var applicationTrigger: ApplicationTrigger
    private lateinit var bus: RxBus
    private lateinit var lteDao: LteDao
    private val lte = LTE(
            true, 12, listOf("packages enabled"), 100, YoutubeRegex(listOf(), listOf()),
            NetflixRegex(listOf(), listOf()),
            SpeedTestRegex(listOf(), listOf(), listOf(), listOf(), listOf())
            , "", listOf()
    )
    /*
    todo add the db logic when db logic is ready
     */

    @Test
    fun whenApplicationInFocusLoss() {
        delegate?.processApplicationState(ApplicationState.FOCUS_LOSS)
        Assert.assertEquals(590, delegate?.getFocusLossCode())
    }

    @Test
    fun whenApplicationScreenOff() {
        delegate?.processApplicationState(ApplicationState.SCREEN_OFF)
        Assert.assertEquals(595, delegate?.getScreenOffCode())
    }

    @Test
    fun checkingActions() {
        val STREAMING_START_ACTION = "YOUTUBE_STREAMING_START_ACTION_ECHOLTE"
        val STREAMING_END_ACTION = "YOUTUBE_STREAMING_END_ACTION_ECHOLTE"
        val STREAMING_START_TRIGGER_ID = "YOUTUBE_STREAMING_START_TRIGGER_ID_ECHOLTE"
        val STREAMING_END_TRIGGER_ID = "YOUTUBE_STREAMING_END_TRIGGER_ID_ECHOLTE"
        val TIMEOUT_ACTION = "YOUTUBE_TIMEOUT_ACTION_ECHOLTE"
        Assert.assertEquals(STREAMING_START_ACTION, delegate?.getStreamingStartAction())
        Assert.assertEquals(STREAMING_END_ACTION, delegate?.getStreamingEndAction())
        Assert.assertEquals(STREAMING_START_TRIGGER_ID, delegate?.getStreamingStartTriggerId())
        Assert.assertEquals(STREAMING_END_TRIGGER_ID, delegate?.getStreamingEndTriggerId())
        Assert.assertEquals(TIMEOUT_ACTION, delegate?.getTimeoutAction())
        Assert.assertEquals(LTEApplications.YOUTUBE, delegate?.getTriggerApplication())




        val STREAM_TEN_SECONDS = 510
        val STREAM_THIRTY_SECONDS = 515
        val STREAM_SIXTY_SECONDS = 520
        val STREAM_THREE_HUNDRED_SECONDS = 525
        val STREAM_SIX_HUNDRED_SECONDS = 530
        val STREAM_EIGHTEEN_HUNDRED_SECONDS = 535
        val TIMEOUT_REQUEST_CODE = 9879875

        Assert.assertEquals(STREAM_TEN_SECONDS, delegate?.getTenSecondsCode())
        Assert.assertEquals(STREAM_THIRTY_SECONDS, delegate?.getThirtySecondsCode())
        Assert.assertEquals(STREAM_SIXTY_SECONDS, delegate?.getSixtyecondsCode())
        Assert.assertEquals(STREAM_THREE_HUNDRED_SECONDS, delegate?.getThreeHundreSecondsCode())
        Assert.assertEquals(STREAM_SIX_HUNDRED_SECONDS, delegate?.getSixHundredSecondsCode())
        Assert.assertEquals(STREAM_EIGHTEEN_HUNDRED_SECONDS, delegate?.getEighteenHundredCode())
        Assert.assertEquals(TIMEOUT_REQUEST_CODE, delegate?.getTimeoutRequestCode())

    }

    @Test
    fun extractLinkFromLogTest() {
        val LINE_EXTRA = "LINE_EXTRA"
        val mLink  = "https://www.tmobile.com"
        val intent = Intent("android.intent.action.SCREEN_OFF")
        intent.putExtra(LINE_EXTRA, mLink)
        Assert.assertEquals(mLink, delegate?.extractLinkFromLog(intent))

        val intentLog = Intent("android.intent.action.SCREEN_OFF")
        Assert.assertEquals(LteConstants.EMPTY, delegate?.extractLinkFromLog(intentLog))

        val intentPattern = Intent("android.intent.action.SCREEN_OFF")
        val mMisMatchLink  = "www.tmobile.com"
        intent.putExtra(LINE_EXTRA, mMisMatchLink)
        Assert.assertEquals(LteConstants.EMPTY, delegate?.extractLinkFromLog(intentPattern))

    }

   @Test
    fun extractContentIdFromIntentTest() {
        val LINE_EXTRA = "LINE_EXTRA"
        val mLink  = "https://www.tmobile.com?docid=youtube123456"
        val intent = Intent("android.intent.action.SCREEN_OFF")
        intent.putExtra(LINE_EXTRA, mLink)
        Assert.assertEquals("youtube123456", delegate?.extractContentIdFromIntent(intent))

       val intentLog = Intent("android.intent.action.SCREEN_OFF")
       Assert.assertEquals(LteConstants.EMPTY, delegate?.extractContentIdFromIntent(intentLog))

       val intentPattern = Intent("android.intent.action.SCREEN_OFF")
       val mMisMatchLink  = "www.tmobile.com"
       intent.putExtra(LINE_EXTRA, mMisMatchLink)
       Assert.assertEquals(LteConstants.EMPTY, delegate?.extractContentIdFromIntent(intentPattern))
    }

    @Test
    fun getApplicationStateTest(){
        val FOCUS_GAIN_CODE = 501
        val FOCUS_LOSS_CODE = 590
        val SCREEN_OFF_CODE = 595
        Assert.assertEquals(ApplicationState.FOCUS_GAIN.name, delegate?.getApplicationState(FOCUS_GAIN_CODE)?.name)
        Assert.assertEquals(ApplicationState.SCREEN_OFF.name, delegate?.getApplicationState(SCREEN_OFF_CODE)?.name)
        Assert.assertEquals(ApplicationState.FOCUS_LOSS.name, delegate?.getApplicationState(FOCUS_LOSS_CODE)?.name)
    }

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        logcatListener = LogcatListener.getInstance()
        delegate = YoutubeDelegate.getInstance(context)
        lteDataManager = LteDataManager(context)
        lteDataManager.baseLteBroadcastReceiver = BaseLteBroadcastReceiver()
        lteDataManager.baseLteBroadcastReceiver!!.setListener(lteDataManager)
        lteDataManager.initLteDataManager()
        applicationTrigger = ApplicationTrigger(context)
        bus = RxBus.instance
        lteDao = EchoLocateLteDatabase.getEchoLocateLteDatabase(context).lteDao()

    }

    /**
     * Testcase for focus gain youtube event
     */
    @Test
    fun testFocusGainSendBroadCast() {
        val latch = CountDownLatch(1)
        val PACKAGE_NAME_EXTRA = "ApplicationPackageName"
        val OEM_TIMESTAMP_EXTRA = "oemIntentTimestamp"

        val intent = Intent("diagandroid.app.ApplicationState")

        intent.putExtra(PACKAGE_NAME_EXTRA, "com.google.android.youtube")
        intent.putExtra(OEM_TIMESTAMP_EXTRA, "oemIntentTimestamp")
        intent.putExtra(LteIntents.APP_STATE_KEY, "FOCUS_GAIN")

        val intentFilter = IntentFilter("diagandroid.app.ApplicationState")
        broadCastManager.registerReceiver(lteDataManager.baseLteBroadcastReceiver!!, intentFilter)
        val status = broadCastManager.sendBroadcast(intent)
        lteDataManager.setBroadCastRegistered(true)
        latch.await(1, TimeUnit.SECONDS)
        Assert.assertTrue(status)
        broadCastManager.unregisterReceiver(lteDataManager.baseLteBroadcastReceiver!!)
    }

    /**
     * Testcase for focus gain youtube event
     */
    @Test
    fun testFocusStop() {
        val latch = CountDownLatch(1)
        val PACKAGE_NAME_EXTRA = "ApplicationPackageName"
        val OEM_TIMESTAMP_EXTRA = "oemIntentTimestamp"

        //lteDataManager.initLteDataManager()
        val intent = Intent("diagandroid.app.ApplicationState")

        intent.putExtra(PACKAGE_NAME_EXTRA, "com.google.android.youtube")
        intent.putExtra(OEM_TIMESTAMP_EXTRA, "oemIntentTimestamp")
        intent.putExtra(LteIntents.APP_STATE_KEY, "FOCUS_LOSS")

        val intentFilter = IntentFilter("diagandroid.app.ApplicationState")
        broadCastManager.registerReceiver(lteDataManager.baseLteBroadcastReceiver!!, intentFilter)
        val status = broadCastManager.sendBroadcast(intent)
        latch.await(1, TimeUnit.SECONDS)
        Assert.assertTrue(status)
        broadCastManager.unregisterReceiver(lteDataManager.baseLteBroadcastReceiver!!)
    }


    /**
     * Testcase for speed test stream start
     */
    @Test
    fun testFocusStart() {
        testFocusGainSendBroadCast()
        Log.d(
            "com.google.android.youtube",
            "2019-11-19 14:56:58.895 22678-22702/? W/YouTube: Pinging https://s.youtube.com/api/stats/playback?cl=280975310&ei=C6TTXZPiFMzHz7sPzuSC-Ac&feature=g-high-rec&fexp=11217114%2C23803853%2C11216358%2C23811262%2C39786090%2C23842986%2C23848795%2C11213630%2C23839597%2C11211694%2C23837993%2C11217119%2C11211411%2C39786092%2C23856652%2C1714252%2C23837861&ns=yt&plid=AAWXrqHeN1MOnKUC&autoplay=1&delay=10&el=detailpage&len=689&mos=1&of=dHNG9R6EX4vEsOXQ8pNbvg&vm=CAEQARgEKiBKVmpsYXZfam82SzE4RG14NWNMSDZTSzV3VUdadnFtQg&cpn=xI404GVVXlL0x-bD&ver=2&cplatform=mobile&cbr=com.google.android.youtube&c=android&cmodel=Android%20SDK%20built%20for%20x86&cos=Android&csdk=29&cbrver=14.19.57&cver=14.19.57&cosvebr=10&cbrand=Google&cplayer=ANDROID_EXOPLAYER&rt=0.1&lact=3661&fmt=134&afmt=140&cmt=0.0&conn=3&vis=5&uao=0&muted=1&volume=33&rtn=10.0&docid=(scrubbed)&referrer=(scrubbed)\n"
        )

        /**
         * This log is end action
         */
//        Log.d(
//            "com.google.android.youtube",
//            "2019-11-19 14:58:16.816 22678-22769/? W/YouTube: Pinging https://s.youtube.com/api/stats/qoe?cl=280975310&ei=C6TTXZPiFMzHz7sPzuSC-Ac&event=streamingstats&feature=g-high-rec&fexp=11217114%2C23803853%2C11216358%2C23811262%2C39786090%2C23842986%2C23848795%2C11213630%2C23839597%2C11211694%2C23837993%2C11217119%2C11211411%2C39786092%2C23856652%2C1714252%2C23837861&ns=yt&plid=AAWXrqHeN1MOnKUC&cpn=xI404GVVXlL0x-bD&cplatform=mobile&cbr=com.google.android.youtube&c=android&cmodel=Android%20SDK%20built%20for%20x86&cos=Android&csdk=29&cbrver=14.19.57&cver=14.19.57&cosver=10&cbrand=Google&conn=18.241:3&cplayer=ANDROID_EXOPLAYER&bat=18.248:1.000:0&bh=17.319:13.04&cmt=18.238:16.892&fmt=134&seq=2&docid=(scrubbed)&referrer=(scrubbed)\n"
//        )
    }


    @Test
    fun testPublishConfigChanges() {
        val latch = CountDownLatch(1)
        val lte = LTE(
            false, 6, listOf(), 10, YoutubeRegex(listOf(), listOf()),
            NetflixRegex(listOf(), listOf()),
            SpeedTestRegex(listOf(), listOf(), listOf(), listOf(), listOf())
            , "", listOf()
        )
        val lteConfigEvent = LteConfigEvent(lte)
        val postTicket = PostTicket(lteConfigEvent)
        bus.post(postTicket)
        latch.await(1, TimeUnit.MILLISECONDS)
        assert(applicationTrigger.getTriggerLimit() == 10)
        RxBus.instance.destroy()
    }

    @Test
    fun testBroadCastForTriggerLimitReached() {
        val triggerCount = 100
        applicationTrigger.saveTriggerCount(triggerCount)
        val latch = CountDownLatch(1)
        val PACKAGE_NAME_EXTRA = "ApplicationPackageName"
        val OEM_TIMESTAMP_EXTRA = "oemIntentTimestamp"

        val intent = Intent("diagandroid.app.ApplicationState")

        intent.putExtra(PACKAGE_NAME_EXTRA, "com.google.android.youtube")
        intent.putExtra(OEM_TIMESTAMP_EXTRA, "oemIntentTimestamp")
        intent.putExtra(LteIntents.APP_STATE_KEY, "FOCUS_GAIN")

        val intentFilter = IntentFilter("diagandroid.app.ApplicationState")
        broadCastManager.registerReceiver(lteDataManager.baseLteBroadcastReceiver!!, intentFilter)
        val status = broadCastManager.sendBroadcast(intent)
        lteDataManager.setBroadCastRegistered(true)
        latch.await(1, TimeUnit.SECONDS)
        Assert.assertTrue(status)
        assert(applicationTrigger.getTriggerCount() == triggerCount)
        broadCastManager.unregisterReceiver(lteDataManager.baseLteBroadcastReceiver!!)
    }

    @Test
    fun testTriggerLimit() {
        val triggerLimit = 100
        applicationTrigger.saveTriggerLimit(triggerLimit)
        assert(applicationTrigger.getTriggerLimit() == triggerLimit)
    }

    @Test
    fun testBroadCastForTriggerCount() {
        lteDataManager.initLteDataManager()
        val triggerCount = 95
        applicationTrigger.saveTriggerCount(triggerCount)
        val latch = CountDownLatch(1)
        val PACKAGE_NAME_EXTRA = "ApplicationPackageName"
        val OEM_TIMESTAMP_EXTRA = "oemIntentTimestamp"

        val intent = Intent("diagandroid.app.ApplicationState")

        intent.putExtra(PACKAGE_NAME_EXTRA, "com.google.android.youtube")
        intent.putExtra(OEM_TIMESTAMP_EXTRA, "oemIntentTimestamp")
        intent.putExtra(LteIntents.APP_STATE_KEY, "FOCUS_GAIN")

        val intentFilter = IntentFilter("diagandroid.app.ApplicationState")
        broadCastManager.registerReceiver(lteDataManager.baseLteBroadcastReceiver!!, intentFilter)
        val status = broadCastManager.sendBroadcast(intent)
        lteDataManager.setBroadCastRegistered(true)
        latch.await(1, TimeUnit.SECONDS)
        Assert.assertTrue(status)
        assert(applicationTrigger.getTriggerCount() > triggerCount)
        broadCastManager.unregisterReceiver(lteDataManager.baseLteBroadcastReceiver!!)
    }

    @Test
    fun testHandleIntentScreenOffAction() {
        val latch = CountDownLatch(1)
        mockkObject(LteSharedPreference)
        every { LteSharedPreference.triggerCount } returns 10
        every { LteSharedPreference.triggerLimit } returns 10

        delegate?.setRegexFromConfig(lte)

        delegate?.processApplicationState(ApplicationState.FOCUS_GAIN)
        lteDao.deleteAllBaseEchoLocateLteEntity()
        val initialEntityCount = lteDao.getBaseEchoLocateLteEntityByStatus(STATUS_RAW).size
        val intent = Intent(Intent.ACTION_SCREEN_OFF)
        delegate?.handleIntent(intent)
        latch.await(3, TimeUnit.SECONDS)
        val actualEntityCount = lteDao.getBaseEchoLocateLteEntityByStatus(STATUS_RAW).size
        lteDao.deleteAllBaseEchoLocateLteEntity()
        assert(initialEntityCount+1 == actualEntityCount)
    }

    @Test
    fun testHandleIntentStreamingStartAction() {
        val latch = CountDownLatch(1)
        mockkObject(LteSharedPreference)
        every { LteSharedPreference.triggerCount } returns 10
        every { LteSharedPreference.triggerLimit } returns 10

        delegate?.setRegexFromConfig(lte)

        lteDao.deleteAllBaseEchoLocateLteEntity()
        val initialEntityCount = lteDao.getBaseEchoLocateLteEntityByStatus(STATUS_RAW).size
        val intent = Intent(delegate?.STREAMING_START_ACTION)
        delegate?.handleIntent(intent)
        latch.await(3, TimeUnit.SECONDS)
        val actualEntityCount = lteDao.getBaseEchoLocateLteEntityByStatus(STATUS_RAW).size
        lteDao.deleteAllBaseEchoLocateLteEntity()
        assert(initialEntityCount+1 == actualEntityCount)
    }

    @Test
    fun testHandleIntentStreamingEndAction() {
        val latch = CountDownLatch(1)
        mockkObject(LteSharedPreference)
        every { LteSharedPreference.triggerCount } returns 10
        every { LteSharedPreference.triggerLimit } returns 10

        delegate?.setRegexFromConfig(lte)

        lteDao.deleteAllBaseEchoLocateLteEntity()
        val initialEntityCount = lteDao.getBaseEchoLocateLteEntityByStatus(STATUS_RAW).size
        val intent = Intent(delegate?.STREAMING_END_ACTION)
        delegate?.handleIntent(intent)
        latch.await(3, TimeUnit.SECONDS)
        val actualEntityCount = lteDao.getBaseEchoLocateLteEntityByStatus(STATUS_RAW).size
        lteDao.deleteAllBaseEchoLocateLteEntity()
        assert(initialEntityCount+1 == actualEntityCount)
    }
}

