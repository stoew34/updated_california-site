package com.tmobile.mytmobile.echolocate.nr5g.sa5g.dataprocessor

import android.content.Context
import android.net.NetworkCapabilities
import com.tmobile.mytmobile.echolocate.nr5g.core.utils.Nr5gUtils
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity.Sa5gActiveNetworkEntity
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.model.BaseSa5gData
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.model.BaseSa5gMetricsData
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.model.Sa5gActiveNetwork
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.utils.Sa5gConstants
import com.tmobile.mytmobile.echolocate.utils.DevLogUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.pr.androidcommon.system.SystemService
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class Sa5gActiveNetworkProcessor(val context: Context) : Sa5gBaseDataProcessor(context) {

    private val tag: String = this.javaClass.simpleName
    private var sa5gActiveNetworkDisposable: Disposable? = null

    /**
     * Expected size of the source received from data metrics
     * @return Int returns the size of the source
     */
    override fun getExpectedSourceSize(): Int {
        return Sa5gConstants.SA5G_ACTIVE_NETWORK_EXPECTED_SIZE
    }

    /**
     * This function processes active network and return disposable
     *  @param baseSa5gMetricsData: BaseSa5gMetricsData
     *  @param baseSa5gData: BaseSa5gData
     */
    override suspend fun processSa5gMetricsData(
        baseSa5gMetricsData: BaseSa5gMetricsData,
        baseSa5gData: BaseSa5gData
    ): Disposable? {
            sa5gActiveNetworkDisposable =  Observable.just(saveActiveNetworkData(baseSa5gMetricsData,
                baseSa5gData)).subscribeOn(
                Schedulers.io()).subscribe()
            return sa5gActiveNetworkDisposable
        }

    /**
     *  This fun checks the active network Data and saves to DB
     *  @param baseSa5gMetricsData: total metrics data to be processed
     *  @param baseSa5gData: [BaseSa5gData]
     */
    private fun saveActiveNetworkData(baseSa5gMetricsData: BaseSa5gMetricsData, baseSa5gData: BaseSa5gData){
        val connectivityManager = SystemService.getConnectivityManager(context)

        var networkCapabilities: NetworkCapabilities? = null
        try {
            networkCapabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        } catch (ex: SecurityException) {
            Nr5gUtils.sendCrashReportToFirebase(
                "Exception in sa5gActiveNetworkProcessor : saveActiveNetworkData()",
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

        val sa5gActiveNetwork = Sa5gActiveNetwork(networkType)
        val sa5gActiveNetworkEntity = Sa5gActiveNetworkEntity(sa5gActiveNetwork.getActiveNetwork)

        sa5gActiveNetworkEntity.sessionId = baseSa5gData.sessionId
        sa5gActiveNetworkEntity.uniqueId = baseSa5gData.uniqueId

        CoroutineScope(IO).launch {
            saveSa5gActiveNetworkToDatabase(sa5gActiveNetworkEntity)
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
     * saves the Sa5gActiveNetworkEntity object to database
     * @param sa5gActiveNetworkEntity: [Sa5gActiveNetworkEntity] the object to save
     */
    private fun saveSa5gActiveNetworkToDatabase(sa5gActiveNetworkEntity: Sa5gActiveNetworkEntity) {
        sa5gRepository.insertSa5gActiveNetworkEntity(sa5gActiveNetworkEntity)
    }
}


