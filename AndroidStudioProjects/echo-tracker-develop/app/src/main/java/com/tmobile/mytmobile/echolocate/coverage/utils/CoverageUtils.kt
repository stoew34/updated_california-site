package com.tmobile.mytmobile.echolocate.coverage.utils

import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.text.format.DateUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.tmobile.myaccount.events.diagnostics.pojos.collector.event.ClientSideEvent
import com.tmobile.myaccount.events.diagnostics.pojos.collector.event.eventdata.BaseEventData
import com.tmobile.mytmobile.echolocate.BuildConfig
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.PostTicket
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.coverage.model.CoverageSingleSessionReport

import com.tmobile.mytmobile.echolocate.scheduler.events.ScheduledJobCompletedEvent
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import java.lang.Exception


class CoverageUtils {
    companion object {
        private const val ECHO_LOCATE_COVERAGE_EVENT_TYPE = "diagnosticsapp.network.coverage"

        val EMPTY_STRING = ""

        /**
         * TWENTY_ONE_SECONDS
         */
        const val TWENTY_ONE_SECONDS = 21 * DateUtils.SECOND_IN_MILLIS

        /**
         * Checks for telephony feature.
         *
         * @param context the context
         * @return true, if has
         */
        fun hasTelephonyFeature(context: Context): Boolean {
            return context.packageManager
                .hasSystemFeature(PackageManager.FEATURE_TELEPHONY)
        }

        /**
         * This function will broadcast the job completed status using [ScheduledJobCompletedEvent]
         * If workId is null, it means the job is not requested by scheduler
         */
        fun sendJobCompletedToScheduler(androidWorkId: String?, moduleName: String?) {
            if (!androidWorkId.isNullOrEmpty()) {
                /** Scheduler job is completed */
                val postJobCompletedTicket = PostTicket(
                    ScheduledJobCompletedEvent(androidWorkId!!)
                )
                RxBus.instance.post(postJobCompletedTicket)
                EchoLocateLog.eLogD("Diagnostic : ScheduleWorker job completed for $moduleName : $androidWorkId")
            }
        }

        /**
         * Checking a permission for Location
         */
        fun checkLocationPermission(context: Context): Boolean {
            return (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) &&
                    (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED)
        }

        /**
         * Checking a permission for phone
         */
        fun checkPhonePermission(context: Context): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        }

        /**
         * get nr cell info from Signal strength string passed
         * will return null if CellSignalStrengthNr is not found
         * @param signalStrength
         * @return Nrcell as string
         */
        fun getNrCell(signalStrength: String): String? {
            var cellNr: String? = null
            val regex = "CellSignalStrengthNr((.|\\n)*)\\},"
            val pattern: Pattern = Pattern.compile(regex)
            val matcher: Matcher = pattern.matcher(signalStrength)
            if (matcher.find()) {
                cellNr = matcher.group()
            }
            return cellNr
        }

        fun extractValues(inputString: String?): Map<String, String>? {
            val resultMap: MutableMap<String, String> =
                HashMap()
            val regex = "[\\w]+\\s*=\\s*[-]?[\\d]+"

            "[\\w]+[a-z]\\s*=\\s*[A-Z]+[\\w]+"
            val pattern = Pattern.compile(regex)
            val matcher = pattern.matcher(inputString)
            while (matcher.find()) {
                var result = matcher.group()
                result = result.replace("\\s".toRegex(), "")
                val splitResult =
                    result.split("=")
                resultMap[splitResult[0]] = splitResult[1]
            }
            return resultMap
        }

        fun extractValueNRState(
            key: String,
            inputString: String?
        ): String? {
            val valuesMap: MutableMap<String, String> =
                HashMap()

            val regex = "[\\w]+[a-z]\\s*=\\s*[A-Z]+[\\w]+"
            val pattern = Pattern.compile(regex)
            val matcher = pattern.matcher(inputString)
            while (matcher.find()) {
                var result = matcher.group()
                result = result.replace("\\s".toRegex(), "")
                val splitResult = result.split("=")
                valuesMap[splitResult[0]] = splitResult[1]
            }

            return if (valuesMap.containsKey(key)) {
                valuesMap[key]
            } else ""
        }

        /**
         * Log the crash to firebase crashlytics console with the custom message.
         */
        fun sendCrashReportToFirebase(
            logMessage: String,
            localizedMessage: String?,
            throwableMessage: String
        ) {
            val firebaseCrashlytics = FirebaseCrashlytics.getInstance()
            firebaseCrashlytics.log("$logMessage : $localizedMessage")
            firebaseCrashlytics.recordException(Throwable(Exception(throwableMessage)))
            firebaseCrashlytics.sendUnsentReports()
        }

        fun createClientSideEvent(jsonPayload: String): ClientSideEvent {
            val gson = Gson()
            var clientSideEvent: ClientSideEvent? = null
            val coverageReport =
                gson.fromJson(
                    jsonPayload,
                    CoverageSingleSessionReport::class.java
                ) as BaseEventData
            clientSideEvent = ClientSideEvent().withEventData(coverageReport)
            clientSideEvent.withEventType(ECHO_LOCATE_COVERAGE_EVENT_TYPE)
            clientSideEvent.withClientVersion(BuildConfig.VERSION_NAME)
            clientSideEvent.withTimestamp(Date())
            return clientSideEvent
        }
    }
}