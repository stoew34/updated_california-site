package com.tmobile.mytmobile.echolocate.utils

import android.os.Build

class DeviceAboveQCondition : ConditionalIgnoreRule.IgnoreCondition {

    override fun isSatisfied(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
    }
}

class DeviceBelowQCondition : ConditionalIgnoreRule.IgnoreCondition {
    override fun isSatisfied(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
    }
}
