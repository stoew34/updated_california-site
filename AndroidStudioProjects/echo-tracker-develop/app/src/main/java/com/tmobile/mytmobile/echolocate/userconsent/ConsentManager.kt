package com.tmobile.mytmobile.echolocate.userconsent

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticActions
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELAnalyticsEvent
import com.tmobile.mytmobile.echolocate.configuration.events.analytics.ELModulesEnum
import com.tmobile.mytmobile.echolocate.userconsent.userconsentevents.UserConsentResponseEvent
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.PostTicket
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.SubscribeTicket
import com.tmobile.mytmobile.echolocate.userconsent.consentreader.DiagnosticFlagsResolver
import com.tmobile.mytmobile.echolocate.userconsent.database.dao.UserConsentDao
import com.tmobile.mytmobile.echolocate.userconsent.database.databasemodel.UserConsentResponseModel
import com.tmobile.mytmobile.echolocate.userconsent.database.repository.UserConsentRepository
import com.tmobile.mytmobile.echolocate.userconsent.userconsentevents.UserConsentUpdateEvent
import com.tmobile.mytmobile.echolocate.userconsent.model.UserConsentUpdateParameters
import com.tmobile.mytmobile.echolocate.userconsent.utils.UserConsentStringUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class ConsentManager private constructor(private val context: Context) {

    private val bus = RxBus.instance
    private var consentUpdateDisposable: Disposable? = null
    private var countUpdate: Int = 0
    private var countRXevent: Int = 0
    private val userConsentRepository = UserConsentRepository.getInstance(context)


    init {
        /**
         * Register for the flag change event coming from the broadcast receiver. This event is only to be used only
         * inside consent module.
         */
        registerConsentUpdate()
    }

    /**
     * Companion object for other classes to access the instance of [ConsentManager]
     */
    companion object {
        /**
         * ConsentManager instance
         */
        @Volatile
        private var INSTANCE: ConsentManager? = null

        /***
         * creates ConsentManager instance
         */
        fun getInstance(context: Context): ConsentManager {
            return INSTANCE ?: synchronized(this) {
                val instance = ConsentManager(context)
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * For Unit test only @see [com.tmobile.mytmobile.echolocate.userconsent.UserConsentDataFlowTests]
     */
    @VisibleForTesting
    fun setUserConsentDaoForTesting(userConsentDao: UserConsentDao) {
        userConsentRepository.setUserConsentDaoForTesting(userConsentDao)
    }

    /**
     * save Flags requested from DiagnosticFlagsResolver to DB
     *
     * @param event:UserConsentResponseEvent  The user consent response event to be stored in the database.
     */
    fun saveFlagstoDB(event: UserConsentResponseEvent): UserConsentResponseEvent? {
        userConsentRepository.insertUserConsentResponse(event)
        return userConsentRepository.getLatestUserConsentResponse()
    }

    /**
     * update DB with new Flag(s) received from @see [DiagnosticConsentChangedReceiver] by fun @see [registerConsentUpdate]
     * get the latest from DB, replace old Boolean value with new one + add new timestamp and source, save to DB as new
     */
    fun updateDB(params: UserConsentUpdateParameters) {
        lateinit var analyticsEvent: ELAnalyticActions
        //once we get new data in registerConsentUpdate - we run updateDB and send data to db
        var saveData =
            true //var saveData using for define if get correct string from receiver(it could be more than 3 option)
        val userConsentResponseEvent = userConsentRepository.getLatestUserConsentResponse()
        EchoLocateLog.eLogD(
            "updateDBCount = ${countUpdate++}",
            System.currentTimeMillis()
        )

        val userConsentResponseEventTemp: UserConsentResponseEvent
        userConsentResponseEventTemp = if (userConsentResponseEvent == null)
            UserConsentResponseEvent()
        else {
            UserConsentResponseEvent(userConsentFlagsParameters = userConsentResponseEvent.userConsentFlagsParameters)
        }
        when (params.name) {
            UserConsentStringUtils.DEVICE_DATA_COLLECTION -> {
                userConsentResponseEventTemp.userConsentFlagsParameters.isAllowedDeviceDataCollection =
                    params.isConsented
                analyticsEvent = ELAnalyticActions.EL_USER_CONSENT_DATA_COLLECTION
            }
            UserConsentStringUtils.ISSUE_ASSIST -> {
                userConsentResponseEventTemp.userConsentFlagsParameters.isAllowedIssueAssist =
                    params.isConsented
                analyticsEvent = ELAnalyticActions.EL_USER_CONSENT_ISSUE_ASSIST
            }
            UserConsentStringUtils.PERSONALIZED_OFFERS -> {
                userConsentResponseEventTemp.userConsentFlagsParameters.isAllowedPersonalizedOffers =
                    params.isConsented
                saveData = false
            }
            else -> saveData = false
        } // case if received not expected string
        userConsentResponseEventTemp.timeStamp = Calendar.getInstance().timeInMillis
        userConsentResponseEventTemp.sourceComponent = params.source
        if (saveData) {
            EchoLocateLog.eLogD("usercontentData$params.isConsented")
            postAnalyticsEventForUserConsent(params.isConsented.toString(), analyticsEvent)

            saveFlagstoDB(userConsentResponseEventTemp) //insert to db new record
        }
    }

    /**
     * request Flags from DiagnosticFlagsResolver, using this fun in case DB doesn't contain flags
     */
    fun getConsentFlagsFromResolver(): UserConsentResponseEvent {
        val consentFlagResponseEvent = DiagnosticFlagsResolver(context).fetchDiagnosticFlags()

        consentFlagResponseEvent.timeStamp = Calendar.getInstance().timeInMillis

        postAnalyticsEventForUserConsent(
            consentFlagResponseEvent.userConsentFlagsParameters.isAllowedDeviceDataCollection.toString(),
            ELAnalyticActions.EL_USER_CONSENT_DATA_COLLECTION
        )
        postAnalyticsEventForUserConsent(
            consentFlagResponseEvent.userConsentFlagsParameters.isAllowedIssueAssist.toString(),
            ELAnalyticActions.EL_USER_CONSENT_ISSUE_ASSIST
        )
        return consentFlagResponseEvent
    }


    /**
     * get Flags
     * logic: -1- if database contains the flags - > get data from database
     *        -2- if database is empty - > getConsentFlagsFromResolver and -saveFlagstoDB, and then return the response event.
     */
    fun getConsentFlags(): UserConsentResponseEvent? {
        var userConsentResponseEvent: UserConsentResponseEvent? = null
        try {
            userConsentResponseEvent =
                userConsentRepository.getLatestUserConsentResponse() //get latest record from db and if it == null ->start logic 2
            if (userConsentResponseEvent == null) {
                userConsentResponseEvent = saveFlagstoDB(getConsentFlagsFromResolver())
            } else if (!userConsentResponseEvent.userConsentFlagsParameters.isAllowedDeviceDataCollection &&
                (userConsentResponseEvent.sourceComponent == UserConsentStringUtils.SRC_CONSENT_FLAG_DEFAULT ||
                        userConsentResponseEvent.sourceComponent == UserConsentStringUtils.SRC_CONSENT_FLAG_CONTENT_RESOLVER)) {
                val userConsentResponseEventConfirm = DiagnosticFlagsResolver(context).fetchDiagnosticFlags()
                if (userConsentResponseEventConfirm.sourceComponent != UserConsentStringUtils.SRC_CONSENT_FLAG_DEFAULT) {
                    userConsentResponseEventConfirm.timeStamp = System.currentTimeMillis()

                    saveFlagstoDB(userConsentResponseEventConfirm)

                    postAnalyticsEventForUserConsent(
                        userConsentResponseEventConfirm.userConsentFlagsParameters.isAllowedDeviceDataCollection.toString(),
                        ELAnalyticActions.EL_USER_CONSENT_DATA_COLLECTION
                    )
                    postAnalyticsEventForUserConsent(
                        userConsentResponseEventConfirm.userConsentFlagsParameters.isAllowedIssueAssist.toString(),
                        ELAnalyticActions.EL_USER_CONSENT_ISSUE_ASSIST
                    )

                    return userConsentResponseEventConfirm
                }
            }
        } catch (securityException: SecurityException) {
            EchoLocateLog.eLogE("error: ${securityException.localizedMessage}")
        } catch (npeException: NullPointerException) {
            EchoLocateLog.eLogE("error: ${npeException.localizedMessage}")
            // This exception occurs when 2 conditions are met:
            //     1. User clears the data for T-Mobile app.
            //     2. User also cleared the data of Echolocate app
            // In this case, we have to consider that the user consent is revoked.
            userConsentResponseEvent = UserConsentResponseEvent() // Default values are false
        }
        return userConsentResponseEvent  //using in ConsentRequestProvider for getUserConsentFlags
    }

    /**
     * For Unit test only @see [com.tmobile.mytmobile.echolocate.userconsent.UserConsentDataFlowTests]
     *
     * The logic is kept the same to simulate the exact behaviour as in the production code even though it looks crazy and when reading it doesn't make it any sense.
     */
    @VisibleForTesting
    fun getConsentFlags(userConsentResponseEventData: UserConsentResponseEvent): UserConsentResponseEvent? {
        var userConsentResponseEvent: UserConsentResponseEvent? = null
        try {
            userConsentResponseEvent =
                userConsentRepository.getLatestUserConsentResponse() //get latest record from db and if it == null ->start logic 2

            // This is expected here since the unit test doesn't add anything to the database.
            if (userConsentResponseEvent == null) {
                userConsentResponseEvent = saveFlagstoDB(userConsentResponseEventData)
            }
        } catch (securityException: SecurityException) {
            EchoLocateLog.eLogE("error: ${securityException.localizedMessage}")
        }
        return userConsentResponseEvent  //using in ConsentRequestProvider for getUserConsentFlags
    }

    /**
     * The RxBus is a singleton that is accessed through [RxBus.instance] it can
     * be removed by calling [RxBus.destroy] but this is not robust yet and should
     * only be done if you're absolutely sure
     *
     * To register to the RxBus you need to create a [SubscribeTicket] object and pass it into the
     * [RxBus.register] method then you can access your Event as an "it" object via a block declaration
     *
     * To remove an event from the [RxBus] you simply call unregister on the [UnsubscribeTicket]
     * returned by the call to [RxBus.unregister] and the event will not fire anymore
     */

    /**
     *  This function intake the changes in the flags when received by the intent receiver.
     */
    private fun registerConsentUpdate() {
        val subscribeTicket =
            SubscribeTicket(RxBus.SubjectType.PUBLISH_SUBJECT)
        consentUpdateDisposable = bus.register<UserConsentUpdateEvent>(subscribeTicket).subscribe {
            it.userConsentUpdate?.let { params ->

                EchoLocateLog.eLogD(
                    "registerConsentUpdate = ${countRXevent++}",
                    System.currentTimeMillis()
                )

                EchoLocateLog.eLogD(
                    "data1_source_Manager = ${params.source}",
                    System.currentTimeMillis()
                )
                EchoLocateLog.eLogD(
                    "data1_isConsented_Manager = ${params.isConsented}",
                    System.currentTimeMillis()
                )
                EchoLocateLog.eLogD(
                    "data1_name_Manager = ${params.name}",
                    System.currentTimeMillis()
                )
                updateDB(params) //pass data updateDB
            }

        }

    }

    /**
     *  keep this fun just in case we need to do disposable
     */
    fun unregisterConsentUpdate() =
        consentUpdateDisposable?.run {
            bus.unregister(this)
        }

    /**
     *  Passing data from database to @see [ConsentRequestProvider] by Flowable
     */
    fun getUserConsentResponseFlowable(): Flowable<List<UserConsentResponseModel>> {
        return userConsentRepository
            .getUserConsentResponseHistory()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
    }

    @VisibleForTesting
    fun resetInstance() {
        synchronized(this) {
            INSTANCE = null
        }
    }

    /**
     * This function is used to post the user consent configuration event to analytics manager
     * @param status-checks the status of cms config
     * @param payload-stores the status code based on api status
     *
     */
    fun postAnalyticsEventForUserConsent(payload: String, status: ELAnalyticActions) {
        val analyticsEvent = ELAnalyticsEvent(
            ELModulesEnum.USER_CONSENT,
            status,
            payload
        )
        analyticsEvent.timeStamp = System.currentTimeMillis()
        val postAnalyticsTicket = PostTicket(analyticsEvent)
        RxBus.instance.post(postAnalyticsTicket)
    }
}