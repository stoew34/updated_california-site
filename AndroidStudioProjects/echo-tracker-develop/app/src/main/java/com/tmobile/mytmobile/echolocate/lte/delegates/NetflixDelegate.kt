package com.tmobile.mytmobile.echolocate.lte.delegates

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.tmobile.mytmobile.echolocate.configuration.model.LTE
import com.tmobile.mytmobile.echolocate.lte.utils.ApplicationState
import com.tmobile.mytmobile.echolocate.lte.utils.LTEApplications
import com.tmobile.mytmobile.echolocate.lte.utils.LteConstants
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.utils.SingletonHolder
import java.util.regex.Pattern


/*
This class collects the data such as focus state and actions from NETFLIX application
 */
class NetflixDelegate(context: Context) : BaseStreamDelegate(context) {

    companion object : SingletonHolder<NetflixDelegate, Context>(::NetflixDelegate)

    val FOCUS_GAIN_CODE = 601
    private val FOCUS_LOSS_CODE = 690
    private val SCREEN_OFF_CODE = 695
    val TIMEOUT_ACTION = "NETFLIX_TIMEOUT_ACTION_ECHOLTE"
    internal val STREAMING_START_ACTION = "NETFLIX_STREAMING_START_ACTION_ECHOLTE"
    private val STREAMING_START_TRIGGER_ID = "NETFLIX_STREAMING_START_TRIGGER_ID_ECHOLTE"
    private val STREAMING_START_CODE = 605
    internal val STREAMING_END_ACTION = "NETFLIX_STREAMING_END_ACTION_ECHOLTE"
    private val STREAMING_END_TRIGGER_ID = "NETFLIX_STREAMING_END_TRIGGER_ID_ECHOLTE"
    private val STREAMING_END_CODE = 680
    private val TIMEOUT_REQUEST_CODE = 9879875

    var regexStartStreaming = listOf<String>()
    var regexEndStreaming = listOf<String>()

    val ACTIONS = listOf(STREAMING_START_ACTION, STREAMING_END_ACTION, TIMEOUT_ACTION)

    /*
    Periodic events list
     */
    val STREAM_TEN_SECONDS = 610
    val STREAM_THIRTY_SECONDS = 615
    val STREAM_SIXTY_SECONDS = 620
    val STREAM_THREE_HUNDRED_SECONDS = 625
    val STREAM_SIX_HUNDRED_SECONDS = 630
    val STREAM_EIGHTEEN_HUNDRED_SECONDS = 635


    /**
     * FIND_VIDEO_ID_REGEX
     *
     * NETFLIX content id regex finding in the logcat
     */
    private val FIND_VIDEO_ID_REGEX = "(?<=docid=).+?(?=(&|$))"
    /**
     * FIND_VIDEO_ID_PATTERN
     *
     * pattern for the video id
     */
    private val FIND_VIDEO_ID_PATTERN = Pattern.compile(FIND_VIDEO_ID_REGEX)
    /**
     * LINK_REGEX
     *
     * NETFLIX link regex finding in the logcat
     */
    private val LINK_REGEX = "https://.*"
    /**
     * LINK_PATTERN
     *
     * pattern for the NETFLIX link
     */

    private val LINK_PATTERN = Pattern.compile(LINK_REGEX)

    override fun getFocusGainCode(): Int {
        return FOCUS_GAIN_CODE
    }

    override fun getFocusLossCode(): Int {
        return FOCUS_LOSS_CODE
    }

    override fun getScreenOffCode(): Int {
        return SCREEN_OFF_CODE
    }

    override fun getStreamingStartAction(): String {
        return STREAMING_START_ACTION
    }

    override fun getStreamingStartTriggerId(): String {
        return STREAMING_START_TRIGGER_ID
    }

    override fun getStreamingStartRegex(): List<String> {
        return regexStartStreaming
    }

    override fun getStreamingStartCode(): Int {
        return STREAMING_START_CODE
    }

    override fun getStreamingEndAction(): String {
        return STREAMING_END_ACTION
    }

    override fun getStreamingEndTriggerId(): String {
        return STREAMING_END_TRIGGER_ID
    }

    override fun getStreamingEndRegex(): List<String> {
        return regexEndStreaming
    }

    override fun getStreamingEndCode(): Int {
        return STREAMING_END_CODE
    }

    override fun getTriggerApplication(): LTEApplications {
        return LTEApplications.NETFLIX
    }

    override fun getLogcatListenerIds(): List<String> {
        return listOf(STREAMING_START_TRIGGER_ID, STREAMING_END_TRIGGER_ID)
    }

    override fun getTimeoutAction(): String {
        return TIMEOUT_ACTION
    }

    override fun getTimeoutRequestCode(): Int {
        return TIMEOUT_REQUEST_CODE
    }


    override fun getTenSecondsCode(): Int {
        return STREAM_TEN_SECONDS
    }

    override fun getThirtySecondsCode(): Int {
        return STREAM_THIRTY_SECONDS
    }

    override fun getSixtyecondsCode(): Int {
        return STREAM_SIXTY_SECONDS
    }

    override fun getThreeHundreSecondsCode(): Int {
        return STREAM_THREE_HUNDRED_SECONDS
    }

    override fun getEighteenHundredCode(): Int {
        return STREAM_EIGHTEEN_HUNDRED_SECONDS
    }

    override fun getSixHundredSecondsCode(): Int {
        return STREAM_SIX_HUNDRED_SECONDS
    }

    /**
     * Get netflix content id from intent
     * @param intent: state intent get from the broadcast receiver
     */
    override fun extractContentIdFromIntent(intent: Intent): String {
        val logcatLine = getLogLine(intent)
        if (TextUtils.isEmpty(logcatLine)) {
            return LteConstants.EMPTY
        }
        val matcher = FIND_VIDEO_ID_PATTERN.matcher(logcatLine)
        if (!matcher.find()) {
            return LteConstants.EMPTY
        }
        try {
            return matcher.group()
        } catch (e: IllegalStateException) {
            EchoLocateLog.eLogE("error: ${e.localizedMessage}")
        }

        return LteConstants.EMPTY
    }

    /**
     * Get netflix link from intent
     * @param intent: state intent get from the broadcast receiver
     */
    override fun extractLinkFromLog(intent: Intent): String {
        val log = getLogLine(intent)
        if (TextUtils.isEmpty(log)) {
            return LteConstants.EMPTY
        }
        val matcher = LINK_PATTERN.matcher(log)
        if (!matcher.find()) {
            return LteConstants.EMPTY
        }
        try {
            return matcher.group()
        } catch (e: IllegalStateException) {
            EchoLocateLog.eLogE("error: ${e.localizedMessage}")
        }

        return LteConstants.EMPTY
    }

    override fun getApplicationState(triggerCode: Int): ApplicationState {
        return when (triggerCode) {
            FOCUS_GAIN_CODE -> ApplicationState.FOCUS_GAIN
            FOCUS_LOSS_CODE -> ApplicationState.FOCUS_LOSS
            SCREEN_OFF_CODE -> ApplicationState.SCREEN_OFF
            else -> ApplicationState.PERIODIC
        }
    }

    /**
     * Get current config from Manager to define regex
     * @param lteConfig: get config from configuration module and validate in LteDataManager
     */
    override fun setRegexFromConfig(lteConfig: LTE): BaseDelegate {
        regexStartStreaming = lteConfig.netflixRegex?.regexStartStreaming?: listOf("")
        regexEndStreaming = lteConfig.netflixRegex?.regexEndStreaming?: listOf("")
        return this
    }
}
