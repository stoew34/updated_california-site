package com.tmobile.mytmobile.echolocate.userconsent.database.databasemodel

import kotlinx.serialization.Serializable

@Serializable
data class UserConsentFlagsParametersModel constructor(
        /**
         * This is the boolean value representing consent status for diagnostic agreement at time of event.
         */
        var isAllowedDeviceDataCollection: Boolean = false,
        /**
         * This is the boolean value representing consent status for issue assist agreement at time of event.
         */
        var isAllowedIssueAssist: Boolean = false,
        /***
         * This is the boolean value representing consent status for offers agreement at time of event.
         */
        var isAllowedPersonalizedOffers: Boolean = false
)