package com.tmobile.mytmobile.echolocate.userconsent

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.tmobile.mytmobile.echolocate.BuildConfig
import com.tmobile.mytmobile.echolocate.userconsent.userconsentevents.UserConsentFlagsParameters
import com.tmobile.mytmobile.echolocate.userconsent.userconsentevents.UserConsentResponseEvent
import com.tmobile.mytmobile.echolocate.userconsent.model.UserConsentUpdateParameters
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import java.util.*

class ConsentRequestProvider private constructor(val context: Context) : ConsentRequestProviderAbstract() {

    private val consentManager: ConsentManager = ConsentManager.getInstance(context)

    companion object {
        @Volatile
        private var INSTANCE: ConsentRequestProvider? = null

        /***
         * creates ConsentRequestProvider instance
         */
        fun getInstance(context: Context): ConsentRequestProvider {
            return INSTANCE ?: synchronized(this) {
                val instance = ConsentRequestProvider(context)
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * implementation for accessing most current user consent flags
     * @return UserConsentResponseEvent
     **/
    override fun getUserConsentFlags(): UserConsentResponseEvent? {
        return consentManager.getConsentFlags()
    }

    /**
     * implementation for update of user consent permissions
     *@return Observable<UserConsentResponseEvent>
     **/

    override fun getUserConsentUpdates(): Observable<UserConsentResponseEvent> {
        return Observable.create { emitter: ObservableEmitter<UserConsentResponseEvent> ->
            consentManager.getUserConsentResponseFlowable().subscribe {
                if (it.isNotEmpty()) {
                    EchoLocateLog.eLogD(
                            "data1_Provider_Flowable_dataCollect = ${it.last().userConsentFlagsParametersModel.isAllowedDeviceDataCollection}",
                            System.currentTimeMillis()
                    )
                    EchoLocateLog.eLogD(
                            "data1_Provider_Flowable_IssueAssist = ${it.last().userConsentFlagsParametersModel.isAllowedIssueAssist}",
                            System.currentTimeMillis()
                    )
                    EchoLocateLog.eLogD(
                            "data1_Provider_Flowable_Offers = ${it.last().userConsentFlagsParametersModel.isAllowedPersonalizedOffers}",
                            System.currentTimeMillis()
                    )
                    EchoLocateLog.eLogD(
                            "data1_Provider_Flowable_timestamp = ${it.last().timeStamp}",
                            System.currentTimeMillis()
                    )
                    EchoLocateLog.eLogD(
                            "data1_Provider_Flowable_sourse = ${it.last().sourceComponent}",
                            System.currentTimeMillis()
                    )
                    emitter.onNext(UserConsentResponseEvent(it.last()))
                }
            }
        }
    }

    /**
     * For testing at playground only
     *Insert a test data for @see [com.tmobile.mytmobile.echolocate.playground.activities.UserConsentActivity]
     **/
    fun insertTestData() {
        if (BuildConfig.DEBUG) {
            val userConsentResponseEvent =
                    UserConsentResponseEvent(
                            userConsentFlagsParameters =
                            UserConsentFlagsParameters(
                                    true,
                                    true,
                                    true
                            )
                    )

            userConsentResponseEvent.timeStamp = Calendar.getInstance().timeInMillis
            userConsentResponseEvent.sourceComponent = "DiagnosticFlagsResolver"

            consentManager.saveFlagstoDB(userConsentResponseEvent)
            consentManager.updateDB(UserConsentUpdateParameters("activity", true, "Device Data Collection"))
//            consentManager.updateDB(UserConsentUpdateParameters("activity", false, "Device Data Collection"))
//            consentManager.updateDB(UserConsentUpdateParameters("activity", false, "Personalized Offers"))
        }
    }

    override fun stopConsentModule() {
        consentManager.unregisterConsentUpdate()
    }

    @VisibleForTesting
    fun resetInstance() {
        synchronized(this) {
            INSTANCE = null
        }
    }
}
