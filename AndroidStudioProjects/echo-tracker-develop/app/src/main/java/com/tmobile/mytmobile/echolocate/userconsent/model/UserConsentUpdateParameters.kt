package com.tmobile.mytmobile.echolocate.userconsent.model

import kotlinx.serialization.Serializable

/**
 *
 * This class is for internal use only (Only inside user consent module). The main purpose of this data class is to pass the event from
 * @see [DiagnosticConsentChangedReceiver] to @see[ConsentManager].
 *
 * For sending the user consent flag update to other modules, please use @see [UserConsentFlagsParameters] and @see [UserConsentResponseEvent]
 */
@Serializable
data class UserConsentUpdateParameters constructor(

        /**
         * This is the double value representing the altitude of the location of the device at the time of the event.
         */
        var source: String = "",
        /**
         * This is the double value representing precision of the location altitude of the device at the time of the event.
         */
        var isConsented: Boolean = false,
        /***
         * This is the double value of the user's location altitude at the time of the scan. 'BALANCED_POWER' option may be used to fix the location.
         */
        var name: String = ""
)