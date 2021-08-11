package com.tmobile.mytmobile.echolocate.nr5g.core.utils

//import com.tmobile.mytmobile.echolocate.coverage.utils.CoverageUtils
import android.content.Context
import android.os.Looper
import android.telephony.PhoneStateListener
import android.telephony.ServiceState
import com.tmobile.mytmobile.echolocate.utils.ELDeviceUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.pr.androidcommon.system.SystemService
import java.util.concurrent.Callable
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

/**
 * Allows for the implementation of the Samsung OEM API "getNrStatus"
 *
 * Implements the executor service by creating a callable future.
 */

class Nr5gStatusFutureCallable(val context: Context) : Callable<ServiceState> {
    var serviceState: ServiceState? = null
    var semaphore: Semaphore? = null

    @Throws(Exception::class)
    override fun call(): ServiceState? {
        serviceState = null
        if (ELDeviceUtils.isQDeviceOrHigher()) {
            if (!Nr5gUtils.checkLocationPermission(context)) {
                EchoLocateLog.eLogD("ACCESS_FINE_LOCATION is not granted")
                return null
            }
        }
        //PhoneStateListener requires Looper.myLooper() not return null.
        Looper.prepare()
        val manager = SystemService.getTelephonyManager(context!!)
        val phoneListener: PhoneStateListener = object : PhoneStateListener() {
            override fun onServiceStateChanged(serviceStateUpdated: ServiceState) {
                serviceState = serviceStateUpdated
                //we recieved the servicestate object and we no longer needs any more messages
                Looper.myLooper()?.quit()
                //release the semaphore
                semaphore!!.release()
            }
        }
        semaphore = Semaphore(0)
        manager.listen(phoneListener, PhoneStateListener.LISTEN_SERVICE_STATE)
        //connect the message que to the looper
        Looper.loop()
        //Waiting a total of 700ms for the listener to return.
        semaphore!!.tryAcquire(700, TimeUnit.MILLISECONDS)
        return serviceState
    }
}

