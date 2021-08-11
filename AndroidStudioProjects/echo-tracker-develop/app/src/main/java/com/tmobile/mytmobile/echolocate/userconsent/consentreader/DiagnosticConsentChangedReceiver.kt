package com.tmobile.mytmobile.echolocate.userconsent.consentreader

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.PostTicket
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.userconsent.userconsentevents.UserConsentUpdateEvent
import com.tmobile.mytmobile.echolocate.userconsent.model.UserConsentUpdateParameters
import com.tmobile.mytmobile.echolocate.userconsent.utils.UserConsentStringUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog

/**
 * Broadcast Intent definition:
 * permission - required permission to receive this
 * Intent
 * com.tmobile.pr.mytmobile.permission.RECEIVE_BROADCAST_INTENT
 * action
 * com.tmobile.pr.mytmobile.intent.action.DIAGNOSTIC_CONSENT_CHANGED
 *
 * extras:
 *      com.tmobile.pr.mytmobile.intent.extra.source
 *
 *          -Value is a String that represents the source UI that launched into the flow for Diagnostics Consent (or OOBE).
 *          Possible values:
 *              "Android Settings"
 *              "App Settings"
 *              "Device Help"
 *              "First Launch"
 *          "Not First Launch"
 *          "OOBE""Unknown" - Someone added a new entry point into Diagnostics Consent but forgot to update the code! Bug!
 *
 *      com.tmobile.pr.mytmobile.intent.extra.is_consented value
 *      - is a Boolean to indicate if the consent flag that was changed has been consented by the user or not.
 *
 *      com.tmobile.pr.mytmobile.intent.extra.name
 *      Value is a String to represent the name of the Consent flag that was changed.
 *      Possible values:
 *      "Device Data Collection"
 *      "Issue Assist"
 *      "Personalized Offers"
 *      "Interest Based Ads"
 *      "Unknown" - Someone added a new diagnostic consent flag but forgot to update the code! Bug!
 *
 * This permission is defined in TMO App.
 * External apps must request this permission in order to read the consent flags from TMO App.
 * <uses-permission android:name="com.tmobile.pr.mytmobile.permission.READ_CONTENT_PROVIDER"/>
 */

/**
 * fun shall listen for any changes to the consent flags from the TMO app.
 * The function is invoke when the data is updated in TMO app and passing data to ConsentManager
 */


class DiagnosticConsentChangedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        processReceiverData(intent)
    }

    fun processReceiverData(intent: Intent?) {
        var source = ""
        var isConsented = false
        var name = ""

        intent?.run {
            source = getStringExtra(UserConsentStringUtils.EXTRA_SOURCE) ?: ""
            isConsented = getBooleanExtra(UserConsentStringUtils.EXTRA_IS_CONSENTED, false)
            name = getStringExtra(UserConsentStringUtils.EXTRA_NAME) ?: ""
        }
        val userConsentUpdateParameters = UserConsentUpdateParameters(source, isConsented, name)
        val rxBus = RxBus.instance
        postConsentUpdateEvent(userConsentUpdateParameters, rxBus)

        EchoLocateLog.eLogD(
                "Diagnostic : source_BroadcastReceiver = $source",
                System.currentTimeMillis()
        )
        EchoLocateLog.eLogD(
                "Diagnostic : isConsented_BroadcastReceiver = $isConsented",
                System.currentTimeMillis()
        )
        EchoLocateLog.eLogD(
                "Diagnostic : name_BroadcastReceiver = $name",
                System.currentTimeMillis()
        )
    }

    /**
     * for posting the event using the RxBus
     *
     * @param bus which is an [instance] of RxBus
     * @param userConsentUpdateParameters
     */
    private fun postConsentUpdateEvent(userConsentUpdateParameters: UserConsentUpdateParameters, bus: RxBus) {
        val userConsentUpdateEvent =
            UserConsentUpdateEvent()
        userConsentUpdateEvent.userConsentUpdate = userConsentUpdateParameters
        userConsentUpdateEvent.sourceComponent = UserConsentStringUtils.COMPONENT_NAME
        userConsentUpdateEvent.timeStamp = System.currentTimeMillis()
        val postTicket = PostTicket(userConsentUpdateEvent)
        bus.post(postTicket)
    }
}