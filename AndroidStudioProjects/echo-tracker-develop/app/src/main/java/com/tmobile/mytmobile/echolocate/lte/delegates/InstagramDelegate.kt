package com.tmobile.mytmobile.echolocate.lte.delegates

import android.content.Context
import com.tmobile.mytmobile.echolocate.configuration.model.LTE
import com.tmobile.mytmobile.echolocate.lte.model.TriggerData
import com.tmobile.mytmobile.echolocate.lte.utils.ApplicationState
import com.tmobile.mytmobile.echolocate.lte.utils.LTEApplications
import com.tmobile.mytmobile.echolocate.utils.SingletonHolder

class InstagramDelegate(context: Context) : BaseNonStreamDelegate(context) {

    private val triggerDataList: List<TriggerData> = listOf(
        TriggerData(10000L, 310, "STREAMING_TEN_SECONDS_ACTION"),
        TriggerData(30000L, 315, "STREAMING_THIRTY_SECONDS_ACTION"),
        TriggerData(60000L, 320, "STREAMING_SIXTY_SECONDS_ACTION")
    )

    override fun getPeriodicTriggers(): List<TriggerData> {
        return triggerDataList
    }

    companion object : SingletonHolder<InstagramDelegate, Context>(::InstagramDelegate)

    private val FOCUS_GAIN_CODE = 301
    private val FOCUS_LOSS_CODE = 390
    private val SCREEN_OFF_CODE = 395
    override fun getTriggerApplication(): LTEApplications {
        return LTEApplications.INSTAGRAM
    }

    override fun getFocusGainCode(): Int {
        return FOCUS_GAIN_CODE
    }

    override fun getFocusLossCode(): Int {
        return FOCUS_LOSS_CODE
    }

    override fun getScreenOffCode(): Int {
        return SCREEN_OFF_CODE
    }

    /**
     * @return BaseDelegate
     */
    override fun setRegexFromConfig(lteConfig: LTE): BaseDelegate? {
        return null
    }

    override fun getApplicationState(triggerCode: Int): ApplicationState {
        when (triggerCode) {
            FOCUS_GAIN_CODE -> return ApplicationState.FOCUS_GAIN
            FOCUS_LOSS_CODE -> return ApplicationState.FOCUS_LOSS
            SCREEN_OFF_CODE -> return ApplicationState.SCREEN_OFF
            else -> return ApplicationState.PERIODIC
        }
    }
}
