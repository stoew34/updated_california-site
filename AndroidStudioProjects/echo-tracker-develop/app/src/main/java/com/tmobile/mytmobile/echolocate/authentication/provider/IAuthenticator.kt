package com.tmobile.mytmobile.echolocate.authentication.provider

import com.tmobile.mytmobile.echolocate.authentication.datevents.DatUpdateEvent
import io.reactivex.Observable

/**
 * Contract for Authentication provider.
 */
interface IAuthenticator {

    /**
     *  Initializes the authentication module which fetches DAT and persist locally.
     */
    fun initialize()

    /**
     * API to check the validity/expiration of the token passed as an argument of the API.
     * @param token
     * @return isTokenExpired
     */
    fun isTokenExpired(token: String): Boolean

    /**
     * API to check the validity/expiration of token stored locally.
     * @return isTokenExpired
     */
    fun isLocallyStoredTokenExpired(): Boolean


    /**
     * API to get valid/non-expired DAT token
     * @return token
     */
    fun getToken(tokenReceivedListener: ITokenReceivedListener? = null): String?

    /**
     * API to refresh DAT token
     * @return token
     */
    fun refreshToken(tokenReceivedListener: ITokenReceivedListener? = null)

    fun getDatUpdate() : Observable<DatUpdateEvent>

}