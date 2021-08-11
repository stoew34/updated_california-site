package com.tmobile.mytmobile.echolocate.lte.delegates

import android.content.Context
import com.tmobile.mytmobile.echolocate.configuration.model.LTE
import com.tmobile.mytmobile.echolocate.lte.model.TriggerData
import com.tmobile.mytmobile.echolocate.lte.utils.ApplicationState
import com.tmobile.mytmobile.echolocate.lte.utils.LTEApplications
import com.tmobile.mytmobile.echolocate.utils.SingletonHolder

class YouTubeTvDelegate(context: Context) : BaseNonStreamDelegate(context) {

    companion object : SingletonHolder<YouTubeTvDelegate, Context>(::YouTubeTvDelegate)


    /**
     * FOCUS_GAIN_CODE
     *
     * youtube tv focus gain code
     * value 701
     */
    val FOCUS_GAIN_CODE = 701
    /**
     * FOCUS_LOSS_CODE
     *
     * youtube tv focus loss code
     * value 790
     */
    val FOCUS_LOSS_CODE = 790
    /**
     * SCREEN_OFF_CODE
     *
     * youtube tv screen off code
     * value 795
     */
    val SCREEN_OFF_CODE = 795
    /**
     * Periodic events list
     *
     * YOUTUBE_TV_FOCUS_TEN_SECONDS
     * youtube tv ten seconds code
     * value 710
     */
    val YOUTUBE_TV_FOCUS_TEN_SECONDS = 710
    /**
     * YOUTUBE_TV_FOCUS_THIRTY_SECONDS
     *
     * youtube tv thirty seconds code
     * value 715
     */
    val YOUTUBE_TV_FOCUS_THIRTY_SECONDS = 715
    /**
     * YOUTUBE_TV_FOCUS_SIXTY_SECONDS
     *
     * youtube tv sixty seconds code
     * value 720
     */
    val YOUTUBE_TV_FOCUS_SIXTY_SECONDS = 720
    /**
     * YOUTUBE_TV_FOCUS_THREE_HUNDRED_SECONDS
     *
     * youtube tv three hundred seconds code
     * value 720
     */
    val YOUTUBE_TV_FOCUS_THREE_HUNDRED_SECONDS = 725
    /**
     * YOUTUBE_TV_FOCUS_SIX_HUNDRED_SECONDS
     *
     * youtube tv six hundred seconds code
     * value 730
     */
    val YOUTUBE_TV_FOCUS_SIX_HUNDRED_SECONDS = 730

    /**
     * prepares the list with the timeouts specified
     */
    private val triggerDataList: List<TriggerData> = listOf(
        TriggerData(10000L, YOUTUBE_TV_FOCUS_TEN_SECONDS, "STREAMING_TEN_SECONDS_ACTION"),
        TriggerData(30000L, YOUTUBE_TV_FOCUS_THIRTY_SECONDS, "STREAMING_THIRTY_SECONDS_ACTION"),
        TriggerData(60000L, YOUTUBE_TV_FOCUS_SIXTY_SECONDS, "STREAMING_SIXTY_SECONDS_ACTION"),
        TriggerData(300000L, YOUTUBE_TV_FOCUS_THREE_HUNDRED_SECONDS, "STREAMING_THREE_HUNDRED_SECONDS_ACTION"),
        TriggerData(600000L, YOUTUBE_TV_FOCUS_SIX_HUNDRED_SECONDS, "STREAMING_SIX_HUNDRED_SECONDS_ACTION")
    )


    /**
     * returns the generated trigger list
     * @return List<TriggerData>
     */
    override fun getPeriodicTriggers(): List<TriggerData> {
        return triggerDataList
    }

    /**
     * returns the triggered application
     * @return LTEApplications
     */
    override fun getTriggerApplication(): LTEApplications {
        return LTEApplications.YOUTUBE_TV
    }

    /**
     * gets the focus gain code for youtube tv
     * @return returns the focus gain code
     */
    override fun getFocusGainCode(): Int {
        return FOCUS_GAIN_CODE
    }

    /**
     * gets the focus loss code for youtube tv
     * @return returns the focus loss code
     */
    override fun getFocusLossCode(): Int {
        return FOCUS_LOSS_CODE
    }

    /**
     * gets the screen off code for youtube tv
     * @return returns the screen off code
     */
    override fun getScreenOffCode(): Int {
        return SCREEN_OFF_CODE
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
     * @return BaseDelegate
     */
    override fun setRegexFromConfig(lteConfig: LTE): BaseDelegate? {
        return null
    }
}