package com.tmobile.mytmobile.echolocate.utils

import com.google.firebase.crashlytics.FirebaseCrashlytics

object FirebaseUtils {

    /**
     * Log the crash to firebase crashlytics console with the custom message.
     */
     fun logCrashToFirebase(logMessage: String, localizedMessage :String?, throwableMessage: String) {
        val firebaseCrashlytics = FirebaseCrashlytics.getInstance()
        firebaseCrashlytics.log("$logMessage : $localizedMessage")
        firebaseCrashlytics.recordException(Throwable(Exception(throwableMessage)))
        firebaseCrashlytics.sendUnsentReports()
    }
}