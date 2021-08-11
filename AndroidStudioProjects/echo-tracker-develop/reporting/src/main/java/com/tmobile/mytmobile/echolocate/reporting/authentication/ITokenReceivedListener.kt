package com.tmobile.mytmobile.echolocate.reporting.authentication

/**
 * Contract when the token is received.
 */
interface ITokenReceivedListener {

    fun onReceivedToken(token: String)

}