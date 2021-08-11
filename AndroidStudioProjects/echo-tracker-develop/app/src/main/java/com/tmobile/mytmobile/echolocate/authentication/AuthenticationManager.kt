package com.tmobile.mytmobile.echolocate.authentication

import android.content.Context
import android.text.TextUtils
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tmobile.datsdk.DatSdkAgent
import com.tmobile.datsdk.DatSdkAgentImpl
import com.tmobile.datsdk.utils.DatUtils
import com.tmobile.mytmobile.echolocate.authentication.provider.ITokenReceivedListener
import com.tmobile.mytmobile.echolocate.authentication.datevents.DatUpdateEvent
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.PostTicket
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.SubscribeTicket
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.utils.SingletonHolder
import com.tmobile.mytmobile.echolocate.authentication.utils.TokenSharedPreference
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticActions
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticsEvent
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELModulesEnum
import com.tmobile.mytmobile.echolocate.variant.Constants
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * A singleton class responsible to get and refresh the authentication token and save it to database.
 */
class AuthenticationManager(val context: Context) {
    private var authAgent: DatSdkAgent? = null
    private var workIdForScheduledJob: String? = null
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
        TokenSharedPreference.init(context)
        TokenSharedPreference.tokenObject = datJwt
    }

    /**
     * Fetch token from preferences.
     * @return String token
     */
    fun getSavedToken(): String {
        TokenSharedPreference.init(context)
        return TokenSharedPreference.tokenObject.toString()
    }

    /**
     * Initialize [DatSdkAgent] and get DAT silently
     * @param tokenListener
     */
    fun initAgentAndGetDatSilent(tokenListener: ITokenReceivedListener? = null) {
        //TODO try-catch was added to handle crash from ASDK lib, need remove after proper ASDK fix
        try {
            if (authAgent == null) {
                authAgent = DatSdkAgentImpl.getInstance(
                    context,
                    Constants.ENVIRONMENT,
                    TMO_APP_NATIVE,
                    TRANSACTION_ID
                )
                EchoLocateLog.eLogD(
                    "DatSdkAgentImpl.getInstance = $authAgent",
                    System.currentTimeMillis()
                )
            }
            getDatTokenSilent(authAgent!!, tokenListener)
        } catch (e: Exception) {
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.recordException(Throwable("Crash occurred in ASDK version 1.0.8.0 -> ${e.localizedMessage} "))
            crashlytics.sendUnsentReports()
            EchoLocateLog.eLogE("Diagnostic : Crash occurred in ASDK version 1.0.8.0 -> ${e.localizedMessage}")
        }
    }

    /**
     * Get DAT silently from Asdk
     * @param agent [DatSdkAgent]
     * @param tokenListener callback delegate
     */
    private fun getDatTokenSilent(agent: DatSdkAgent, tokenListener: ITokenReceivedListener? = null) {
        EchoLocateLog.eLogD("getDatTokenSilent", System.currentTimeMillis())
        if (tokenDisposable == null || tokenDisposable!!.isDisposed()) {
            tokenDisposable = agent.dat
                .debounce(10, TimeUnit.SECONDS)
                .observeOn(Schedulers.io())
                .subscribe({ datResponse ->
                    val datToken = datResponse.datToken ?: ""
                    EchoLocateLog.eLogD("token = $datToken", System.currentTimeMillis())
                    postDatTokenUpdate(datToken)
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
                    EchoLocateLog.eLogE(" Error while fetching DAT the token ${e.printStackTrace()}")
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

    private fun postDatTokenUpdate(datToken: String) {
        val datUpdateEvent = DatUpdateEvent(datToken)
        var bus = RxBus.instance
        val postTicket = PostTicket(datUpdateEvent)
        bus.post(postTicket)
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

    fun getDatUpdate(): Observable<DatUpdateEvent> {
        val subscribeTicket = SubscribeTicket(RxBus.SubjectType.PUBLISH_SUBJECT)
        return RxBus.instance.register(subscribeTicket)
    }

    /**
     * When Authentication module requests new token, as part of scheduled job,
     * we have to set this value [workIdForScheduledJob], so that the job-completed-status will be informed.
     * If the parameter is null, it means the request is not coming as part of scheduled job, like requesting
     * token from debug UI or from [ReportSender]
     */
    fun setWorkIdForScheduledJob(workId: String?) {
        workIdForScheduledJob = workId
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
