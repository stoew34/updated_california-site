package com.tmobile.mytmobile.echolocate.playground

/**
 * enum that defines all the modules used in the echolocate app
 */
enum class OEMToolEnum(val key: String) {
    SA5G_DATAMETRICS("5G SA DataMetrics Tool"),
    NSA_5G_DATAMETRICS("NSA 5G DataMetrics Tool"),
    LTE_DATAMETRICS("LTE DataMetrics Tool"),
    VOICEPOC("Voice Tool"),
    ADBCOMMANDS("Adb Commands"),
    ANDROID_PUBLIC_API("TestPublicAPI"),
    LOCATION("Location")
}