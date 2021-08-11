package com.tmobile.mytmobile.echolocate.coverage.utils

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.telephony.ServiceState
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.pr.androidcommon.system.SystemService
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future

class DataCollectionService {
    fun getServiceState(context: Context): ServiceState? {
        var serviceState = getServiceStateSync(context)
        if (serviceState == null) {
            serviceState = getServiceStateAsync(context)
        }
        return serviceState
    }

    /**
     * Asynchronous call for the Service State object
     * Uses a ExecutorService and a Callable with a 700ms wait lock for receiving the callback from the listener.
     * get a service state object when returned
     */
    private fun getServiceStateAsync(context: Context): ServiceState? {
        val executor =
            Executors.newFixedThreadPool(2)
        val coverageCallable = CoverageStatusFutureCallable(context)
        val result: Future<ServiceState> = executor.submit(coverageCallable)
        try {
            return result.get()
        } catch (e: ExecutionException) {
            EchoLocateLog.eLogE("Diagnostic: Coverage Nr DataCollector - $e")
        } catch (e: InterruptedException) {
            EchoLocateLog.eLogE("Diagnostic: Coverage Nr DataCollector - $e")
        }
        return null
    }

    /**
     * Synchronous call for the Servicestate from telephonyManager
     * Returns servicestate from telephony manager
     * Returns null if required permission is not present.
     */
    @SuppressLint("MissingPermission")
    @TargetApi(Build.VERSION_CODES.O)
    private fun getServiceStateSync(context: Context): ServiceState? {
        val telephonyManager = SystemService.getTelephonyManager(context)
        if (telephonyManager == null
            || !CoverageUtils.checkLocationPermission(context)
            || !CoverageUtils.checkPhonePermission(context)) {
            return null
        }

        return telephonyManager.serviceState
    }
}