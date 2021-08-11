package com.tmobile.mytmobile.echolocate.lte.utils

import android.content.Intent
import org.junit.Assert
import org.junit.Test

class LteUtilsTest {

    /**
     * this will check the timer object created or not
     */
    @Test
    fun isPackageEligibleForDataCollection() {
        val intent = Intent("diagandroid.app.ApplicationState")
        val packagesEnabled: List<String>? = null
        Assert.assertEquals(null,
            packagesEnabled?.let { LteUtils.isPackageEligibleForDataCollection(intent, it) })
        Assert.assertEquals(ApplicationState.UNSUPPORTED.name, LteUtils.extractApplicationStateFromIntent(intent).name)
    }

}