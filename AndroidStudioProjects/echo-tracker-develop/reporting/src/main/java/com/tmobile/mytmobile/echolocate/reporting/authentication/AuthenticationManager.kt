package com.tmobile.mytmobile.echolocate.reporting.authentication

import android.content.Context
import android.text.TextUtils
import androidx.annotation.VisibleForTesting
import com.tmobile.datsdk.BuildConfig
import com.tmobile.datsdk.DatSdkAgent
import com.tmobile.datsdk.DatSdkAgentImpl
import com.tmobile.datsdk.utils.DatUtils
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.PostTicket
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.reporting.utils.ReportingLog
import com.tmobile.mytmobile.echolocate.reporting.utils.SingletonHolder
import com.tmobile.mytmobile.echolocate.reporting.utils.ReportingModuleSharedPrefs
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticActions
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticsEvent
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELModulesEnum
import com.tmobile.mytmobile.echolocate.configuration.events.NonFatalCrashEvent
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * A singleton class responsible to get and refresh the authentication token and save it to database.
 */
class AuthenticationManager(val context: Context) {
    private var tokenDisposable: Disposable? = null

    companion object : SingletonHolder<AuthenticationManager, Context>(::AuthenticationManager) {
        private const val TMO_APP_NATIVE = "DIAApp"
        private val TRANSACTION_ID = UUID.randomUUID().toString()
        private const val REQUEST_RESULT_FAIL_NULL = "true null"
        private const val REQUEST_RESULT_FAIL = "true "
        private const val REQUEST_RESULT_SUCCESS = "false "
    }

    /**
     * Saves token in preferences.
     * @param datJwt
     */
    private fun saveToken(datJwt: String) {
        ReportingModuleSharedPrefs.tokenObject = datJwt
    }

    /**
     * Saves token in preferences.
     * @param datJwt
     */
    @VisibleForTesting
    fun saveTokenTest(datJwt: String) {
        saveToken(datJwt)
    }

    /**
     * Fetch token from preferences.
     * @return String token
     */
    fun getSavedToken(): String {
        return ReportingModuleSharedPrefs.tokenObject.toString()
    }

    /**
     * Initialize [DatSdkAgent] and get DAT silently
     * @param tokenListener
     */
    fun initAgentAndGetDatSilent(tokenListener: ITokenReceivedListener? = null) {
        //TODO try-catch was added to handle crash from ASDK lib, need remove after proper ASDK fix
        var authAgent: DatSdkAgent? = null
        try {
            if (authAgent == null) {
                authAgent = DatSdkAgentImpl.getInstance(
                    context,
                    ReportingModuleSharedPrefs.clientAppEnvironment,
                    TMO_APP_NATIVE,
                    TRANSACTION_ID
                )
                ReportingLog.eLogD(
                    "DatSdkAgentImpl.getInstance = $authAgent",
                    System.currentTimeMillis(), context
                )
            }
            getDatTokenSilent(authAgent!!, tokenListener)
        } catch (exception: Exception) {
            val logMessage = "exception  occured in ${AuthenticationManager} with ASDK version ${BuildConfig.VERSION_NAME}"
            val exceptionDetails = "$logMessage at $exception.localizedMessage"
            var nonFatalCrashEvent = NonFatalCrashEvent(logMessage, exceptionDetails)
            var bus = RxBus.instance
            bus.post( PostTicket(nonFatalCrashEvent))
        }
    }

    /**
     * Get DAT silently from Asdk
     * @param agent [DatSdkAgent]
     * @param tokenListener callback delegate
     */
    private fun getDatTokenSilent(agent: DatSdkAgent, tokenListener: ITokenReceivedListener? = null) {
        ReportingLog.eLogD("getDatTokenSilent", System.currentTimeMillis(), context)
        if (tokenDisposable == null || tokenDisposable!!.isDisposed()) {
            tokenDisposable = agent.dat
                .debounce(10, TimeUnit.SECONDS)
                .observeOn(Schedulers.io())
                .subscribe({ datResponse ->
                    val datToken = datResponse.datToken ?: ""
                    ReportingLog.eLogD("token = $datToken", System.currentTimeMillis(), context)
                    saveToken(datToken)
                    tokenListener?.onReceivedToken(datToken)
                    tokenDisposable?.dispose()

                    /** Get result dat token received and sent to analytics module*/
                    if (datToken.isBlank()) {
                        postAnalyticsEventForDatTokenResult(
                            REQUEST_RESULT_FAIL_NULL,
                            ELAnalyticActions.EL_AUTHENTICATION_FAIL
                        )
                    } else {
                        postAnalyticsEventForDatTokenResult(
                            REQUEST_RESULT_SUCCESS,
                            ELAnalyticActions.EL_AUTHENTICATION_FAIL
                        )
                    }
                }, { e ->
                    ReportingLog.eLogE(" Error while fetching DAT the token ${e.printStackTrace()}")
                    postAnalyticsEventForDatTokenResult(
                        REQUEST_RESULT_FAIL + e.message.toString(),
                        ELAnalyticActions.EL_AUTHENTICATION_FAIL
                    )
                    tokenDisposable?.dispose()
                }, {
                    tokenDisposable?.dispose()
                })
        }
    }

    /**
     * Function checks the expiry status of DAT Token
     * @param datJwt DAT token to be passed
     * @return expiry status of DAT token.
     */
    fun checkIfTokenExpired(datJwt: String): Boolean {
        return if (!TextUtils.isEmpty(datJwt)) {
            DatUtils.isDATLessThanHour(datJwt)
        } else {
            true
        }
    }

    /**
     * API to check the validity/expiration of token stored locally.
     * @return isTokenExpired
     */
    fun isLocallyStoredTokenExpired(): Boolean {
        val authenticationManager = AuthenticationManager.getInstance(context)
        val datToken = authenticationManager.getSavedToken()
        if (!TextUtils.isEmpty(datToken)) {
            return checkIfTokenExpired(datToken)
        }
        return true
    }

    /**
     * This function is used to post new event to analytics manager
     * @param status-checks the status of cms config
     * @param payload-stores the status code based on api status
     */
    private fun postAnalyticsEventForDatTokenResult(payload: String, status: ELAnalyticActions) {
        val analyticsEvent = ELAnalyticsEvent(
            moduleName = ELModulesEnum.AUTH_TOKENS,
            action = status,
            payload = payload
        )
        analyticsEvent.timeStamp = System.currentTimeMillis()

        val postAnalyticsTicket = PostTicket(analyticsEvent)
        RxBus.instance.post(postAnalyticsTicket)
    }
}
