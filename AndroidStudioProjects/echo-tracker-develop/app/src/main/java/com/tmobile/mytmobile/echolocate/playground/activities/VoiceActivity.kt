package com.tmobile.mytmobile.echolocate.playground.activities

import android.app.ProgressDialog
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.tmobile.mytmobile.echolocate.BuildConfig
import com.tmobile.mytmobile.echolocate.R
import com.tmobile.mytmobile.echolocate.playground.viewmodel.VoiceViewModel
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import com.tmobile.mytmobile.echolocate.voice.repository.database.entity.*
import com.tmobile.mytmobile.echolocate.voice.repository.VoiceRepository
import com.tmobile.mytmobile.echolocate.voice.model.BaseVoiceData
import com.tmobile.mytmobile.echolocate.voice.model.NetworkIdentity
import com.tmobile.mytmobile.echolocate.voice.reportprocessor.VoiceDataStatus
import com.tmobile.mytmobile.echolocate.voice.utils.VoiceEntityConverter
import com.tmobile.mytmobile.echolocate.voice.utils.VoiceOEMSoftwareVersionCollector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*
import java.util.concurrent.Executors


/**
 * This Activity uses the VoiceModule API's defined to display the voice reports on the playground
 */
class VoiceActivity : AppCompatActivity() {

    private lateinit var etToDate: EditText
    private lateinit var etFromDate: EditText
    private lateinit var etReportView: EditText
    private lateinit var voiceRepository: VoiceRepository
    private lateinit var prgDlg: ProgressDialog


    private val viewModel by lazy {
        ViewModelProviders.of(this).get(VoiceViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice)
        voiceRepository = VoiceRepository(this)
        initViews()
        etReportView.movementMethod = ScrollingMovementMethod()

    }

    /**
     * Initialization section of views used in the activity
     */
    private fun initViews() {
        etFromDate = findViewById(R.id.et_fromdate)
        etToDate = findViewById(R.id.et_fromdate)
        etReportView = findViewById(R.id.et_reports)

    }

    fun processRAWData(@Suppress("UNUSED_PARAMETER")view: View?) {
        GlobalScope.launch(Dispatchers.IO) {
            viewModel.processRAWData()
        }
    }

    /**
     * Listener that gets called when getAllReports button is clicked
     * to fetch the voice reports generated between current time and the last 7 days
     * @param view:View view clicked
     */
    fun getAllReports(@Suppress("UNUSED_PARAMETER")view: View?) {

        viewModel.getReports("", "").observe(this, Observer {
            if (it != null && (it as String).isNotEmpty()) {
                etReportView.visibility = View.VISIBLE
                //  etReportView.setText(it as String)
                val jsonObject = JSONObject(it)
                etReportView.setText(jsonObject.toString(2))
            }else{
                Toast.makeText(applicationContext,"Report EMPTY", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * Listener that gets called when getReports button is clicked
     * to fetch the voice reports generated for the given date range
     * @param view:View view clicked
     */
    fun getReportInDateRange(@Suppress("UNUSED_PARAMETER")view: View?) {
        val startTime = etFromDate.text.toString()
        val endTime = etToDate.text.toString()

        if (etFromDate.text.toString().isEmpty() || etToDate.text.toString().isEmpty()) {
            etFromDate.error = resources.getString(R.string.field_non_empty_msg)
        } else {
            if (!EchoLocateDateUtils.isDateParsable(startTime) || !EchoLocateDateUtils.isDateParsable(endTime)) {
                etFromDate.error = resources.getString(R.string.date_parse_msg)
            } else {
                viewModel.getReports(startTime, endTime).observe(this, Observer {
                    if (it != null) {
                        etReportView.visibility = View.VISIBLE
                        // etReportView.setText(it as String)
                        val jsonObject = JSONObject(it as String)
                        etReportView.setText(jsonObject.toString(2))

                    }
                })
            }
        }
    }

    fun generateDummyData(view: View?) {
        showProgress(true)
        Executors.newSingleThreadExecutor().execute {
            insertDummyVoiceData()
        }
    }

    fun showProgress(fShow: Boolean) {
        runOnUiThread {
            if (fShow) {
                prgDlg = ProgressDialog.show(this, "Please wait", "Generating dummy records", true)
            } else {
                prgDlg.dismiss()
            }
        }
    }

    private fun insertDummyVoiceData() {
        var callStartTime = EchoLocateDateUtils.getCurrentTimeInMillis() - (3 * 60 * 60 * 1000)

        for (callNum in 0..2) {
            callStartTime += 60000

            // Insert Base call entity
            insertBaseCallEntity(callStartTime)

            // Insert OEM Software Version
            insertOEMSwVersionEntity(callStartTime)

            // Insert app trigger intent
            insertAppTriggerCallEntity(callStartTime)

            // Insert app setting data
            insertCallSettingDataEntity(callStartTime)

            // Insert Detailed call states
            insertDetailedCallStates(callStartTime)

            // Insert UI call states
            insertUICallStates(callStartTime)

            // Insert IMS signalling data

        }

        runOnUiThread {
            showProgress(false)
        }
    }

    private fun insertBaseCallEntity(callId : Long) {
        val baseEchoLocateVoiceEntity = BaseEchoLocateVoiceEntity(
            sessionId = callId.toString(),
            status = VoiceDataStatus.STATUS_RAW,
            callNumber = "1234567788",
            clientVersion = BuildConfig.VERSION_NAME,
            startTime = callId + 1000,
            endTime = 0L,
            numDiscardedIntents = 0,
            networkIdentity = NetworkIdentity(
                "310",
                "260"
            )
        )
        voiceRepository.insertBaseEchoLocateVoiceEntity(baseEchoLocateVoiceEntity)
    }

    private fun insertOEMSwVersionEntity(callId : Long) {
        val uniqueId = UUID.randomUUID().toString()
        val oemSoftwareVersion = VoiceOEMSoftwareVersionCollector().getOEMSoftwareVersion(applicationContext)
        val baseVoiceData = BaseVoiceData(callId.toString(), uniqueId)
        val oemSoftwareVersionEntity =
            VoiceEntityConverter.convertOEMSoftwareVersiontoEntity(oemSoftwareVersion, baseVoiceData)
        oemSoftwareVersionEntity.callId = callId.toString()
        oemSoftwareVersionEntity.uniqueId = uniqueId
        voiceRepository.insertOEMSoftwareVersionEntity(oemSoftwareVersionEntity)
    }

    private fun insertAppTriggerCallEntity(callId : Long) {
        val uniqueId = UUID.randomUUID().toString()
        val appTriggeredCallDataEntity = AppTriggeredCallDataEntity(
            "Contacts",
            "com.android.contacts",
            "910002200",
            "9.10.22",
            callId.toString(),
            (callId + 990).toString()
        )
        appTriggeredCallDataEntity.callId = callId.toString()
        appTriggeredCallDataEntity.uniqueId = uniqueId

        insertEventInfoEntity(callId, uniqueId)

        voiceRepository.insertAppTriggeredCallDataEntity(appTriggeredCallDataEntity)

    }

    private fun insertCallSettingDataEntity(callId : Long) {
        val uniqueId = UUID.randomUUID().toString()
        val callSettingDataEntity = CallSettingDataEntity(
            "REGISTERED", "WIFI_OFF", "WIFIPREFERRED", (callId + 2000).toString(), (callId + 2000).toString()
        )
        callSettingDataEntity.callId = callId.toString()
        callSettingDataEntity.uniqueId = uniqueId

        insertEventInfoEntity(callId, uniqueId)
        voiceRepository.insertCallSettingEntity(callSettingDataEntity)
    }

    private fun insertDetailedCallStates(callId : Long) {
        var uniqueId = UUID.randomUUID().toString()
        var detailedCallStateEntity = DetailedCallStateEntity(
            "NA",
            "ATTEMPTING",
            (callId + 2100).toString(),
            (callId + 2100).toString()
        )
        detailedCallStateEntity.callId = callId.toString()
        detailedCallStateEntity.uniqueId = uniqueId
        insertEventInfoEntity(callId, uniqueId)
        voiceRepository.insertDetailedCallStateEntity(detailedCallStateEntity)

        uniqueId = UUID.randomUUID().toString()
        detailedCallStateEntity = DetailedCallStateEntity(
            "NA",
            "CONNECTED",
            (callId + 7100).toString(),
            (callId + 7100).toString()
        )
        detailedCallStateEntity.callId = callId.toString()
        detailedCallStateEntity.uniqueId = uniqueId
        insertEventInfoEntity(callId, uniqueId)
        voiceRepository.insertDetailedCallStateEntity(detailedCallStateEntity)

        uniqueId = UUID.randomUUID().toString()
        detailedCallStateEntity = DetailedCallStateEntity(
            "NA",
            "DISCONNECTING",
            (callId + 32100).toString(),
            (callId + 32100).toString()
        )
        detailedCallStateEntity.callId = callId.toString()
        detailedCallStateEntity.uniqueId = uniqueId
        insertEventInfoEntity(callId, uniqueId)
        voiceRepository.insertDetailedCallStateEntity(detailedCallStateEntity)

        uniqueId = UUID.randomUUID().toString()
        detailedCallStateEntity = DetailedCallStateEntity(
            "16",
            "ENDED",
            (callId + 32400).toString(),
            (callId + 32400).toString()
        )
        detailedCallStateEntity.callId = callId.toString()
        detailedCallStateEntity.uniqueId = uniqueId
        insertEventInfoEntity(callId, uniqueId)
        voiceRepository.insertDetailedCallStateEntity(detailedCallStateEntity)

        voiceRepository.updateBaseEchoLocateVoiceEntityEndTime(
            callId.toString(),
            callId + 32500
        )
    }

    private fun insertUICallStates(callId : Long) {
        var uniqueId = UUID.randomUUID().toString()
        var uiCallStateEntity = UiCallStateEntity(
            "CALL_PRESSED",
            (callId + 1100).toString(),
            (callId + 1100).toString()
        )
        uiCallStateEntity.callId = callId.toString()
        uiCallStateEntity.uniqueId = uniqueId
        insertEventInfoEntity(callId, uniqueId)
        voiceRepository.insertUiCallStateEntity(uiCallStateEntity)

        uniqueId = UUID.randomUUID().toString()
        uiCallStateEntity = UiCallStateEntity(
            "CALL_CONNECTED",
            (callId + 7500).toString(),
            (callId + 7500).toString()
        )
        uiCallStateEntity.callId = callId.toString()
        uiCallStateEntity.uniqueId = uniqueId
        insertEventInfoEntity(callId, uniqueId)
        voiceRepository.insertUiCallStateEntity(uiCallStateEntity)

        uniqueId = UUID.randomUUID().toString()
        uiCallStateEntity = UiCallStateEntity(
            "END_PRESSED",
            (callId + 32160).toString(),
            (callId + 32160).toString()
        )
        uiCallStateEntity.callId = callId.toString()
        uiCallStateEntity.uniqueId = uniqueId
        insertEventInfoEntity(callId, uniqueId)
        voiceRepository.insertUiCallStateEntity(uiCallStateEntity)

        uniqueId = UUID.randomUUID().toString()
        uiCallStateEntity = UiCallStateEntity(
            "CALL_DISCONNECTED",
            (callId + 32800).toString(),
            (callId + 32800).toString()
        )
        uiCallStateEntity.callId = callId.toString()
        uiCallStateEntity.uniqueId = uniqueId
        insertEventInfoEntity(callId, uniqueId)
        voiceRepository.insertUiCallStateEntity(uiCallStateEntity)
    }

    private fun insertEventInfoEntity(callId : Long, uniqueId : String) {
        val cellInfoEntity = CellInfoEntity("NA", "NA", "-97", "-11", "-247",
            "24", "NA", "11316", "66,12", "21666306", "LTE")
        cellInfoEntity.callId = callId.toString()
        cellInfoEntity.uniqueId = uniqueId
        voiceRepository.insertCellInfoEntity(cellInfoEntity)

        val voiceLocationEntity = VoiceLocationEntity(84.0, 0F, 47.5505293, -122.1754714,
            12.9569997787476F, 3)
        voiceLocationEntity.callId = callId.toString()
        voiceLocationEntity.uniqueId = uniqueId
        voiceRepository.insertVoiceLocationEntity(voiceLocationEntity)
    }

}