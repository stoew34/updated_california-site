package com.tmobile.mytmobile.echolocate.utils

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DevLogUtilsTest {


    @Test
    fun storeIntentDataIntoAFileReturnSuccess() {
        val downloadIntent = Intent(Intent.ACTION_ANSWER)
        DevLogUtils.getIntentData(downloadIntent)
    }


}