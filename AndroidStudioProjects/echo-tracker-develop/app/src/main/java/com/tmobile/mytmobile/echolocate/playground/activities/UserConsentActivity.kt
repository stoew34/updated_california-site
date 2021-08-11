package com.tmobile.mytmobile.echolocate.playground.activities

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.tmobile.mytmobile.echolocate.R
import com.tmobile.mytmobile.echolocate.userconsent.ConsentRequestProvider

class UserConsentActivity : AppCompatActivity() {

    private var getUserConsentFlagsBtn: Button? = null
    private var getUserConsentUpdatesBtn: Button? = null
    private var getTestBtn: Button? = null
    private var DiagnosticAgreedCbox: CheckBox? = null
    private var IssueAssistCbox: CheckBox? = null
    private var OffersCbox: CheckBox? = null

    var consentRequestProvider: ConsentRequestProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userconsent)

        consentRequestProvider = ConsentRequestProvider.getInstance(this)
        initViews()
        setListenersForViews()
        setListenersForUpdate()
        insertTestData()
    }

    fun initViews() {
        getUserConsentFlagsBtn = findViewById(R.id.btnGetUserConsentFlags)
        getUserConsentUpdatesBtn = findViewById(R.id.btngetUserConsentUpdates)
        getTestBtn = findViewById(R.id.btnTest)

        DiagnosticAgreedCbox = findViewById(R.id.ckDiagnosticAgreed)
        IssueAssistCbox = findViewById(R.id.ckIssueAssist)
        OffersCbox = findViewById(R.id.ckOffers)
    }

    fun setListenersForViews() {

        /**
         * Start testing with this button, at very first run it allow to test Resolver
         * get Flags
         * logic: -1- if database contains the flags - > get data from database
         *        -2- if database is empty - > getConsentFlagsFromResolver
         */
        getUserConsentFlagsBtn!!.setOnClickListener {

            val diagnosticFlags = consentRequestProvider?.getUserConsentFlags()

            diagnosticFlags?.userConsentFlagsParameters?.let { userConsentFlagsParameters ->
                val isAllowedDeviceDataCollection = userConsentFlagsParameters.isAllowedDeviceDataCollection
                val isAllowedIssueAssist = userConsentFlagsParameters.isAllowedIssueAssist
                val isAllowedPersonalizedOffers = userConsentFlagsParameters.isAllowedPersonalizedOffers

                DiagnosticAgreedCbox?.isChecked = isAllowedDeviceDataCollection
                IssueAssistCbox?.isChecked = isAllowedIssueAssist
                OffersCbox?.isChecked = isAllowedPersonalizedOffers

                Toast.makeText(
                        applicationContext,
                        "$isAllowedDeviceDataCollection + $isAllowedIssueAssist + $isAllowedPersonalizedOffers",
                        Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Continue testing with this button, after any change in database
     * (and framework setting as well) it should reflect this changes
     *this flowable implementatiom
     */
    fun setListenersForUpdate() {
        getUserConsentUpdatesBtn!!.setOnClickListener {
            consentRequestProvider?.getUserConsentUpdates()?.subscribe {

                val isAllowedDeviceDataCollection = it.userConsentFlagsParameters.isAllowedDeviceDataCollection
                val isAllowedIssueAssist = it.userConsentFlagsParameters.isAllowedIssueAssist
                val isAllowedPersonalizedOffers = it.userConsentFlagsParameters.isAllowedPersonalizedOffers

                DiagnosticAgreedCbox?.isChecked = isAllowedDeviceDataCollection
                IssueAssistCbox?.isChecked = isAllowedIssueAssist
                OffersCbox?.isChecked = isAllowedPersonalizedOffers
            }
        }
    }

    fun insertTestData() {
        getTestBtn!!.setOnClickListener {
            consentRequestProvider?.insertTestData()
        }
    }
}