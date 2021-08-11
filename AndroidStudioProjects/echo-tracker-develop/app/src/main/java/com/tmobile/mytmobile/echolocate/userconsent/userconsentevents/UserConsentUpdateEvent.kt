package com.tmobile.mytmobile.echolocate.userconsent.userconsentevents

import com.tmobile.mytmobile.echolocate.communicationbus.events.EchoLocateBaseEvent
import com.tmobile.mytmobile.echolocate.userconsent.model.UserConsentUpdateParameters

/**
 * class that holds user consent update response parameters.
 */
data class UserConsentUpdateEvent(
        /**
         * class UserConsentUpdateEvent:
         *
         * class that holds user consent update response parameters.
         * Class is used for automatic updates of consent flag changes through
         * the Observer/Subscriber pattern and Rxjava communication bus.
         *
         */
        var userConsentUpdate: UserConsentUpdateParameters? = null
) : EchoLocateBaseEvent() {
    constructor() : this(null)
}