package com.tmobile.mytmobile.echolocate.playground.activities

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.telephony.*
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.tmobile.mytmobile.echolocate.R
import com.tmobile.mytmobile.echolocate.utils.ELDeviceUtils
import java.util.concurrent.Executors


@RequiresApi(Build.VERSION_CODES.P)
class NetworkScanActivity : AppCompatActivity() {

    private val NETWORK_TECH = arrayOf("LTE", "5G")
    object Companion {
        val LTE_BANDS = arrayOf(
            "BAND_1",
            "BAND_2",
            "BAND_3",
            "BAND_4",
            "BAND_5",
            "BAND_6",
            "BAND_7",
            "BAND_8",
            "BAND_9",
            "BAND_10",
            "BAND_11",
            "BAND_12",
            "BAND_13",
            "BAND_14",
            "BAND_17",
            "BAND_18",
            "BAND_19",
            "BAND_20",
            "BAND_21",
            "BAND_22",
            "BAND_23",
            "BAND_24",
            "BAND_25",
            "BAND_26",
            "BAND_27",
            "BAND_28",
            "BAND_30",
            "BAND_31",
            "BAND_33",
            "BAND_34",
            "BAND_35",
            "BAND_36",
            "BAND_37",
            "BAND_38",
            "BAND_39",
            "BAND_40",
            "BAND_41",
            "BAND_42",
            "BAND_43",
            "BAND_44",
            "BAND_45",
            "BAND_46",
            "BAND_47",
            "BAND_48",
            "BAND_49",
            "BAND_50",
            "BAND_51",
            "BAND_52",
            "BAND_53",
            "BAND_65",
            "BAND_66",
            "BAND_68",
            "BAND_70",
            "BAND_71",
            "BAND_72",
            "BAND_73",
            "BAND_74",
            "BAND_85",
            "BAND_87",
            "BAND_88"
        )

        val NR_BANDS = arrayOf(
            "BAND_1",
            "BAND_2",
            "BAND_3",
            "BAND_5",
            "BAND_7",
            "BAND_8",
            "BAND_12",
            "BAND_14",
            "BAND_18",
            "BAND_20",
            "BAND_25",
            "BAND_28",
            "BAND_29",
            "BAND_30",
            "BAND_34",
            "BAND_38",
            "BAND_39",
            "BAND_40",
            "BAND_41",
            "BAND_48",
            "BAND_50",
            "BAND_51",
            "BAND_65",
            "BAND_66",
            "BAND_70",
            "BAND_71",
            "BAND_74",
            "BAND_75",
            "BAND_76",
            "BAND_77",
            "BAND_78",
            "BAND_79",
            "BAND_80",
            "BAND_81",
            "BAND_82",
            "BAND_83",
            "BAND_84",
            "BAND_86",
            "BAND_89",
            "BAND_90",
            "BAND_91",
            "BAND_92",
            "BAND_93",
            "BAND_94",
            "BAND_95",
            "BAND_257",
            "BAND_258",
            "BAND_260",
            "BAND_261"
        )

        val LTE_BAND_VALUES = arrayOf(
            AccessNetworkConstants.EutranBand.BAND_1,
            AccessNetworkConstants.EutranBand.BAND_2,
            AccessNetworkConstants.EutranBand.BAND_3,
            AccessNetworkConstants.EutranBand.BAND_4,
            AccessNetworkConstants.EutranBand.BAND_5,
            AccessNetworkConstants.EutranBand.BAND_6,
            AccessNetworkConstants.EutranBand.BAND_7,
            AccessNetworkConstants.EutranBand.BAND_8,
            AccessNetworkConstants.EutranBand.BAND_9,
            AccessNetworkConstants.EutranBand.BAND_10,
            AccessNetworkConstants.EutranBand.BAND_11,
            AccessNetworkConstants.EutranBand.BAND_12,
            AccessNetworkConstants.EutranBand.BAND_13,
            AccessNetworkConstants.EutranBand.BAND_14,
            AccessNetworkConstants.EutranBand.BAND_17,
            AccessNetworkConstants.EutranBand.BAND_18,
            AccessNetworkConstants.EutranBand.BAND_19,
            AccessNetworkConstants.EutranBand.BAND_20,
            AccessNetworkConstants.EutranBand.BAND_21,
            AccessNetworkConstants.EutranBand.BAND_22,
            AccessNetworkConstants.EutranBand.BAND_23,
            AccessNetworkConstants.EutranBand.BAND_24,
            AccessNetworkConstants.EutranBand.BAND_25,
            AccessNetworkConstants.EutranBand.BAND_26,
            AccessNetworkConstants.EutranBand.BAND_27,
            AccessNetworkConstants.EutranBand.BAND_28,
            AccessNetworkConstants.EutranBand.BAND_30,
            AccessNetworkConstants.EutranBand.BAND_31,
            AccessNetworkConstants.EutranBand.BAND_33,
            AccessNetworkConstants.EutranBand.BAND_34,
            AccessNetworkConstants.EutranBand.BAND_35,
            AccessNetworkConstants.EutranBand.BAND_36,
            AccessNetworkConstants.EutranBand.BAND_37,
            AccessNetworkConstants.EutranBand.BAND_38,
            AccessNetworkConstants.EutranBand.BAND_39,
            AccessNetworkConstants.EutranBand.BAND_40,
            AccessNetworkConstants.EutranBand.BAND_41,
            AccessNetworkConstants.EutranBand.BAND_42,
            AccessNetworkConstants.EutranBand.BAND_43,
            AccessNetworkConstants.EutranBand.BAND_44,
            AccessNetworkConstants.EutranBand.BAND_45,
            AccessNetworkConstants.EutranBand.BAND_46,
            AccessNetworkConstants.EutranBand.BAND_47,
            AccessNetworkConstants.EutranBand.BAND_48,
            AccessNetworkConstants.EutranBand.BAND_49,
            AccessNetworkConstants.EutranBand.BAND_50,
            AccessNetworkConstants.EutranBand.BAND_51,
            AccessNetworkConstants.EutranBand.BAND_52,
            AccessNetworkConstants.EutranBand.BAND_53,
            AccessNetworkConstants.EutranBand.BAND_65,
            AccessNetworkConstants.EutranBand.BAND_66,
            AccessNetworkConstants.EutranBand.BAND_68,
            AccessNetworkConstants.EutranBand.BAND_70,
            AccessNetworkConstants.EutranBand.BAND_71,
            AccessNetworkConstants.EutranBand.BAND_72,
            AccessNetworkConstants.EutranBand.BAND_73,
            AccessNetworkConstants.EutranBand.BAND_74,
            AccessNetworkConstants.EutranBand.BAND_85,
            AccessNetworkConstants.EutranBand.BAND_87,
            AccessNetworkConstants.EutranBand.BAND_88
        )

        val NR_BAND_VALUES = arrayOf(
            AccessNetworkConstants.NgranBands.BAND_1,
            AccessNetworkConstants.NgranBands.BAND_2,
            AccessNetworkConstants.NgranBands.BAND_3,
            AccessNetworkConstants.NgranBands.BAND_5,
            AccessNetworkConstants.NgranBands.BAND_7,
            AccessNetworkConstants.NgranBands.BAND_8,
            AccessNetworkConstants.NgranBands.BAND_12,
            AccessNetworkConstants.NgranBands.BAND_14,
            AccessNetworkConstants.NgranBands.BAND_18,
            AccessNetworkConstants.NgranBands.BAND_20,
            AccessNetworkConstants.NgranBands.BAND_25,
            AccessNetworkConstants.NgranBands.BAND_28,
            AccessNetworkConstants.NgranBands.BAND_29,
            AccessNetworkConstants.NgranBands.BAND_30,
            AccessNetworkConstants.NgranBands.BAND_34,
            AccessNetworkConstants.NgranBands.BAND_38,
            AccessNetworkConstants.NgranBands.BAND_39,
            AccessNetworkConstants.NgranBands.BAND_40,
            AccessNetworkConstants.NgranBands.BAND_41,
            AccessNetworkConstants.NgranBands.BAND_48,
            AccessNetworkConstants.NgranBands.BAND_50,
            AccessNetworkConstants.NgranBands.BAND_51,
            AccessNetworkConstants.NgranBands.BAND_65,
            AccessNetworkConstants.NgranBands.BAND_66,
            AccessNetworkConstants.NgranBands.BAND_70,
            AccessNetworkConstants.NgranBands.BAND_71,
            AccessNetworkConstants.NgranBands.BAND_74,
            AccessNetworkConstants.NgranBands.BAND_75,
            AccessNetworkConstants.NgranBands.BAND_76,
            AccessNetworkConstants.NgranBands.BAND_77,
            AccessNetworkConstants.NgranBands.BAND_78,
            AccessNetworkConstants.NgranBands.BAND_79,
            AccessNetworkConstants.NgranBands.BAND_80,
            AccessNetworkConstants.NgranBands.BAND_81,
            AccessNetworkConstants.NgranBands.BAND_82,
            AccessNetworkConstants.NgranBands.BAND_83,
            AccessNetworkConstants.NgranBands.BAND_84,
            AccessNetworkConstants.NgranBands.BAND_86,
            AccessNetworkConstants.NgranBands.BAND_89,
            AccessNetworkConstants.NgranBands.BAND_90,
            AccessNetworkConstants.NgranBands.BAND_91,
            AccessNetworkConstants.NgranBands.BAND_92,
            AccessNetworkConstants.NgranBands.BAND_93,
            AccessNetworkConstants.NgranBands.BAND_94,
            AccessNetworkConstants.NgranBands.BAND_95,
            AccessNetworkConstants.NgranBands.BAND_257,
            AccessNetworkConstants.NgranBands.BAND_258,
            AccessNetworkConstants.NgranBands.BAND_260,
            AccessNetworkConstants.NgranBands.BAND_261
        )
    }
    lateinit var textAPIResult: TextView
    lateinit var spinnerNetworkTech: Spinner
    lateinit var spinnerBand: Spinner
    lateinit var selectedNetTechTextView: TextView
    lateinit var selectedBandTextView: TextView
    private var mTech = ""
    private var mBand = -1
    private var mBandText = ""
    private var mBandType = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network_scan)
        initViews()
    }

    private fun initViews() {
        textAPIResult = findViewById(R.id.txt_api_result)
        spinnerNetworkTech = findViewById(R.id.spinner_net_tech)
        spinnerBand = findViewById(R.id.spinner_band)
        selectedNetTechTextView = findViewById(R.id.selected_net_tech_value)
        selectedBandTextView = findViewById(R.id.selected_band_value)

        spinnerNetworkTech.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, NETWORK_TECH)
        spinnerNetworkTech.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedNetTechTextView.text = NETWORK_TECH[position]

                mTech = NETWORK_TECH[position]
                when(position) {
                    0 -> {
                        mBandType = AccessNetworkConstants.AccessNetworkType.EUTRAN
                        spinnerBand.adapter = ArrayAdapter<String>(
                            this@NetworkScanActivity,
                            android.R.layout.simple_spinner_item,
                            Companion.LTE_BANDS
                        )
                    }
                    1 -> {
                        mBandType = AccessNetworkConstants.AccessNetworkType.NGRAN
                        spinnerBand.adapter = ArrayAdapter<String>(
                            this@NetworkScanActivity,
                            android.R.layout.simple_spinner_item,
                            Companion.NR_BANDS
                        )
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        spinnerBand.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedBands: Array<Int> = if (mTech == NETWORK_TECH[0])
                    Companion.LTE_BAND_VALUES
                else
                    Companion.NR_BAND_VALUES

                val selectedBandValues: Array<String> = if (mTech == NETWORK_TECH[0])
                    Companion.LTE_BANDS
                else
                    Companion.NR_BANDS

                mBand = selectedBands[position]
                selectedBandTextView.text = selectedBandValues[position]

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    @SuppressLint("SetTextI18n")
    inner class RadioCallback : TelephonyScanManager.NetworkScanCallback() {
        private var mCellInfoResults: List<CellInfo>? = null
        private var mScanError = 0
        override fun onResults(cellInfoResults: List<CellInfo>?) {
            var result = ""
            mCellInfoResults = cellInfoResults
            this@NetworkScanActivity.runOnUiThread {
                for (cellInfo in mCellInfoResults!!) {
                    result += "\n--------------------------------------"
                    result += "\nRegistered - ${cellInfo.isRegistered}\n"
                    val cellIdentityLte = cellInfo.cellIdentity as CellIdentityLte
                    result += "\nCell Identity:\n EarFcn - ${cellIdentityLte.earfcn}\n" +
                            "BandWidth - ${cellIdentityLte.bandwidth}\n" +
                            "MCC - ${cellIdentityLte.mccString}\n" +
                            "MNC - ${cellIdentityLte.mncString}\n"
                    val cellSignalStrength =
                        cellInfo.cellSignalStrength as CellSignalStrengthLte
                    result += "\nCell Signal LTE:\n ss - ${cellSignalStrength.rssi}\n" +
                            "RSRP - ${cellSignalStrength.rsrp}\n" +
                            "RSRQ - ${cellSignalStrength.rsrq}\n" +
                            "RSSNR - ${cellSignalStrength.rssnr}\n" +
                            "CQI - ${cellSignalStrength.cqi}\n" +
                            "TA - ${cellSignalStrength.timingAdvance}"
                    result += "\n--------------------------------------"
                }
                textAPIResult.text = "${textAPIResult.text} $result"
            }
        }

        override fun onError(error: Int) {
            mScanError = error
            this@NetworkScanActivity.runOnUiThread {
                textAPIResult.text = "Error: $mScanError"
            }
        }

        override fun onComplete() {
            this@NetworkScanActivity.runOnUiThread {
                val result = textAPIResult.text.toString()
                textAPIResult.text = "$result\nScan Completed!"
            }
        }
    }

    @SuppressLint("MissingPermission", "NewApi", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.P)
    fun initNetworkScan(view: View) {
        if (validateInputs()) {
            if (ELDeviceUtils.isPieDeviceOrHigher()) {
                val networkScanRequest: NetworkScanRequest
                val bands = intArrayOf(
                    0
                )
                val telephonyManager =
                    getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

                bands[0] = mBand
                val radioAccessSpecifiers: Array<RadioAccessSpecifier?> = arrayOfNulls(1)
                radioAccessSpecifiers[0] = RadioAccessSpecifier(
                    mBandType,
                    bands,
                    null)

                networkScanRequest = NetworkScanRequest(
                    NetworkScanRequest.SCAN_TYPE_PERIODIC,
                    radioAccessSpecifiers,
                    15,
                    300,
                    true,
                    3,
                    null)

                telephonyManager.requestNetworkScan(networkScanRequest,
                    Executors.newSingleThreadExecutor(), RadioCallback())

            } else {
                textAPIResult.text = "Device Version is below Android P"
            }
        } else {
            Toast.makeText(this,
                "Please select all the values before starting the scan",
                Toast.LENGTH_SHORT).show()
        }
    }

    fun clearText(view: View) {
        textAPIResult.text = ""
    }

    private fun validateInputs() : Boolean {
        return mTech.isNotEmpty() && mBand != -1 && mBandType != -1
    }

}