package com.tmobile.mytmobile.echolocate.reporting

/**
 * Created by Divya Mittal on 4/21/21
 */
import android.content.Context
import com.tmobile.mytmobile.echolocate.network.INetworkProvider
import com.tmobile.mytmobile.echolocate.network.NetworkManager
import com.tmobile.mytmobile.echolocate.network.model.DIARequest
import com.tmobile.mytmobile.echolocate.network.model.NetworkRequestHeader
import com.tmobile.mytmobile.echolocate.network.model.NetworkRetryPrefs
import com.tmobile.mytmobile.echolocate.network.result.NetworkResponseDetails
import io.reactivex.Observable

internal class NetworkProvider private constructor(val context: Context) :
    INetworkProvider {

    companion object {
        @Volatile
        private var INSTANCE: INetworkProvider? = null

        /***
         * access to singleton NetworkProvider object
         */
        fun getInstance(context: Context): INetworkProvider {
            return INSTANCE
                ?: synchronized(this) {
                    val instance: INetworkProvider =
                        NetworkProvider(
                            context
                        )
                    INSTANCE = instance
                    instance
                }
        }
    }

    /**
     * API implementation for performing network operations
     *
     * @param diaRequest
     * @param headerMap
     * @param networkRetryPrefs
     * @param context
     * @return Observable<NetworkResponseDetails>
     */
    override fun performNetworkOperations(
        diaRequest: DIARequest,
        headerMap: NetworkRequestHeader,
        networkRetryPrefs: NetworkRetryPrefs,
        datToken: String,
        context: Context
    ): Observable<NetworkResponseDetails> {
        return NetworkManager().performNetworkOperations(
            diaRequest,
            headerMap,
            networkRetryPrefs,
            datToken,
            context
        )
    }

    override fun performNetworkOperationsUsingRxBus(
        diaRequest: DIARequest,
        headerMap: NetworkRequestHeader,
        networkRetryPrefs: NetworkRetryPrefs,
        datToken: String,
        context: Context
    ) {
        NetworkManager().performNetworkOperationsUsingRxBus(
            diaRequest,
            headerMap,
            networkRetryPrefs,
            datToken,
            context
        )
    }

    override fun setLogLevel(level: Int) {
        //already set in echo app
    }

    /**
     * Removes the old failed network request from the database
     * Currently, we are deleting the network requests older than 5 days
     */
    override fun cleanOldFailedNetworkRequests(context: Context) {
        NetworkManager().cleanOldFailedNetworkRequests(context)
    }
}


