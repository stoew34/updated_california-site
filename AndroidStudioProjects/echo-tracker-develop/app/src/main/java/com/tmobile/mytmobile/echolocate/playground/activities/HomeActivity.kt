package com.tmobile.mytmobile.echolocate.playground.activities

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.GridView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.tmobile.mytmobile.echolocate.R
import com.tmobile.mytmobile.echolocate.playground.ModulesEnum
import com.tmobile.mytmobile.echolocate.playground.adapter.ModuleListAdapter
import com.tmobile.mytmobile.echolocate.userconsent.ConsentRequestProvider
import com.tmobile.mytmobile.echolocate.userconsent.userconsentevents.UserConsentFlagsParameters
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog


class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val modulesGridView = findViewById<GridView>(R.id.module_grid_view)
        val adapter = ModuleListAdapter(this, ModulesEnum.values())
        modulesGridView.adapter = adapter

        checkConsentFlags()
    }

    private fun checkConsentFlags() {
        var userConsentFlagsParameters: UserConsentFlagsParameters? =
            ConsentRequestProvider.getInstance(this).getUserConsentFlags()?.userConsentFlagsParameters

        if (userConsentFlagsParameters == null) {
            // Assign default values which are in declined state
            userConsentFlagsParameters = UserConsentFlagsParameters()
        }

        if (!userConsentFlagsParameters.isAllowedDeviceDataCollection) {
            EchoLocateLog.eLogI("Diagnostic : No data collection as the user consent is denied")
            showAlertDialogForConsent(getString(R.string.msg_consent_flag_denied))
        }
    }

    private fun showAlertDialogForConsent(message: String) {
        runOnUiThread {
            val alertDialog = AlertDialog.Builder(this).create()
            alertDialog.run {
                setTitle(title)
                setMessage(message)
                setCanceledOnTouchOutside(false)
                setOnCancelListener(
                    DialogInterface.OnCancelListener { dialog: DialogInterface ->
                        dialog.dismiss()
                    }
                )
                setButton(
                    DialogInterface.BUTTON_POSITIVE, getString(R.string.btn_ok),
                    DialogInterface.OnClickListener { dialog: DialogInterface, i: Int ->
                        dialog.dismiss()
                    }
                )
                setButton(
                    DialogInterface.BUTTON_NEUTRAL, getString(R.string.btn_settings),
                    DialogInterface.OnClickListener { dialog: DialogInterface, i: Int ->
                        try {
                            val consentIntent = Intent("com.carrieriq.tmobile.SUMMARY")
                            startActivity(consentIntent)
                        } catch (ex: Exception) {
                            EchoLocateLog.eLogE("Diagnostic : Error launching UI for the user consent")
                        }
                        dialog.dismiss()
                    }
                )
                if (!isFinishing) {
                    show()
                }
            }
        }
    }

}
