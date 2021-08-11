package com.tmobile.mytmobile.echolocate.lte.delegates

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.lte.utils.ApplicationState
import com.tmobile.mytmobile.echolocate.lte.utils.LTEApplications
import com.tmobile.mytmobile.echolocate.lte.utils.LteConstants
import com.tmobile.mytmobile.echolocate.lte.utils.logcat.LogcatListener
import junit.framework.Assert.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * This test class is responsible to provide test logic youtube delegate
 * which implements the test case logic for various actions such focus state etc..
 */
class NetflixDelegateTest {

    private var delegate: NetflixDelegate? = null
    private lateinit var context: Context
    private var logcatListener: LogcatListener? = null

    /*
    todo add the db logic when db logic is ready
     */


    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        logcatListener = LogcatListener.getInstance()
        delegate = NetflixDelegate.getInstance(context)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            InstrumentationRegistry.getInstrumentation().uiAutomation
                .grantRuntimePermission(
                    context.packageName,
                    Manifest.permission.READ_PHONE_STATE
                )
        } else {
            InstrumentationRegistry.getInstrumentation().uiAutomation.executeShellCommand(
                "pm grant " + context.packageName
                        + " android.permission.READ_PHONE_STATE"
            )
        }
    }

    @Test
    fun onStateChangedFocusGain() {
        delegate?.processApplicationState(ApplicationState.FOCUS_GAIN)
        assertTrue(delegate!!.stateFocused)
    }

    @Test
    fun onStateChangedFocusLoss() {
        delegate?.processApplicationState(ApplicationState.FOCUS_LOSS)
        assertFalse(delegate!!.stateFocused)
    }

    @Test
    fun onStateChangedFocusLossAndAssertEmptyList() {
        delegate?.processApplicationState(ApplicationState.FOCUS_LOSS)
        assertFalse(delegate!!.stateFocused)
        assert(delegate?.timeOutIDList!!.isEmpty())

    }

    @Test
    fun testOnIntentStreamingStart() {
        delegate?.handleIntent(Intent(delegate?.STREAMING_START_ACTION))
        assertNotNull(delegate?.timeOutIDList)


    }

    @Test
    fun testOnIntentStreamingEnd() {
        delegate?.handleIntent(Intent(delegate?.STREAMING_END_ACTION))
    }

    @Test
    fun testOnIntentScreenOff() {
        delegate?.handleIntent(Intent(Intent.ACTION_SCREEN_OFF))

    }

    @Test
    fun whenApplicationScreenOff() {
        delegate?.processApplicationState(ApplicationState.SCREEN_OFF)
        Assert.assertEquals(695, delegate?.getScreenOffCode())
    }

    /**
     * This method is used to check the actions.
     */
    @Test
    fun checkingActions() {
        val STREAMING_START_ACTION = "NETFLIX_STREAMING_START_ACTION_ECHOLTE"
        val STREAMING_END_ACTION = "NETFLIX_STREAMING_END_ACTION_ECHOLTE"
        val STREAMING_START_TRIGGER_ID = "NETFLIX_STREAMING_START_TRIGGER_ID_ECHOLTE"
        val STREAMING_END_TRIGGER_ID = "NETFLIX_STREAMING_END_TRIGGER_ID_ECHOLTE"
        val TIMEOUT_ACTION = "NETFLIX_TIMEOUT_ACTION_ECHOLTE"
        Assert.assertEquals(STREAMING_START_ACTION, delegate?.getStreamingStartAction())
        Assert.assertEquals(STREAMING_END_ACTION, delegate?.getStreamingEndAction())
        Assert.assertEquals(STREAMING_START_TRIGGER_ID, delegate?.getStreamingStartTriggerId())
        Assert.assertEquals(STREAMING_END_TRIGGER_ID, delegate?.getStreamingEndTriggerId())
        Assert.assertEquals(TIMEOUT_ACTION, delegate?.getTimeoutAction())
        Assert.assertEquals(LTEApplications.NETFLIX, delegate?.getTriggerApplication())


        val STREAM_TEN_SECONDS = 610
        val STREAM_THIRTY_SECONDS = 615
        val STREAM_SIXTY_SECONDS = 620
        val STREAM_THREE_HUNDRED_SECONDS = 625
        val STREAM_SIX_HUNDRED_SECONDS = 630
        val STREAM_EIGHTEEN_HUNDRED_SECONDS = 635
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
    fun getApplicationStateTest(){
        val FOCUS_GAIN_CODE = 601
        val FOCUS_LOSS_CODE = 690
        val SCREEN_OFF_CODE = 695
        Assert.assertEquals(ApplicationState.FOCUS_GAIN.name, delegate?.getApplicationState(FOCUS_GAIN_CODE)?.name)
        Assert.assertEquals(ApplicationState.SCREEN_OFF.name, delegate?.getApplicationState(SCREEN_OFF_CODE)?.name)
        Assert.assertEquals(ApplicationState.FOCUS_LOSS.name, delegate?.getApplicationState(FOCUS_LOSS_CODE)?.name)
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
        val mLink  = "https://www.tmobile.com?docid=net123456"
        val intent = Intent("android.intent.action.SCREEN_OFF")
        intent.putExtra(LINE_EXTRA, mLink)
        Assert.assertEquals("net123456", delegate?.extractContentIdFromIntent(intent))

        val intentLog = Intent("android.intent.action.SCREEN_OFF")
        Assert.assertEquals(LteConstants.EMPTY, delegate?.extractContentIdFromIntent(intentLog))

        val intentPattern = Intent("android.intent.action.SCREEN_OFF")
        val mMisMatchLink  = "www.tmobile.com"
        intent.putExtra(LINE_EXTRA, mMisMatchLink)
        Assert.assertEquals(LteConstants.EMPTY, delegate?.extractContentIdFromIntent(intentPattern))
    }

}

