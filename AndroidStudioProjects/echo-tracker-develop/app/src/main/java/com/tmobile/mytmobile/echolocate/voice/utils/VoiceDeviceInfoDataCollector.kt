package com.tmobile.mytmobile.echolocate.voice.utils

/**
 * Created by Divya Mittal on 4/13/21
 */
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import com.tmobile.mytmobile.echolocate.standarddatablocks.DeviceInfo
import com.tmobile.mytmobile.echolocate.utils.ELDeviceUtils
import com.tmobile.pr.androidcommon.system.SystemService

/**
 * class DeviceInfoDataCollector
 *
 * This class is used to assign values to DeviceInfo.kt objects
 */
class VoiceDeviceInfoDataCollector {

    /**
     *  invoke this method to get the DeviceInfo data
     *  @param Context
     *  @return DeviceInfo
     */
    fun getDeviceInformation(context: Context): DeviceInfo {

        if (ELDeviceUtils.isRDeviceOrHigher() &&
            // For R devices, we must have either READ_PHONE_NUMBERS or READ_PHONE_STATE permission
            // to read the phone number, IMEI & IMSI
            (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_NUMBERS
            )) &&
            (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_STATE
            )) ) {
            // need to handle permissions in the calling handler, otherwise will return null, as the permissions are not given
            return DeviceInfo(VoiceIDGenerator.getUuid(), "", "", "", VoiceIDGenerator.getTestSessionID())
        } else if (ELDeviceUtils.isQDeviceOrHigher() && PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_STATE
            )
        ) {
            // need to handle permissions in the calling handler, otherwise will return null, as the permissions are not given
            return DeviceInfo(VoiceIDGenerator.getUuid(), "", "", "", VoiceIDGenerator.getTestSessionID())
        }

        val telephonyManager: TelephonyManager = SystemService.getTelephonyManager(context) as TelephonyManager

        // instantiate with corresponding values to the object

        return DeviceInfo(
            VoiceIDGenerator.getUuid(),           //uuid
            VoiceUtils.getImei(context),             //imei
            getImsi(telephonyManager),              //imsi
            getMsisdn(telephonyManager),            //msisdn
            VoiceIDGenerator.getTestSessionID())
    }

    /**
     * fun getImsi
     *  IMSI getter function
     *
     * @param TelephonyManager
     * @return String
     */
    @SuppressLint("MissingPermission")
    private fun getImsi(telephonyManager: TelephonyManager?): String {

        return try {
            if (telephonyManager != null) {
                telephonyManager.subscriberId ?: ""
            } else {
                ""
            }
        } catch (ex : Exception) {
            ""
        }
    }

    /**
     *  fun getMsisdn
     *
     *    gets device phone number/msisdn
     *
     *   @param TelephonyManager
     *
     *   @return String
     */
    @SuppressLint("MissingPermission", "HardwareIds")
    private fun getMsisdn(telephonyManager: TelephonyManager?): String {

        return try {
            if (telephonyManager != null) {
                telephonyManager.line1Number ?: ""
            } else {
                ""
            }
        } catch (ex : Exception) {
            ""
        }
    }

}