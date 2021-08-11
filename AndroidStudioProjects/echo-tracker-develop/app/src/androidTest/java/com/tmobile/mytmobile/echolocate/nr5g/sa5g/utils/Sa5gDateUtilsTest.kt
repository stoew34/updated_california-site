package com.tmobile.mytmobile.echolocate.nr5g.sa5g.utils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import org.junit.Test

class Sa5gDateUtilsTest {

    @Test
    fun testEmptyStringConvertToShemaDateFormat() {
        val date = EchoLocateDateUtils.convertToShemaDateFormat("")
        assert(date.isBlank());
    }

    @Test
    fun testconvertToShemaDateFormat() {
        val date = EchoLocateDateUtils.convertToShemaDateFormat(System.currentTimeMillis().toString())
        assert(date.isNotBlank());
    }

    @Test
    fun testgetTriggerTimeStamp() {
        val date = EchoLocateDateUtils.getTriggerTimeStamp()
        assert(date.isNotBlank());
    }
}