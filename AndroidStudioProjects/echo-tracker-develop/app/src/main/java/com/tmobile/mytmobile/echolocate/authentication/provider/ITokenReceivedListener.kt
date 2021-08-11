package com.tmobile.mytmobile.echolocate.authentication.provider

/**
 * Contract when the token is received.
 */
interface ITokenReceivedListener {

    fun onReceivedToken(token: String)

}