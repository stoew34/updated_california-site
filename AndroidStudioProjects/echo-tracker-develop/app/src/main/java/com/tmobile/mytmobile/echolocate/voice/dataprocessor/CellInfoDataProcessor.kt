package com.tmobile.mytmobile.echolocate.voice.dataprocessor

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.telephony.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tmobile.echolocate.CallSessionProto
import com.tmobile.mytmobile.echolocate.utils.ELDeviceUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.voice.model.CellInfo
import com.tmobile.pr.androidcommon.system.SystemService
import java.util.concurrent.Executor
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

class CellInfoDataProcessor(val context: Context) {

    private val SEMICOLON = ";"

    private var resultSemaphore: Semaphore? = null
    private val ACQUIRE_TIMEOUT_SECONDS = 5

    lateinit var cellInfo: CellInfo
    val newCellInfoOrBuilder = CallSessionProto.DeviceIntents.EventInfo.CellInfo.newBuilder()

    /**
     * Returns [CallSessionProto.DeviceIntents.EventInfo.CellInfo.Builder] object based on the intent passed by extracting
     * the extras from the intent and assigning to it respective attributes
     * @param intent: [Intent]
     * @return [CallSessionProto.DeviceIntents.EventInfo.CellInfo.Builder] generated object
     */
    fun getCellInfoBuilder(intent: Intent): CallSessionProto.DeviceIntents.EventInfo.CellInfo.Builder? {
        newCellInfoOrBuilder.networkBand =
            intent.getStringExtra(BaseIntentProcessor.VOICE_ACCESS_NETWORK_STATE_BAND).toString()
        newCellInfoOrBuilder.networkType =
            intent.getStringExtra(BaseIntentProcessor.VOICE_ACCESS_NETWORK_STATE_TYPE).toString()

        val accessNetworkSignal =
            intent.getStringExtra(BaseIntentProcessor.VOICE_ACCESS_NETWORK_STATE_SIGNAL_EXTRA)
        if (accessNetworkSignal != null) {
            val signalState =
                accessNetworkSignal.split(SEMICOLON.toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()

            if (signalState.size == BaseIntentProcessor.NETWORK_STATE_SIGNAL_VALUES_SIZE) {
                newCellInfoOrBuilder.rssi = signalState[1]
                newCellInfoOrBuilder.rscp = signalState[2]
                newCellInfoOrBuilder.ecio = signalState[3]
                newCellInfoOrBuilder.rsrp = signalState[4]
                newCellInfoOrBuilder.rsrq = signalState[5]
                newCellInfoOrBuilder.sinr = signalState[6]
                newCellInfoOrBuilder.snr = signalState[7]
            }
        }
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                val telephonyManager = SystemService.getTelephonyManager(context)
                if (telephonyManager != null) {
                    if (ELDeviceUtils.isQDeviceOrHigher()) {
                        resultSemaphore = Semaphore(1)
                        getCellIdForQDevice(telephonyManager)
                        acquireResult()
                    } else {
                        if (telephonyManager.allCellInfo != null) {
                            setLacAndCellId(telephonyManager.allCellInfo)
                        }
                    }
                }
            } catch (exception: IllegalStateException) {
                val crashlytics = FirebaseCrashlytics.getInstance()
                crashlytics.recordException(
                    Throwable
                        (
                        "Fatal Exception: java.lang.IllegalStateException: " +
                                "allCellinfo must not be null -> ${exception.localizedMessage} "
                    )
                )
                crashlytics.sendUnsentReports()
            }

        } else {
            EchoLocateLog.eLogD("ACCESS_COARSE_LOCATION is not granted", System.currentTimeMillis())
        }
        return newCellInfoOrBuilder
    }

    /**
     * Returns [CellInfo] object based on the intent passed by extracting
     * the extras from the intent and assigning to it respective attributes
     * @param intent: [Intent]
     * @return [CellInfo] generated object
     */
    fun getCellInfo(intent: Intent): CellInfo {

        cellInfo = CellInfo("", "", "", "", "", "", "", "", "", "", "")

        val accessNetworkSignal =
            intent.getStringExtra(BaseIntentProcessor.VOICE_ACCESS_NETWORK_STATE_SIGNAL_EXTRA)

        if (intent.hasExtra(BaseIntentProcessor.VOICE_ACCESS_NETWORK_STATE_BAND)) {
            cellInfo.networkBand =
                intent.getStringExtra(BaseIntentProcessor.VOICE_ACCESS_NETWORK_STATE_BAND)
                    .toString()
        }

        if (intent.hasExtra(BaseIntentProcessor.VOICE_ACCESS_NETWORK_STATE_TYPE)) {
            cellInfo.networkType =
                intent.getStringExtra(BaseIntentProcessor.VOICE_ACCESS_NETWORK_STATE_TYPE)
                    .toString()
        }

        if (accessNetworkSignal != null) {
            val signalState =
                accessNetworkSignal.split(SEMICOLON.toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()

            if (signalState.size == BaseIntentProcessor.NETWORK_STATE_SIGNAL_VALUES_SIZE) {
                cellInfo.rssi = signalState[1]
                cellInfo.rscp = signalState[2]
                cellInfo.ecio = signalState[3]
                cellInfo.rsrp = signalState[4]
                cellInfo.rsrq = signalState[5]
                cellInfo.sinr = signalState[6]
                cellInfo.snr = signalState[7]
            }
        }
        if (ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                val telephonyManager = SystemService.getTelephonyManager(context)
                if (telephonyManager != null) {
                    if (ELDeviceUtils.isQDeviceOrHigher()) {
                        resultSemaphore = Semaphore(1)
                        getCellIdForQDevice(telephonyManager)
                        acquireResult()
                    } else {
                        if (telephonyManager.allCellInfo != null) {
                            setLacAndCellId(telephonyManager.allCellInfo)
                        }
                    }
                }
            } catch (exception: IllegalStateException) {
                val crashlytics = FirebaseCrashlytics.getInstance()
                crashlytics.recordException(
                    Throwable
                        (
                        "Fatal Exception: java.lang.IllegalStateException: " +
                                "allCellinfo must not be null -> ${exception.localizedMessage} "
                    )
                )
                crashlytics.sendUnsentReports()
            }

        } else {
            EchoLocateLog.eLogD("ACCESS_COARSE_LOCATION is not granted", System.currentTimeMillis())
        }
        return cellInfo
    }

    private fun setLacAndCellId(cellInfoList: List<android.telephony.CellInfo?>?) {
        if (cellInfoList != null) {
            for (cell in cellInfoList) {
                if (cell != null) {
                    when {
                        cell.isRegistered -> when {
                            cell is CellInfoGsm -> {
                                newCellInfoOrBuilder.cellId = cell.cellIdentity.cid.toString()
                                newCellInfoOrBuilder.lac = cell.cellIdentity.lac.toString()
                            }
                            cell is CellInfoLte -> {
                                newCellInfoOrBuilder.cellId = cell.cellIdentity.ci.toString()
                                newCellInfoOrBuilder.lac = cell.cellIdentity.tac.toString()
                            }
                            cell is CellInfoWcdma -> {
                                newCellInfoOrBuilder.cellId = cell.cellIdentity.cid.toString()
                                newCellInfoOrBuilder.lac = cell.cellIdentity.lac.toString()
                            }
                            ELDeviceUtils.isQDeviceOrHigher() && cell is CellInfoTdscdma -> {
                                //supported in Android Q
                                newCellInfoOrBuilder.cellId = cell.cellIdentity.cid.toString()
                                newCellInfoOrBuilder.lac = cell.cellIdentity.lac.toString()
                            }
                            ELDeviceUtils.isQDeviceOrHigher() && cell is CellInfoNr -> {
                                val cellIdentity = cell.cellIdentity as CellIdentityNr
                                newCellInfoOrBuilder.cellId = cellIdentity.nci.toString()
                                newCellInfoOrBuilder.lac = cellIdentity.tac.toString()

                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * Acquire result semaphore, released on either success or error callback
     */
    open fun acquireResult() {
        try {
            resultSemaphore?.tryAcquire(
                ACQUIRE_TIMEOUT_SECONDS.toLong(), TimeUnit.SECONDS
            )
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun getCellIdForQDevice(telephonyManager: TelephonyManager) {
        try {
            val executor = ThreadExecutor()

            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            telephonyManager.requestCellInfoUpdate(
                executor,
                CellInfoCallback(executor)
            )

        } catch (e: NullPointerException) {
            //Workaround for OS crash in 'requestCellInfoUpdate' (On LG and OnePlus devices with Android 10). Fix for DIA-10011
            //While internal error is thrown, the passed parameter (Parcelable) is null, but OS is trying to invoke "getCause" on it anyway, which will result in NPE.
            EchoLocateLog.eLogE("Error: " + e.localizedMessage.toString())

            //In order to mimic expected result (in case of regular error), we will return empty array.
            setLacAndCellId(ArrayList())

        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    inner class CellInfoCallback(var executor: ThreadExecutor?) :
        TelephonyManager.CellInfoCallback() {

        override fun onCellInfo(cellInfo: MutableList<android.telephony.CellInfo>) {
            setLacAndCellId(cellInfo)
            if (executor?.handler != null && executor?.handler!!.looper != null) {
                executor?.handler!!.removeCallbacksAndMessages(null)
                executor?.handler!!.looper.quit()
            }
            resultSemaphore?.release()
        }

        override fun onError(errorCode: Int, detail: Throwable?) {
            EchoLocateLog.eLogE("CellInfoCallback error %d$errorCode")

            onCellInfo(ArrayList())
            if (executor?.handler != null && executor?.handler!!.looper != null) {
                executor?.handler!!.removeCallbacksAndMessages(null)
                executor?.handler!!.looper.quit()
            }
            resultSemaphore?.release()
        }

    }

    class ThreadExecutor : Executor {
        var looper: Looper =
            getHandlerWithHandlerThreadLooper("CellInfoUpdate")?.looper!!
        val handler = Handler(looper)
        override fun execute(r: Runnable) {
            handler.post(r)
            handler.looper.thread.uncaughtExceptionHandler =
                Thread.UncaughtExceptionHandler { t, e ->
                    EchoLocateLog.eLogE(
                        " Uncaught Exception error %s on thread %s" +
                                e.localizedMessage +
                                t.toString()
                    )

                    handler.looper.quitSafely()
                }
        }

        private fun getHandlerWithHandlerThreadLooper(name: String?): Handler? {
            val handlerThread = HandlerThread(name)
            handlerThread.start()
            val looper = handlerThread.looper
            return Handler(looper)
        }
    }
}