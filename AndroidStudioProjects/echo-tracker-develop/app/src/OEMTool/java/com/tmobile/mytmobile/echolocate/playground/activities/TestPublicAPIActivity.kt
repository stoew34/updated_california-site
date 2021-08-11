package com.tmobile.mytmobile.echolocate.playground.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.*
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.tmobile.mytmobile.echolocate.R
import com.tmobile.mytmobile.echolocate.playground.PublicApisEnum
import com.tmobile.mytmobile.echolocate.playground.adapter.PublicAPIListAdapter
import com.tmobile.mytmobile.echolocate.playground.viewmodel.TestPublicAPIViewModel
import com.tmobile.mytmobile.echolocate.utils.ELDeviceUtils
import com.tmobile.pr.androidcommon.system.SystemService


@RequiresApi(Build.VERSION_CODES.P)
@SuppressLint("SetTextI18n")
class TestPublicAPIActivity : AppCompatActivity(), PublicAPIListAdapter.ApiSelectedListener {

    private lateinit var textAPIResult: TextView
    private var mPhoneStateListener: PhoneStateListener? = null
    private var mIsDisplayStateListenerRegistered: Boolean = false

    private lateinit var viewModel: TestPublicAPIViewModel
    private lateinit var telephonyManager: TelephonyManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_public_api)

        initViews()
    }

    private fun initViews() {
        telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        viewModel = ViewModelProviders.of(this).get(TestPublicAPIViewModel::class.java)
        val publicAPIGridView = findViewById<GridView>(R.id.public_api_grid_view)
        textAPIResult = findViewById(R.id.txt_api_result)
        val apis = PublicApisEnum.values()
        val adapter = PublicAPIListAdapter(this, apis, this)
        publicAPIGridView.adapter = adapter
        viewModel.result.observe(this, androidx.lifecycle.Observer {
            textAPIResult.text = it
        })
    }

    override fun onAPIClicked(api: PublicApisEnum) {
        when (api) {
            PublicApisEnum.CELL_INFO_5G -> {
                viewModel.getCellInfo(telephonyManager)
            }
            PublicApisEnum.CELL_SIGNAL_STRENGTH_5G -> {
                viewModel.getCellSignalStrength(telephonyManager)
            }
            PublicApisEnum.CELL_IDENTITY_5G -> {
                viewModel.getCellIdentity(telephonyManager)
            }
            PublicApisEnum.NETWORK_SCAN_P -> {
                if (ELDeviceUtils.isPieDeviceOrHigher()) {
                    val intent = Intent(this, NetworkScanActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Your device is below Android Pie", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            PublicApisEnum.NETWORK_TYPE_5G -> {
                viewModel.getNetworkType5g(telephonyManager)
            }
            PublicApisEnum.DATA_NETWORK_TYPE -> {
                viewModel.getDataNetworkType(telephonyManager)
            }
            PublicApisEnum.DISPLAY_STATE_REGISTER -> {
                if (ELDeviceUtils.isRDeviceOrHigher()) {
                    if (!mIsDisplayStateListenerRegistered) {
                        mPhoneStateListener = DisplayStateListener(this)
                        telephonyManager.listen(
                            mPhoneStateListener,
                            PhoneStateListener.LISTEN_DISPLAY_INFO_CHANGED
                        )
                        mIsDisplayStateListenerRegistered = true
                        Toast.makeText(
                            this,
                            "Display state listener is registered",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else {
                        Toast.makeText(
                            this,
                            "Display state listener is already registered",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            PublicApisEnum.DISPLAY_STATE_UNREGISTER -> {
                if (ELDeviceUtils.isRDeviceOrHigher()) {
                    mPhoneStateListener = DisplayStateListener(this)
                    telephonyManager.listen(
                        mPhoneStateListener,
                        PhoneStateListener.LISTEN_NONE
                    )
                    mIsDisplayStateListenerRegistered = false
                    Toast.makeText(
                        this,
                        "Display state listener is unregistered",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
            PublicApisEnum.NETWORK_BANDWIDTH -> {
                val connectivityManager =
                    SystemService.getConnectivityManager(applicationContext)
                viewModel.getUplinkAndDownlinkBandwidthSpeed(connectivityManager)
            }
            PublicApisEnum.CLEAR -> {
                viewModel.clearText()
            }
        }
    }

    /**
     * This class will register the display state listener
     * Refer this class for constant values
     * https://developer.android.com/reference/kotlin/android/telephony/TelephonyManager#NETWORK_TYPE_NR:kotlin.Int
     */
    inner class DisplayStateListener(val context: Context) : PhoneStateListener() {
        @RequiresApi(Build.VERSION_CODES.R)
        @SuppressLint("MissingPermission")
        override fun onDisplayInfoChanged(telephonyDisplayInfo: TelephonyDisplayInfo) {
            Toast.makeText(
                context,
                "Display state received with ${telephonyDisplayInfo.networkType}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}