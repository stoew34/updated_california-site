package com.tmobile.mytmobile.echolocate.dsdkHandshake

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.tmobile.mytmobile.echolocate.BuildConfig
import com.tmobile.mytmobile.echolocate.configuration.model.DsdkHandshake
import com.tmobile.mytmobile.echolocate.configuration.model.DsdkHandshakeModule.BaseDsdkHandshakeModule
import com.tmobile.mytmobile.echolocate.configuration.model.DsdkHandshakeModule.LteDsdkHandshakeModule
import com.tmobile.mytmobile.echolocate.configuration.model.DsdkHandshakeModule.Nr5gDsdkHandshakeModule
import com.tmobile.mytmobile.echolocate.configuration.model.DsdkHandshakeModule.VoiceDsdkHandshakeModule
import com.tmobile.mytmobile.echolocate.dsdkHandshake.database.databasemodel.DsdkHandshakeParametersModel
import com.tmobile.mytmobile.echolocate.dsdkHandshake.database.repository.DsdkHandshakeRepository
import com.tmobile.mytmobile.echolocate.dsdkHandshake.utils.DsdkHandshakeUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.dsdkHandshake.utils.DsdkHandshakeUtils.Companion.checkTacInList
import com.tmobile.mytmobile.echolocate.utils.SingletonHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.min

/**
 * This class is responsible to handle the dsdk handshake with echoapp
 */
class DsdkHandshakeManager private constructor(private val context: Context) {

    val dsdkHandshakeRepository = DsdkHandshakeRepository.getInstance(context)
    private val tmoPackageName = "com.tmobile.pr.mytmobile"

    companion object : SingletonHolder<DsdkHandshakeManager, Context>(::DsdkHandshakeManager)


    fun processHandshakeConfig(dsdkHandshakeConfig: DsdkHandshake) {
        try {
            // The DSDK Handshake will be enabled for EchoApp, only if the TMOApp version on the device is equal to (or greater than) featureSupportedVersionTMOApp.
            if (dsdkHandshakeConfig.isEnabled && checkTmoAppVersionForHandShake(dsdkHandshakeConfig.featureSupportedTMOAppVersion)) {
                if (dsdkHandshakeConfig.blacklistedTmoAppVersion.isNotEmpty()
                    && checkIfTmoAppVersionIsBlackListed(dsdkHandshakeConfig.blacklistedTmoAppVersion)
                ) {
                    performDsdkHandshake(dsdkHandshakeConfig, true)
                } else {
                    performDsdkHandshake(dsdkHandshakeConfig, false)
                }
            } else {
                showToastForDebugBuild("Handshake not supported")
                EchoLocateLog.eLogD("Diagnostic : No need to process the handshake between DSDK and EchoApp")
            }
        } catch (ex: Exception) {
            // If there is any exception, don't send any information to TMOApp
            showToastForDebugBuild("Exception while handling handshake information")
            EchoLocateLog.eLogE("Diagnostic : Exception while handling handshake information : ${ex.localizedMessage}")
            return
        }
    }

    /**
     * This function will perform the handshake with two conditions
     * @param isTmoAppVersionBlackListed :if the tmo version is blacklisted it will stop the all the modules and returns true
     * else it will check the other conditions in voiceStopDataCollection/lteStopDataCollection/nr5gStopDataCollection
     */
    private fun performDsdkHandshake(
        dsdkHandshakeConfig: DsdkHandshake,
        isTmoAppVersionBlackListed: Boolean
    ) {
        val dsdkHandshakeParametersModel = DsdkHandshakeParametersModel()
        when {
            isTmoAppVersionBlackListed -> {
                dsdkHandshakeParametersModel.voiceStopDataCollection = true
                dsdkHandshakeParametersModel.lteStopDataCollection = true
                dsdkHandshakeParametersModel.nr5gStopDataCollection = true
            }
            else -> {
                // 1. Check voice flag status
                dsdkHandshakeParametersModel.voiceStopDataCollection =
                    checkModuleSupportForDsdk(dsdkHandshakeConfig.dsdkVoiceEligibility)

                // 2. Check lte flag status
                dsdkHandshakeParametersModel.lteStopDataCollection =
                    checkModuleSupportForDsdk(dsdkHandshakeConfig.dsdkLteEligibility)

                // 3. Check nr5g flag status
                dsdkHandshakeParametersModel.nr5gStopDataCollection =
                    checkModuleSupportForDsdk(dsdkHandshakeConfig.dsdkNr5gEligibility)
            }
        }
        // Save the flags in the database
        dsdkHandshakeRepository.insertDsdkHandshakeParameterToDB(dsdkHandshakeParametersModel)
        EchoLocateLog.eLogD("Diagnostic : DsdkHandshakeParametersModel = $dsdkHandshakeParametersModel")
        sendHandShakeBroadcast(dsdkHandshakeParametersModel)
    }

    private fun sendHandShakeBroadcast(dsdkHandshakeParametersModel: DsdkHandshakeParametersModel) {
        // Send the broadcast with updated flags
        val handshakeIntent = Intent()
        handshakeIntent.action = DsdkHandshakeUtils.DSDK_HANDSHAKE_INTENT_ACTION

        try {
            val config = Gson().toJson(dsdkHandshakeParametersModel).toString()
            handshakeIntent.putExtra("dsdkHandshakeEvent", config)
            context.sendBroadcast(
                handshakeIntent,
                DsdkHandshakeUtils.DSDK_HANDSHAKE_BROADCAST_RECEIVER_PERMISSION
            )
            showToastForDebugBuild("Handshake information broadcasted :\n" +
                    "\tVoice : ${dsdkHandshakeParametersModel.voiceStopDataCollection}\n" +
                    "\tLTE : ${dsdkHandshakeParametersModel.lteStopDataCollection}\n" +
                    "\tNr5g : ${dsdkHandshakeParametersModel.nr5gStopDataCollection}")
        } catch (exception: IllegalStateException) {
            EchoLocateLog.eLogE("IllegalStateException in converting dsdkhandshake String to Json : ${exception.localizedMessage}")
        } catch (exception: JsonSyntaxException) {
            EchoLocateLog.eLogE("JsonSyntaxException in converting dsdkhandshake String to Json : ${exception.localizedMessage}")
        } catch (exception: Exception) {
            EchoLocateLog.eLogE("Exception in converting dsdkhandshake String to Json : ${exception.localizedMessage}")
        }
    }

    private fun checkModuleSupportForDsdk(moduleData: BaseDsdkHandshakeModule): Boolean {

        return when {
            // 1. Check for shouldStopModule flag from moduleData
            //   If true, return true
            //   Else check next condition
            shouldStopModule(moduleData) -> {
                true
            }

            // 2. Check the TAC number of the device
            //   If TAC number is listed in tacListUnsupported from moduleData, return true
            //   Else return default value "false" so that TMO app can continue to collect module(voice/lte/nr5g) data
            else -> checkTacInList(context, moduleData.blacklistedTAC)
        }
    }

    private fun shouldStopModule(moduleData: BaseDsdkHandshakeModule): Boolean {
        return when (moduleData) {
            is VoiceDsdkHandshakeModule -> moduleData.voiceStopDataCollection
            is LteDsdkHandshakeModule -> moduleData.lteStopDataCollection
            is Nr5gDsdkHandshakeModule -> moduleData.nr5gStopDataCollection
            else -> false
        }
    }


    /**
     *    This function checks the TMOApp version against FeatureSupportedVersionTmoApp
     *    from config file
     *  @return Boolean
     *    True : If the current TMOApp version is equal or greater than specified version
     *    False : If the current TMOApp version is smaller than specified version
     */
    private fun checkTmoAppVersionForHandShake(appVersion: String): Boolean {
        val currVersionTmo = DsdkHandshakeUtils.getApplicationVersionName(context, tmoPackageName)
        if (currVersionTmo.isEmpty())
            return false

        if (appVersion.isEmpty())
            return true

        val versionCodes = appVersion.split(".")
        val currVersionCodes = currVersionTmo.split(".")

        // To avoid IndexOutOfBounds while accessing elements from list
        val maxIndex: Int = min(versionCodes.size, currVersionCodes.size)

        for (index in 0 until maxIndex) {
            val currValue = Integer.valueOf(currVersionCodes[index])
            val specifiedValue = Integer.valueOf(versionCodes[index])
            if (currValue > specifiedValue) {
                // current version of TMOApp is greater
                return true
            } else if (currValue < specifiedValue) {
                // current version of TMOApp is smaller
                return false
            }
        }

        // Current TMOApp version matches with the specified version
        return true
    }

    /**
     *    This function checks the TMOApp version against blacklistedTmoAppVersion
     *    from config file
     *  @return Boolean
     *    True : If the current TMOApp version is equal or greater than specified version
     *    False : If the current TMOApp version is smaller than specified version
     */
    // todo once the FeatureSupportedVersionTmoApp is available we can remove this function and use single function
    private fun checkIfTmoAppVersionIsBlackListed(appVersion: String): Boolean {
        val currVersionTmo = DsdkHandshakeUtils.getApplicationVersionName(context, tmoPackageName)
        if (currVersionTmo.isEmpty())
            return false

        if (appVersion.isEmpty())
            return true

        val versionCodes = appVersion.split(".")
        val currVersionCodes = currVersionTmo.split(".")

        // To avoid IndexOutOfBounds while accessing elements from list
        val maxIndex: Int = min(versionCodes.size, currVersionCodes.size)
        for (index in 0 until maxIndex) {
            val currValue = Integer.valueOf(currVersionCodes[index])
            val specifiedValue = Integer.valueOf(versionCodes[index])
            if (currValue > specifiedValue) {
                // current version of TMOApp is greater
                return true
            } else if (currValue < specifiedValue) {
                // current version of TMOApp is smaller
                return false
            }
        }

        // Current TMOApp version matches with the specified version
        return true
    }

    private fun showToastForDebugBuild(msg: String) {
        if (BuildConfig.DEBUG) {
            if (msg.isNotEmpty()) {
                GlobalScope.launch(Dispatchers.Main) {
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
