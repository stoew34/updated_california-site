package com.tmobile.mytmobile.echolocate.userconsent.utils

import android.net.Uri

class UserConsentUtils {

    companion object {
        /**
         * Uri provided by TMO
         * content://com.tmobile.pr.mytmobile.contentprovider/diagnostic_consent
         *
         * Using ContentResolver instance to get data from TMO/Android Framework content provider
         */
        val contentUri =
            Uri.parse("content://${UserConsentStringUtils.AUTHORITY}/diagnostic_consent")

        val RECONFIRM_CONSENT_FLAGS_COMPONENT_NAME = "ConsentFlagRequest"

        /**
         * Array to hold the periodic interval for
         */
        val consentFlagAttemptsDuration: Array<Long> = arrayOf(
            2880,   // 72 hours (48 hours + 24 hours + 15 minutes)
            1440,   // 24 hours (24 hours + 15 minutes)
            15      // 15 minutes
//            25,    // 60 minutes (25 + 20 + 15 minutes)
//            20,    // 35 minutes (20 + 15 minutes)
//            15     // 15 minutes
        )

        // As we are using 0 and -1 for attempt number, we will use -2 as default value
        val CONSENT_ATTEMPT_DEFAULT_UNASSIGNED_VALUE = -2
    }

}