package com.tmobile.mytmobile.echolocate.voice

/**
 * Created by Divya Mittal on 4/13/21
 */
import android.content.ContentResolver
import android.content.Context
import android.telephony.TelephonyManager
import com.tmobile.mytmobile.echolocate.voice.utils.VoiceDeviceInfoDataCollector
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import org.junit.Ignore
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock


class VoiceDeviceInfoDataCollectorTest {

    var deviceInfoDatacollector = VoiceDeviceInfoDataCollector()

    @Ignore
    @Test
    fun testGetDeviceInformation() {
        val context = mock(Context::class.java)
        val mockTelephonyManager = mock(TelephonyManager::class.java)
        val contentResolver = mock(ContentResolver::class.java)
        `when`(context.contentResolver).thenReturn(contentResolver)

        `when`(context.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager)
        `when`(mockTelephonyManager.subscriberId).thenReturn("T-Mobile")
        `when`(mockTelephonyManager.line1Number).thenReturn("675467898435")
        `when`(mockTelephonyManager.deviceId).thenReturn("ecw57689")
        `when`(mockTelephonyManager.imei).thenReturn("ecw57689")


        val deviceInfo = deviceInfoDatacollector.getDeviceInformation(context)

        assertNotNull(deviceInfo)
        assertEquals("T-Mobile", deviceInfo.imsi)
        assertEquals("ecw57689", deviceInfo.imei)
        assertEquals("675467898435", deviceInfo.msisdn)
    }

    @Test
    fun testIsOreoDevice() {

    }

    @Test
    fun testGetImei() {


    }

    @Test
    fun testGetImsi() {

    }


    @Test
    fun testGetMsisdn() {


    }

    @Test
    fun testGetTestSessionID() {

    }


}