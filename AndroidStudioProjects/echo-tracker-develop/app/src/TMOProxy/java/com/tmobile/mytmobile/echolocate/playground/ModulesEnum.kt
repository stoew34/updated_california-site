package com.tmobile.mytmobile.echolocate.playground

/**
 * enum that defines all the modules used in the echolocate app
 */
enum class ModulesEnum(val key: String) {
    VOICE("Voice"),
    LTE("Lte"),
    NR5G("Nr5g"),
    REPORTS("Reports"),
    ANALYTICS("Analytics"),
    COVERAGE("Coverage"),
    AUTH_TOKENS("AuthTokens"),
    USER_CONSENT("UserConsent"),
    LOCATION("Location"),
    CONFIGURATION("Configuration"),
    HEARTBEAT("HeartBeat"),
    AUTOUPDATE("AutoUpdate"),
    CRASHTEST("CrashTest"),
    COPYAPPDATA("CopyAppData"),
    DEVICEINFO("DeviceInfo"),
    OSS_LICENSES("OSS-Licenses"),
    FLAVOR_CONFIG("FlavorConfig")
}