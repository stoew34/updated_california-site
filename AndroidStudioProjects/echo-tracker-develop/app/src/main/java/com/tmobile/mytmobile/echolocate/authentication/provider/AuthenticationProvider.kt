package com.tmobile.mytmobile.echolocate.authentication.provider

import android.content.Context
import android.text.TextUtils
import com.tmobile.mytmobile.echolocate.authentication.AuthenticationManager
import com.tmobile.mytmobile.echolocate.authentication.datevents.DatUpdateEvent
import com.tmobile.mytmobile.echolocate.utils.SingletonHolder
import io.reactivex.Observable

/**
 * class is responsible for providing the valid DAT token for the calling modules.
 */
class AuthenticationProvider private constructor(val context: Context) : IAuthenticator {

    companion object : SingletonHolder<AuthenticationProvider, Context>(::AuthenticationProvider){
        const val REFRESH_TOKEN_COMPONENT_NAME = "RefreshAuthToken"
    }

    /**
     *  Initializes the authentication module which fetches DAT and persist locally.
     */
    override fun initialize() {
        AuthenticationManager.getInstance(context).initAgentAndGetDatSilent()
    }

    /**
     * API to get valid/non-expired DAT token.
     * @return token
     */
    override fun getToken(tokenReceivedListener: ITokenReceivedListener?): String? {
        val authenticationManager = AuthenticationManager.getInstance(context)
        val token = authenticationManager.getSavedToken()
        if (!authenticationManager.checkIfTokenExpired(token)) {
            tokenReceivedListener.let {
                tokenReceivedListener!!.onReceivedToken(token)
            }
            return token
        }
        authenticationManager.initAgentAndGetDatSilent(tokenReceivedListener)
        return null
    }

    /**
     * API to check the validity/expiration of token stored locally.
     * @return isTokenExpired
     */
    override fun isLocallyStoredTokenExpired(): Boolean {
        val authenticationManager = AuthenticationManager.getInstance(context)
        val datToken = authenticationManager.getSavedToken()
        if (!TextUtils.isEmpty(datToken)) {
            return authenticationManager.checkIfTokenExpired(datToken)
        }
        return true
    }

    /**
     * API to check the validity/expiration of the token passed as an argument of the API.
     * @param token
     * @return isTokenExpired
     */
    override fun isTokenExpired(token: String): Boolean {
        return AuthenticationManager.getInstance(context).checkIfTokenExpired(token)
    }

    /**
     * API to refresh the token.
     * @param tokenReceivedLlistener
     */
    override fun refreshToken(tokenReceivedListener: ITokenReceivedListener?) {
        AuthenticationManager.getInstance(context).initAgentAndGetDatSilent(tokenReceivedListener)
    }

    override fun getDatUpdate(): Observable<DatUpdateEvent> {
        return AuthenticationManager.getInstance(context).getDatUpdate()
    }

}