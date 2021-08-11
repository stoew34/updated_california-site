package com.tmobile.mytmobile.echolocate.lte.utils

enum class LTEApplications(private val packageName: String, private val key: String) {
    YOUTUBE("com.google.android.youtube", "youtube"),
    FACEBOOK("com.facebook.katana", "facebook"),
    INSTAGRAM("com.instagram.android", "instagram"),
    SPEED_TEST("org.zwanoo.android.speedtest", "speedtest"),
    YOUTUBE_TV("com.google.android.apps.youtube.unplugged", "youtubetv"),
    NETFLIX("com.netflix.mediaclient", "netflix");


    fun getPackageName(): String {
        return packageName
    }

    fun getKey(): String {
        return key
    }

}
