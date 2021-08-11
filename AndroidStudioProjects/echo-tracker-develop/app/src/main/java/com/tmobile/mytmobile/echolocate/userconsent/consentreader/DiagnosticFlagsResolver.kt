package com.tmobile.mytmobile.echolocate.userconsent.consentreader

import android.content.ContentResolver
import android.content.Context
import com.tmobile.mytmobile.echolocate.userconsent.userconsentevents.UserConsentFlagsParameters
import com.tmobile.mytmobile.echolocate.userconsent.userconsentevents.UserConsentResponseEvent
import com.tmobile.mytmobile.echolocate.userconsent.utils.UserConsentStringUtils
import com.tmobile.mytmobile.echolocate.userconsent.utils.UserConsentUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import java.lang.Boolean.TRUE


class DiagnosticFlagsResolver(private val context: Context) {

    /**
     * Initializes fetching Boolean flags from TMO app/Android Framework content provider
     * Function used for passing data to ConsentManager
     *
     */
    fun fetchDiagnosticFlags(): UserConsentResponseEvent {
        val contentResolver: ContentResolver = context.contentResolver
        val userConsentFlagsParameters = UserConsentFlagsParameters()
        var srcCmpt = UserConsentStringUtils.SRC_CONSENT_FLAG_CONTENT_RESOLVER

        val cursor = contentResolver.query(UserConsentUtils.contentUri, null, null, null, null)
        if (cursor != null) {
            cursor.run {
                val isMoveToFirst = moveToFirst()
                if (isMoveToFirst) {

                    val columnIndexDeviceDataCollection = getColumnIndex(UserConsentStringUtils.COLUMN1)
                    userConsentFlagsParameters.isAllowedDeviceDataCollection =
                        getString(columnIndexDeviceDataCollection).equals(
                            TRUE.toString(),
                            ignoreCase = true
                        )

                    val columnIndexIssueAssist = getColumnIndex(UserConsentStringUtils.COLUMN2)
                    userConsentFlagsParameters.isAllowedIssueAssist =
                        getString(columnIndexIssueAssist).equals(TRUE.toString(), ignoreCase = true)
                    val columnIndexPersonalizedOffers = getColumnIndex(UserConsentStringUtils.COLUMN3)
                    userConsentFlagsParameters.isAllowedPersonalizedOffers =
                        getString(columnIndexPersonalizedOffers).equals(
                            TRUE.toString(),
                            ignoreCase = true
                        )
                }
                close()
            }

            if (!userConsentFlagsParameters.isAllowedDeviceDataCollection) { // Consent flag is false as reported by content provider
                // To avoid reconfirmation again in case the flag is false,
                // we will add source component for reconfirmation here
                srcCmpt = UserConsentStringUtils.SRC_CONSENT_FLAG_RECONFIRMED
            }
        } else {
            EchoLocateLog.eLogD("ConsentFlags : fetchDiagnosticFlags : cursor is null")
            DiagnosticFlagScheduler(context).schedulerJob()
            srcCmpt = UserConsentStringUtils.SRC_CONSENT_FLAG_DEFAULT
        }
        EchoLocateLog.eLogD(
            "isAllowedDeviceDataCollection = ${userConsentFlagsParameters.isAllowedDeviceDataCollection}",
            System.currentTimeMillis()
        )
        EchoLocateLog.eLogD(
            "isAllowedIssueAssist = ${userConsentFlagsParameters.isAllowedIssueAssist}",
            System.currentTimeMillis()
        )
        EchoLocateLog.eLogD(
            "isAllowedPersonalizedOffers = ${userConsentFlagsParameters.isAllowedPersonalizedOffers}",
            System.currentTimeMillis()
        )

        val userConsentResponseEvent = UserConsentResponseEvent(userConsentFlagsParameters = userConsentFlagsParameters)
        userConsentResponseEvent.sourceComponent = srcCmpt
        return userConsentResponseEvent
    }
}
