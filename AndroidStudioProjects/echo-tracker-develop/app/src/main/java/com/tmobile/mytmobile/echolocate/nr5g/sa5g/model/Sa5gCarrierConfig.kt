package com.tmobile.mytmobile.echolocate.nr5g.sa5g.model

import com.google.gson.annotations.SerializedName

/**
 * Model class that holds Sa5gTrigger data to form a JSON
 *
 * Serialized name annotation for all variables to be in JSON,
 * and it will be helpful while doing progaurding
 */
data class Sa5gCarrierConfig(

    /**
     * Returns Carrier Config Version
     */
    @SerializedName("carrierConfigVersion")
    val carrierConfigVersion: String?,

    /**
     * Returns Band Config Keys
     * Example: "SAn2Enabled", "SAn66Enabled", "NONE", "ERROR"
     */
    @SerializedName("bandConfig")
    val bandConfig: List<Sa5gCarrierBandConfig>
)