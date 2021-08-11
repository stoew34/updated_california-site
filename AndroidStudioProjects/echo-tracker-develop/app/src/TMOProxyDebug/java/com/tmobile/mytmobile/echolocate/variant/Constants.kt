package com.tmobile.mytmobile.echolocate.variant

import com.tmobile.environmentsdk.Environment

class Constants {

    companion object{
        const val CONFIGURATION_URL =
            "https://apis.t-mobile.com/devcfg-prd/EchoLocate?device-group=Test-EchoLocate"

        val ENVIRONMENT = Environment.PROD.name

        const val DIA_REQUEST_URL =
            "https://trusted-collector-bk.tmocce.com/collector/0.5"

        val SAVE_DATA_TO_FILE: Boolean = true

    }
}