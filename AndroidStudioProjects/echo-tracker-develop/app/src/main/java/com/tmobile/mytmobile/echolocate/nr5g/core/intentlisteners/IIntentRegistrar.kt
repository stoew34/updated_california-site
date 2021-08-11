package com.tmobile.mytmobile.echolocate.nr5g.core.intentlisteners

/**
 * Registers intent actions to register broadcasts dynamically
 */
interface IIntentRegistrar {

    /**
     * Adds all the actions to a list to enable dynamic registration of
     * broadcast receivers
     */
    fun registerNr5gAppReceiver(intentActions: MutableList<String>)
    fun registerNr5gScreenReceiver(intentActions: MutableList<String>)
}