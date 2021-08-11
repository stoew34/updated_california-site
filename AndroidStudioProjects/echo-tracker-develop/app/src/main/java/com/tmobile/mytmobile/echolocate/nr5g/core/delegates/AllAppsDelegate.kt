package com.tmobile.mytmobile.echolocate.nr5g.core.delegates

import android.content.Context
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.SubscribeTicket
import com.tmobile.mytmobile.echolocate.configmanager.ConfigProvider
import com.tmobile.mytmobile.echolocate.configuration.ConfigKey
import com.tmobile.mytmobile.echolocate.configuration.events.configurationevents.Nr5gConfigEvent
import com.tmobile.mytmobile.echolocate.configuration.events.configurationevents.Sa5gConfigEvent
import com.tmobile.mytmobile.echolocate.configuration.model.Nr5g
import com.tmobile.mytmobile.echolocate.configuration.model.Nr5gTriggerControl
import com.tmobile.mytmobile.echolocate.configuration.model.Sa5g
import com.tmobile.mytmobile.echolocate.nr5g.Nr5gModuleProvider
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.ApplicationState
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gConstants
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.BaseNr5gTriggerData
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.utils.SingletonHolder
import io.reactivex.disposables.Disposable

/**
 * Handler class that gets called when the app receives diagandroid.app.ApplicationState
 * broadcast.
 */
class AllAppsDelegate(context: Context) : BaseDelegate(context) {

    private val triggerDataList: MutableList<BaseNr5gTriggerData> = mutableListOf()
    private var m5GConfigUpdateDisposable: Disposable? = null

    init {
        getTriggerCountConfig(context)
        listenFor5GModuleConfigChanges()
    }

    private fun getTriggerCountConfig(context: Context) {
        val configProvider = ConfigProvider.getInstance(context)

        when {

            (Nr5gModuleProvider.getInstance(context).isSa5gSupported) -> {
                EchoLocateLog.eLogD("APP_INTENT_ACTION :: getTriggerCountConfig() :: isSa5gSupported block")
                val sa5g = configProvider.getConfigurationForKey(ConfigKey.SA5G, context) as Sa5g?
                val triggerControl = sa5g?.triggerControl
                addTriggerList(triggerControl)
            }

            (Nr5gModuleProvider.getInstance(context).isNsa5gSupported) -> {
                EchoLocateLog.eLogD("APP_INTENT_ACTION :: getTriggerCountConfig() :: isNsa5gSupported block")
                val nr5g = configProvider.getConfigurationForKey(ConfigKey.NR5G, context) as Nr5g?
                val triggerControl = nr5g?.triggerControl
                addTriggerList(triggerControl)
            }
        }
    }

    companion object : SingletonHolder<AllAppsDelegate, Context>(::AllAppsDelegate) {
        /**
         * FOCUS_GAIN_CODE
         *
         * App focus gain code when application launch
         * value 200
         */
        private const val FOCUS_GAIN_CODE = 200

        /**
         * SCREEN_ON_CODE
         *
         * App focus loss code
         * value 290 not show
         */
        private const val FOCUS_LOSS_CODE = 290
    }

    override fun getTriggerDataList(): List<BaseNr5gTriggerData> {
        return triggerDataList
    }

    /**
     * Function is used to add actions data to the list
     */
    private fun addTriggerList(triggerControl: Nr5gTriggerControl?) {
        if (triggerControl?.triggerPeriodic10s == true) {
            EchoLocateLog.eLogD("APP_INTENT_ACTION :: addTriggerList() :: triggerPeriodic10s enabled")
            triggerDataList.add(
                BaseNr5gTriggerData(
                    TEN_SECONDS,
                    NR5G_TRIGGER_PERIODIC_10,
                    NR5G_TEN_SECONDS_ACTION
                )
            )
        } else {
            EchoLocateLog.eLogD("APP_INTENT_ACTION :: addTriggerList() :: triggerPeriodic10s disabled")
        }

        if (triggerControl?.triggerPeriodic20s == true) {
            EchoLocateLog.eLogD("APP_INTENT_ACTION :: addTriggerList() :: triggerPeriodic20s enabled")
            triggerDataList.add(
                BaseNr5gTriggerData(
                    TWENTY_SECONDS,
                    NR5G_TRIGGER_PERIODIC_20,
                    NR5G_THIRTY_SECONDS_ACTION
                )
            )
        } else {
            EchoLocateLog.eLogD("APP_INTENT_ACTION :: addTriggerList() :: triggerPeriodic20s disabled")
        }
        if (triggerControl?.triggerPeriodic30s == true) {
            EchoLocateLog.eLogD("APP_INTENT_ACTION :: addTriggerList() :: triggerPeriodic30s enabled")
            triggerDataList.add(
                BaseNr5gTriggerData(
                    THIRTY_SECONDS,
                    NR5G_TRIGGER_PERIODIC_30,
                    NR5G_SIXTY_SECONDS_ACTION
                )
            )
        } else {
            EchoLocateLog.eLogD("APP_INTENT_ACTION :: addTriggerList() :: triggerPeriodic30s disabled")
        }
    }

    override fun getFocusGainCode(): Int {
        return FOCUS_GAIN_CODE
    }

    override fun getFocusLossCode(): Int {
        return FOCUS_LOSS_CODE
    }

    /**
     *  Getting Application State gain or focus loss state
     */
    override fun getApplicationState(triggerCode: Int): ApplicationState {
        return when (triggerCode) {
            FOCUS_GAIN_CODE -> ApplicationState.FOCUS_GAIN
            FOCUS_LOSS_CODE -> ApplicationState.FOCUS_LOSS
            else -> ApplicationState.PERIODIC
        }
    }

    override fun getLogcatListenerIds(): List<String> {
        return emptyList()
    }

    override fun getTimeoutAction(): String {
        return Nr5gConstants.EMPTY
    }

    override fun getTimeoutRequestCode(): Int {
        return 0
    }

    /**
     *This fun listens Configuration module and passing new value to fun @runUpdatedConfigForDataCollection
     */
    private fun listenFor5GModuleConfigChanges() {

        val subscribeTicket = SubscribeTicket(RxBus.SubjectType.PUBLISH_SUBJECT)

        m5GConfigUpdateDisposable?.dispose()

        m5GConfigUpdateDisposable = bus.register<Sa5gConfigEvent>(subscribeTicket).subscribe {
            EchoLocateLog.eLogD("APP_INTENT_ACTION :: listenFor5GModuleConfigChanges :: Sa5gConfigEvent received")
            val triggerControl = it?.configValue?.triggerControl
            triggerDataList.clear()
            addTriggerList(triggerControl)
        }

        m5GConfigUpdateDisposable = bus.register<Nr5gConfigEvent>(subscribeTicket).subscribe {
            EchoLocateLog.eLogD("APP_INTENT_ACTION :: listenFor5GModuleConfigChanges() :: Nr5gConfigEvent received")
            val triggerControl = it?.configValue?.triggerControl
            triggerDataList.clear()
            addTriggerList(triggerControl)
        }
    }
}