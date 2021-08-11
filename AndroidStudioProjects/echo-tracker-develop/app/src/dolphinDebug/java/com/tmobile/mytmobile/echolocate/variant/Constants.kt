package com.tmobile.mytmobile.echolocate.variant

import com.tmobile.environmentsdk.Environment

class Constants {

    companion object{
        const val CONFIGURATION_URL =
            "https://api-devstg.t-mobile.com/devcfg/EchoLocate?device-group=EchoLocate-Dolphin"

        val ENVIRONMENT = Environment.STAGING.name

        const val DIA_REQUEST_URL =
            "https://trusted-collector-bk.preprod.tmocce.com/collector/0.5"

        val SAVE_DATA_TO_FILE: Boolean = true

    }
}