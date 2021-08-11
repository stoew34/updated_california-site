package com.tmobile.mytmobile.echolocate.lte.delegates

import android.content.Context
import com.tmobile.mytmobile.echolocate.configuration.model.LTE
import com.tmobile.mytmobile.echolocate.lte.model.TriggerData
import com.tmobile.mytmobile.echolocate.lte.utils.ApplicationState
import com.tmobile.mytmobile.echolocate.lte.utils.LTEApplications
import com.tmobile.mytmobile.echolocate.utils.SingletonHolder

/**
 * Delegate for Facebook application events.
 */
class FacebookDelegate(context: Context) : BaseNonStreamDelegate(context) {

    private val triggerDataList: List<TriggerData> = listOf(
        TriggerData(10000L, 210, "STREAMING_TEN_SECONDS_ACTION"),
        TriggerData(30000L, 215, "STREAMING_THIRTY_SECONDS_ACTION"),
        TriggerData(60000L, 220, "STREAMING_SIXTY_SECONDS_ACTION")
    )

    override fun getPeriodicTriggers(): List<TriggerData> {
        return triggerDataList
    }

    companion object : SingletonHolder<FacebookDelegate, Context>(::FacebookDelegate)

    override fun getFocusGainCode(): Int {
        return FOCUS_GAIN_CODE
    }

    override fun getFocusLossCode(): Int {
        return FOCUS_LOSS_CODE
    }

    override fun getScreenOffCode(): Int {
        return SCREEN_OFF_CODE
    }

    override fun getTriggerApplication(): LTEApplications {
        return LTEApplications.FACEBOOK
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

    /**
     * FOCUS_GAIN_CODE
     *
     * Facebook focus gain code when application launch
     * value 201
     */
    private val FOCUS_GAIN_CODE = 201
    /**
     * FOCUS_LOSS_CODE
     *
     * Facebook focus loss code
     * value 290
     */
    private val FOCUS_LOSS_CODE = 290
    /**
     * SCREEN_OFF_CODE
     *
     * Facebook screen off code
     * value 295
     */
    private val SCREEN_OFF_CODE = 295


}
