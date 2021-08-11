package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.dataprocessor

import android.content.Context
import android.net.NetworkCapabilities
import com.tmobile.mytmobile.echolocate.coverage.utils.CoverageUtils
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gUtils
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.Nr5gActiveNetworkEntity
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.BaseNr5gData
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.BaseNr5gMetricsData
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.Nr5gActiveNetwork
import com.tmobile.mytmobile.echolocate.utils.DevLogUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.pr.androidcommon.system.SystemService
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class Nsa5gActiveNetworkProcessor(val context: Context) : Nsa5gBaseDataProcessor(context) {

    private val tag: String = this.javaClass.simpleName
    private var nsa5gActiveNetworkDisposable: Disposable? = null
    /**
     * Expected size of the source received from data metrics
     * @return Int returns the size of the source
     */
    override fun getExpectedSourceSize(): Int {
        //This should not be forced on all child's of Nsa5gBaseDataProcessor
        //Not valid as this is nothing to do with Data metrics
        return 0
    }

    /**
     *  This function processes network Data and return disposable
     *  @param baseNr5gMetricsData: BaseNr5gMetricsData
     *  @param baseNr5gData: BaseNr5gData
     */
    override suspend fun processNr5gMetricsData(
        baseNr5gMetricsData: BaseNr5gMetricsData,
        baseNr5gData: BaseNr5gData
    ): Disposable? {
        nsa5gActiveNetworkDisposable =  Observable.just(saveNsa5gActiveNetworkData(baseNr5gMetricsData,
            baseNr5gData)).subscribeOn(
            Schedulers.io()).subscribe()
        return nsa5gActiveNetworkDisposable
    }

    /**
     *  This fun checks the active network Data and saves to DB
     *  @param baseNr5gMetricsData: BaseNr5gMetricsData
     *  @param baseNr5gData: BaseNr5gData
     */
    private fun saveNsa5gActiveNetworkData(baseNr5gMetricsData: BaseNr5gMetricsData, baseNr5gData: BaseNr5gData) {
        val connectivityManager = SystemService.getConnectivityManager(context)

        var networkCapabilities: NetworkCapabilities? = null
        try {
            networkCapabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        } catch (ex: SecurityException) {
            Nr5gUtils.sendCrashReportToFirebase(
                "Exception in Nsa5gActiveNetworkProcessor : saveNsa5gActiveNetworkData()",
                ex.localizedMessage,
                "SecurityException"
            )
        }

        var networkType = -1

        for (nt in getNetworkTypes()) {
            if (networkCapabilities != null && networkCapabilities.hasTransport(nt)) {
                networkType = nt
                break
            }
        }
        EchoLocateLog.eLogD("active network type = $networkType")

        val activeNetworkType = Nr5gActiveNetwork(networkType)
        val activeNetworkEntity = Nr5gActiveNetworkEntity(activeNetworkType.getActiveNetwork)

        activeNetworkEntity.sessionId = baseNr5gData.sessionId
        activeNetworkEntity.uniqueId = baseNr5gData.uniqueId

        CoroutineScope(IO).launch {
            saveNr5gActiveNetworkToDatabase(activeNetworkEntity)
        }
    }

    private fun getNetworkTypes(): IntArray {
        return intArrayOf(
            NetworkCapabilities.TRANSPORT_WIFI_AWARE,
            NetworkCapabilities.TRANSPORT_WIFI,
            NetworkCapabilities.TRANSPORT_VPN,
            NetworkCapabilities.TRANSPORT_LOWPAN,
            NetworkCapabilities.TRANSPORT_ETHERNET,
            NetworkCapabilities.TRANSPORT_CELLULAR,
            NetworkCapabilities.TRANSPORT_BLUETOOTH
        )
    }

    /**
     * saves the Nr5gActiveNetworkEntity object to database
     * @param nr5gActiveNetworkEntity: [Nr5gActiveNetworkEntity] the object to save
     */
    private fun saveNr5gActiveNetworkToDatabase(nr5gActiveNetworkEntity: Nr5gActiveNetworkEntity) {
        nr5gRepository.insertNr5gActiveNetwork(nr5gActiveNetworkEntity)
    }
}