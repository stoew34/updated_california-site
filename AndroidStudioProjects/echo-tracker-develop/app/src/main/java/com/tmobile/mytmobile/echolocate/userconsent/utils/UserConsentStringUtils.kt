package com.tmobile.mytmobile.echolocate.userconsent.utils

object UserConsentStringUtils {
    /**
     * Source component name of the user consent module
     */
    const val COMPONENT_NAME: String = "UserConsentModule"

    const val ECHOLOCATE_USER_CONSENT_DB_NAME = "echolocate_user_consent_db"

    const val USER_CONSENT_TABLE_NAME = "user_consent_table"
    /**
     * Value is a String that represents the source UI that launched into the flow for Diagnostics Consent (or OOBE). Possible values:
     *    "Android Settings"
     *    "App Settings"
     *    "Device Help"
     *    "First Launch"
     *    "Not First Launch"
     *    "OOBE"
     *    "Unknown" - Someone added a new entry point into Diagnostics Consent but forgot to update the code! Bug!
     */
    const val EXTRA_SOURCE = "com.tmobile.pr.mytmobile.intent.extra.source"

    /**
     * Value is a String to represent the name of the Consent flag that was changed. Possible values:
     *    "Device Data Collection"
     *    "Issue Assist"
     *    "Personalized Offers"
     *    "Interest Based Ads"
     *    "Unknown" - Someone added a new diagnostic consent flag but forgot to update the code! Bug!
     */
    const val EXTRA_NAME = "com.tmobile.pr.mytmobile.intent.extra.name"

    /**
     * Value is a Boolean to indicate if the consent flag that was changed has been consented by the user or not.
     */
    const val EXTRA_IS_CONSENTED = "com.tmobile.pr.mytmobile.intent.extra.is_consented"

    const val DEVICE_DATA_COLLECTION = "Device Data Collection"

    const val ISSUE_ASSIST = "Issue Assist"

    const val PERSONALIZED_OFFERS = "Personalized Offers"

    const val AUTHORITY: String = "com.tmobile.pr.mytmobile.contentprovider"

    const val COLUMN1: String = "is_allowed_device_data_collection"
    const val COLUMN2: String = "is_allowed_issue_assist"
    const val COLUMN3: String = "is_allowed_personalized_offers"

    /**
     * The default name of source component if the values are not received from Content Provider
     */
    const val SRC_CONSENT_FLAG_DEFAULT: String = "Default"

    /**
     * The name of the source component if the values are received from Content Provider
     */
    const val SRC_CONSENT_FLAG_CONTENT_RESOLVER: String = "DiagnosticFlagsResolver"

    /**
     * The name of the source component if the values are received from Content Provider as part of reconfirmation
     */
    const val SRC_CONSENT_FLAG_RECONFIRMED: String = "DiagnosticFlagsReconfirmed"

}