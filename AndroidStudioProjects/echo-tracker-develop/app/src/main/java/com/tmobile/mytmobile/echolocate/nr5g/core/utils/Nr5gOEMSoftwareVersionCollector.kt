package com.tmobile.mytmobile.echolocate.nr5g.core.utils

/**
 * Created by Divya Mittal on 4/13/21
 */
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import com.tmobile.mytmobile.echolocate.standarddatablocks.OEMSV
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.pr.androidcommon.device.Manufacturer
import com.tmobile.pr.androidcommon.system.SystemService
import com.tmobile.pr.androidcommon.system.reflection.TmoCommonReflection

/**
 *  class OEMSoftwareVersionCollector
 *
 *  class for assigning values to and getting\
 *  OEMSoftwareVersion objects
 */
class Nr5gOEMSoftwareVersionCollector {

    // assemble OEMSoftwareVersion object
    fun getOEMSoftwareVersion(context: Context): OEMSV {

        return OEMSV(
            getSoftwareVersionValue(context),
            getCustomVersionValue(),
            Build.getRadioVersion() ?: "",
            Build.ID,
            Build.VERSION.RELEASE
        )
    }

    /**
     *  getSoftwareVersionValue svValue
     *  @param context : Context
     *  @return svValue : String
     *
     */
    private fun getSoftwareVersionValue(context: Context): String {
        val readPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
        val tm = SystemService.getTelephonyManager(context) as TelephonyManager
        val svValue: String?

        svValue = if (readPermission == PackageManager.PERMISSION_GRANTED) {
            try {
                tm.deviceSoftwareVersion.let {
                    if (!it.isNullOrEmpty() && it.length > 3)
                        it.substring(0, 3)
                    else
                        it
                }
            } catch (e: Exception) {
                EchoLocateLog.eLogE("error: ${e.localizedMessage}")
                ""
            }
        } else {
            ""
        }
        return (svValue ?: "")
    }

    /**
     *  get custom version value cvValue
     *  @return cvValue : String
     */
    private fun getCustomVersionValue(): String {
        var cvValue = ""

        if (Manufacturer.isLg) {       // determine custom value
            try {
                cvValue = TmoCommonReflection.lgSwVersionString ?: ""
            } catch (e: Exception) {           // possible null
                EchoLocateLog.eLogE("error: ${e.localizedMessage}")
            }
        }
        return cvValue
    }
}