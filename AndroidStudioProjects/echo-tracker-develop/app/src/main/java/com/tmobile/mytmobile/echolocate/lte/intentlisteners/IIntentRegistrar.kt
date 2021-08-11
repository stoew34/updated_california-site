package com.tmobile.mytmobile.echolocate.lte.intentlisteners

/**
 * Registers intent actions to register broadcasts dynamically
 */
interface IIntentRegistrar {

    /**
     * Adds all the actions to a list to enable dynamic registration of
     * broadcast receivers
     */
    fun registerLteReceiver(intentActions: MutableList<String>)
}