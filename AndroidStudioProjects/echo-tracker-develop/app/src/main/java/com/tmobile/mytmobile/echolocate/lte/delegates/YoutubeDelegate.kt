package com.tmobile.mytmobile.echolocate.lte.delegates

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import com.tmobile.mytmobile.echolocate.configuration.model.LTE
import com.tmobile.mytmobile.echolocate.lte.utils.ApplicationState
import com.tmobile.mytmobile.echolocate.lte.utils.ApplicationState.FOCUS_GAIN
import com.tmobile.mytmobile.echolocate.lte.utils.ApplicationState.FOCUS_LOSS
import com.tmobile.mytmobile.echolocate.lte.utils.LTEApplications
import com.tmobile.mytmobile.echolocate.lte.utils.LteConstants
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.utils.SingletonHolder
import java.util.regex.Pattern


/**
 *This class collects the data such as focus state and actions from youtube application
 */
class YoutubeDelegate(context: Context) : BaseStreamDelegate(context) {

    companion object : SingletonHolder<YoutubeDelegate, Context>(::YoutubeDelegate)

    val FOCUS_GAIN_CODE = 501
    private val FOCUS_LOSS_CODE = 590
    private val SCREEN_OFF_CODE = 595
    val TIMEOUT_ACTION = "YOUTUBE_TIMEOUT_ACTION_ECHOLTE"
    internal val STREAMING_START_ACTION = "YOUTUBE_STREAMING_START_ACTION_ECHOLTE"
    internal val STREAMING_END_ACTION = "YOUTUBE_STREAMING_END_ACTION_ECHOLTE"
    private val STREAMING_START_TRIGGER_ID = "YOUTUBE_STREAMING_START_TRIGGER_ID_ECHOLTE"
    private val STREAMING_START_CODE = 505
    private val STREAMING_END_TRIGGER_ID = "YOUTUBE_STREAMING_END_TRIGGER_ID_ECHOLTE"
    private val STREAMING_END_CODE = 580
    private val TIMEOUT_REQUEST_CODE = 9879875
    @Volatile private var isStartStreamReceived = false

    lateinit var regexStartStreaming: List<String>
    lateinit var regexEndStreaming: List<String>

    /** Intent Actions supported by this delegate*/
    val ACTIONS = listOf(STREAMING_START_ACTION, STREAMING_END_ACTION, TIMEOUT_ACTION)

    /** Periodic events list*/
    val STREAM_TEN_SECONDS = 510
    val STREAM_THIRTY_SECONDS = 515
    val STREAM_SIXTY_SECONDS = 520
    val STREAM_THREE_HUNDRED_SECONDS = 525
    val STREAM_SIX_HUNDRED_SECONDS = 530
    val STREAM_EIGHTEEN_HUNDRED_SECONDS = 535

    /**
     * FIND_VIDEO_ID_REGEX
     *
     * youtube content id regex finding in the logcat
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
     * youtube link regex finding in the logcat
     */
    private val LINK_REGEX = "https://.*"
    /**
     * LINK_PATTERN
     *
     * pattern for the youtube link
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
        return LTEApplications.YOUTUBE
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
     * This Function handles the focus states [FOCUS_GAIN] and [FOCUS_LOSS] of an application received from lte manager
     */
    override fun processApplicationState(state: ApplicationState) {
            if (state == FOCUS_LOSS) {
                isStartStreamReceived = false
        }
        super.processApplicationState(state)
    }

    /**
     * This functions process the intents which received through rxbus such [YoutubeDelegate.STREAMING_START_ACTION] and [NetflixDelegate.STREAMING_START_ACTION] and lte manager
     */
    override fun handleIntent(intent: Intent?) {
            EchoLocateLog.eLogV("Diagnostic : CMS Limit-----handle intent called" + intent?.action)
            when {
                TextUtils.isEmpty(intent?.action) -> EchoLocateLog.eLogV("Diagnostic : Empty intent")

                Intent.ACTION_SCREEN_OFF == intent?.action -> if (stateFocused) {
                    listenApplicationTriggerLimitReachedEvent()
                    processScreenOffEvent()
                }

                getStreamingStartAction() == intent?.action ->
                    synchronized (isStartStreamReceived) {
                        if (!isStartStreamReceived) {
                            isStartStreamReceived = true
                            processStreamingStartAction(intent)
                        }
                    }

                getStreamingEndAction() == intent?.action -> {
                    isStartStreamReceived = false
                    processStreamingEndAction(intent)
                }

                getTimeoutAction() == intent?.action -> {
                    cancelAll()
                }
            }
    }

    /**
     * Get youtube content id from intent
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
     * Get youtube link from intent
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
            FOCUS_GAIN_CODE -> FOCUS_GAIN
            FOCUS_LOSS_CODE -> FOCUS_LOSS
            SCREEN_OFF_CODE -> ApplicationState.SCREEN_OFF
            else -> ApplicationState.PERIODIC
        }
    }

    /**
     * Get current config from Manager to define regex
     * @param lteConfig: get config from configuration module and validate in LteDataManager
     */
    override fun setRegexFromConfig(lteConfig: LTE): BaseDelegate {
            regexStartStreaming = lteConfig.youtubeRegex?.regexStartStreaming?: listOf("")
            regexEndStreaming = lteConfig.youtubeRegex?.regexEndStreaming?: listOf("")
        return this
    }
}