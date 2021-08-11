package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model

/**
 * Base class that holds actual trigger, used by delegate
 *
 * Serialized name annotation for all variables to be in JSON,
 * and it will be helpful while doing progaurding
 */
data class BaseNr5gTriggerData(


    val triggerDelay: Long,

    val triggerCode: Int,

    val triggerAction: String

)