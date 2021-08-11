package com.tmobile.mytmobile.echolocate.playground.activities

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tmobile.mytmobile.echolocate.BuildConfig
import com.tmobile.mytmobile.echolocate.R
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.RxBus
import com.tmobile.mytmobile.echolocate.communicationbus.rxbus.SubscribeTicket
import com.tmobile.mytmobile.echolocate.playground.adapter.ReportListAdapter
import com.tmobile.mytmobile.echolocate.playground.viewmodel.*
import com.tmobile.mytmobile.echolocate.reportingmanager.ReportProvider
import com.tmobile.mytmobile.echolocate.reporting.database.ReportSenderDatabaseConstants.REPORT_STATUS_NOT_SENT
import com.tmobile.mytmobile.echolocate.reporting.database.entities.ReportSenderEntity
import com.tmobile.mytmobile.echolocate.reporting.database.repository.ReportSenderRepository
import com.tmobile.mytmobile.echolocate.reporting.reportsender.ReportSentEvent
import com.tmobile.mytmobile.echolocate.userconsent.ConsentRequestProvider
import com.tmobile.mytmobile.echolocate.userconsent.userconsentevents.UserConsentFlagsParameters
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.voice.VoiceModuleProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_dolphin.*
import kotlinx.coroutines.*
import org.json.JSONObject


class DolphinHomeActivity : AppCompatActivity() {

    private var reportSenderRepository: ReportSenderRepository? = null
    private var reportListRecyclerView: RecyclerView? = null
    private var reportListAdapter: ReportListAdapter? = null
    private var alertDialog: AlertDialog? = null
    private var reportStatusDisposable: Disposable? = null

    private val voiceViewModel by lazy {
        ViewModelProviders.of(this).get(VoiceViewModel::class.java)
    }

    private val nr5gViewModel by lazy {
        ViewModelProviders.of(this).get(Nr5gViewModel::class.java)
    }

    private val lteViewModel by lazy {
        ViewModelProviders.of(this).get(LteViewModel::class.java)
    }

    private val coverageViewModel by lazy {
        ViewModelProviders.of(this).get(CoverageViewModel::class.java)
    }

    private val analyticsViewModel by lazy {
        ViewModelProviders.of(this).get(AnalyticsViewModel::class.java)
    }

    @OptIn(InternalCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dolphin)
        tv_show_version.text =  "App version : ${BuildConfig.VERSION_NAME}"
        reportSenderRepository = ReportSenderRepository(this)
        reportListAdapter = ReportListAdapter()
        reportListRecyclerView = list_report_queue

        reportListRecyclerView?.let {
            it.adapter = reportListAdapter
            it.layoutManager = LinearLayoutManager(this)
        }

        alertDialog = AlertDialog.Builder(this).create()
        reportListAdapter?.refreshData(getUpdatedReportList(reportSenderRepository))

        btn_send_dolphin_report.setOnClickListener {
            showProgress(true)
            CoroutineScope(Dispatchers.IO).launch {
//                processDataFromAllModules(it)
                processVoiceData()
                sendReports()
            }
        }

        btn_refresh_dolphin_report.setOnClickListener {
            reportListAdapter?.refreshData(getUpdatedReportList(reportSenderRepository))
        }

        supportActionBar?.let {
            it.setDisplayShowHomeEnabled(true)
            it.setLogo(R.drawable.ic_launcher_dolphin)
            it.setDisplayUseLogoEnabled(true)
        }

        checkDolphinReadiness()
    }

    /**
     * This function will check if the dolphin app is ready to collect the data or no.
     * In case no, it will show the pop up with proper message:
     * 1. No user consent provided
     * 2. If report module is not ready : It will start/restart report module
     * 3. If voice module is not ready : It will start/restart voice module
     *
     * In any case, the app will not close. The purpose of this function is to inform
     * user if data collection or report sending is not happening when the app is launched
     */
    private fun checkDolphinReadiness() {
        if (!checkDiagAppPreloaded()) {
            return
        }

        if (checkConsentFlags()) {
            val rptProvider = ReportProvider.getInstance(applicationContext)
            val voiceModuleProvider = VoiceModuleProvider.getInstance(applicationContext)
            if (rptProvider.isReportingModuleEnabled()) {
                // We will check only voice module for now, as only voice module is enabled for dolphin
                if (!voiceModuleProvider.isVoiceModuleReady()) {
                    EchoLocateLog.eLogI("Dolphin : Diagnostic : Voice module is not ready")
                    showAlertDialog(getString(R.string.voice_module_not_ready))
                    CoroutineScope(Dispatchers.IO).launch {
                        voiceModuleProvider.initVoiceModule(applicationContext)
                    }
                }
            } else {
                EchoLocateLog.eLogI("Dolphin : Diagnostic : Report module is not ready")
                showAlertDialog(getString(R.string.report_module_not_ready))
                // We will check only voice module for now, as only voice module is enabled for dolphin
                CoroutineScope(Dispatchers.IO).launch {
                    rptProvider.initReportingModule()
                    if (!voiceModuleProvider.isVoiceModuleReady()) {
                        EchoLocateLog.eLogI("Dolphin : Diagnostic : Voice module is not ready")
                        voiceModuleProvider.initVoiceModule(applicationContext)
                    }
                }
            }
        }
    }

    /**
     * Check if user accepted or denied the user consent
     * Returns True if user accepted the consent
     * Returns False if user denied the consent
     */
    private fun checkConsentFlags() : Boolean {
        var userConsentFlagsParameters: UserConsentFlagsParameters? =
            ConsentRequestProvider.getInstance(this).getUserConsentFlags()?.userConsentFlagsParameters

        if (userConsentFlagsParameters == null) {
            // Assign default values which are in declined state
            userConsentFlagsParameters = UserConsentFlagsParameters()
        }

        if (!userConsentFlagsParameters.isAllowedDeviceDataCollection) {
            EchoLocateLog.eLogI("Dolphin : Diagnostic : No data collection as the user consent is denied")
            showAlertDialogForConsent(getString(R.string.msg_consent_flag_denied))
            return false
        }

        return true
    }

    private fun checkDiagAppPreloaded() : Boolean {
        try {
            val packageInfo = applicationContext.packageManager.getPackageInfo(packageName, 0)
            if (packageInfo != null &&
                packageInfo.applicationInfo != null &&
                (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0)
            ) {
                showAlertDialog(getString(R.string.msg_diagapp_not_preloaded))
                return false
            }
        } catch (ex: PackageManager.NameNotFoundException) {
            // For exception, we will continue using the app
            EchoLocateLog.eLogE("Dolphin : Diagnostic : Error getting application info to check if DiagApp is preloaded")
        }
        return true
    }

    override fun onPause() {
        super.onPause()
        reportStatusDisposable?.dispose()

    }

    override fun onResume() {
        super.onResume()
        updateReportsStatus()
    }

    private fun showProgress(hideUI: Boolean) {
        if (hideUI) {
            dolphin_progress_bar.visibility = View.VISIBLE
            btn_send_dolphin_report.isEnabled = false
            btn_send_dolphin_report.background = getDrawable(R.drawable.rounded_button_disabled)
            btn_refresh_dolphin_report.isEnabled = false
            btn_refresh_dolphin_report.background = getDrawable(R.drawable.rounded_button_disabled)
        } else {
            dolphin_progress_bar.visibility = View.GONE
            btn_send_dolphin_report.isEnabled = true
            btn_send_dolphin_report.background = getDrawable(R.drawable.rounded_button)
            btn_refresh_dolphin_report.isEnabled = true
            btn_refresh_dolphin_report.background = getDrawable(R.drawable.rounded_button)
        }
    }
    private fun getUpdatedReportList(repository: ReportSenderRepository?): List<ReportSenderEntity> {
        var mReportList: List<ReportSenderEntity> = listOf()
        repository?.getReportsForStatus(REPORT_STATUS_NOT_SENT)?.let { it ->
            mReportList = it
        }
        return mReportList
    }

    /**
     * Listener that gets called when generateAnalyticsReport button is clicked
     * to fetch the Analytics reports generated
     * @param view:View view clicked
     */
    fun generateAnalyticsReport(@Suppress("UNUSED_PARAMETER") view: View?) {

        var analyticsReportDisposable: Disposable? = null
        var analyticsReportObservable = analyticsViewModel.getReports()
        if (analyticsReportObservable != null) {
            analyticsReportDisposable = analyticsReportObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it != null && it.DIAReportResponseParameters.payload.isNotEmpty()) {
                        //etReportView.visibility = View.VISIBLE
                        val jsonObject = JSONObject(it.DIAReportResponseParameters.payload)
                        //etReportView.setText(jsonObject.toString(2))
                    } else {
                        //etReportView.setText("")
                    }
                    analyticsReportDisposable?.dispose()
                    analyticsReportDisposable = null
                    analyticsReportObservable = null
                }, {
                    EchoLocateLog.eLogE("Diagnostic error in get reports: " + it.printStackTrace())
                })
        }
    }

    suspend fun processDataFromAllModules(view: View?) {
        processVoiceData()
        processNr5gData()
        processLteData()
        processCoverageData()
        processAnalyticsData(view)
    }

    suspend fun processVoiceData() {
        withContext(Dispatchers.IO) {
            voiceViewModel.processRAWData()
        }
    }

    suspend fun processNr5gData() {
        withContext(Dispatchers.IO) {
            nr5gViewModel.processRAWData()
        }
    }

    suspend fun processLteData() {
        withContext(Dispatchers.IO) {
            lteViewModel.processRAWData()
        }
    }

    suspend fun processCoverageData() {
        withContext(Dispatchers.IO) {
            coverageViewModel.processRAWData()
        }
    }

    suspend fun processAnalyticsData(view: View?) {
        withContext(Dispatchers.IO) {
            generateAnalyticsReport(view)
        }
    }

    fun sendReports() {
        val rptProvider = ReportProvider.getInstance(applicationContext)
        if (rptProvider.isReportingModuleEnabled()) {
            rptProvider.instantRequestReportsFromAllModules(null)
        } else {
            showAlertDialog(getString(R.string.report_module_not_ready))
        }
    }

    private fun showAlertDialog(message: String) {
        runOnUiThread {
            alertDialog?.run {
                setTitle(R.string.dolphin_popup_title)
                setIcon(R.drawable.ic_launcher_dolphin)
                setMessage(message)
                setCanceledOnTouchOutside(false)
                setOnCancelListener(
                    DialogInterface.OnCancelListener { dialog: DialogInterface ->
                        showProgress(false)
                        reportListAdapter?.refreshData(getUpdatedReportList(reportSenderRepository))
                        dialog.dismiss()
                    }
                )
                setButton(
                    DialogInterface.BUTTON_POSITIVE, getString(R.string.btn_ok),
                    DialogInterface.OnClickListener { dialog: DialogInterface, i: Int ->
                        showProgress(false)
                        reportListAdapter?.refreshData(getUpdatedReportList(reportSenderRepository))
                        dialog.dismiss()
                    }
                )
                if (!isFinishing) {
                    show()
                }
            }
        }
    }

    private fun showAlertDialogForConsent(message: String) {
        runOnUiThread {
            alertDialog?.run {
                setTitle(title)
                setIcon(R.drawable.ic_launcher_dolphin)
                setMessage(message)
                setCanceledOnTouchOutside(false)
                setOnCancelListener(
                    DialogInterface.OnCancelListener { dialog: DialogInterface ->
                        dialog.dismiss()
                        finish()
                    }
                )
                setButton(
                    DialogInterface.BUTTON_POSITIVE, getString(R.string.btn_ok),
                    DialogInterface.OnClickListener { dialog: DialogInterface, i: Int ->
                        dialog.dismiss()
                        finish()
                    }
                )
                setButton(
                    DialogInterface.BUTTON_NEUTRAL, getString(R.string.btn_settings),
                    DialogInterface.OnClickListener { dialog: DialogInterface, i: Int ->
                        try {
                            val consentIntent = Intent("com.carrieriq.tmobile.SUMMARY")
                            startActivity(consentIntent)
                        } catch (ex: Exception) {
                            EchoLocateLog.eLogE("Dolphin : Diagnostic : Error launching UI for the user consent")
                        }
                        dialog.dismiss()
                        finish()
                    }
                )
                if (!isFinishing) {
                    show()
                }
            }
        }
    }

    private fun updateReportsStatus() {
        val subscribeTicket = SubscribeTicket(RxBus.SubjectType.PUBLISH_SUBJECT)
        reportStatusDisposable?.dispose()
        reportStatusDisposable = RxBus.instance.register<ReportSentEvent>(subscribeTicket)
            .subscribe { mReportSentEvent ->

                mReportSentEvent.run {
                    when (numberOfReportsSent) {
                        -1 -> {
                            // No Network
                            showAlertDialog(getString(R.string.no_reports_send_no_network))
                        }
                        0 -> {
                            if (totalNumberOfReports <= 0) {
                                // No Network
                                showAlertDialog(getString(R.string.no_reports_to_send))
                            } else {
                                //None of the reports sent
                                showAlertDialog(getString(R.string.reports_sending_failed))
                            }
                        }
                        else -> {
                            //All or some reports sent successfully
                            showAlertDialog("$numberOfReportsSent out of $totalNumberOfReports report(s) sent successfully")
                        }
                    }
                }
            }
    }
}
