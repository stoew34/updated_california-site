package com.tmobile.mytmobile.echolocate.utils

import org.junit.Assert
import org.junit.Test
import java.util.*

class EchoLocateDateUtilsTest {

    @Test
    fun testGetDateBeforeDay() {
        val date = EchoLocateDateUtils.getDateBeforeDays(7)
        Assert.assertNotNull(date)
    }

    @Test
    fun testParseDateSuccess() {
        val flag = EchoLocateDateUtils.isDateParsable("10/01/2019")
        Assert.assertTrue(flag)
    }

    @Test
    fun testParseDateFailure() {
        val flag = EchoLocateDateUtils.isDateParsable("10//2019")
        Assert.assertFalse(flag)
    }

    @Test
    fun `converting hours to days test`() {
        val zeroHours = 0
        val sixHours = 6
        val twelveHours = 12
        val twentyFourHours = 24
        val fortyEightHours = 48

        Assert.assertEquals(0, EchoLocateDateUtils.convertHoursToDays(zeroHours))
        Assert.assertEquals(0, EchoLocateDateUtils.convertHoursToDays(sixHours))
        Assert.assertEquals(0, EchoLocateDateUtils.convertHoursToDays(twelveHours))
        Assert.assertEquals(1, EchoLocateDateUtils.convertHoursToDays(twentyFourHours))
        Assert.assertEquals(2, EchoLocateDateUtils.convertHoursToDays(fortyEightHours))

    }

//    @Test
//    fun `converting date from date-only-string to long format test`() {
//        Assert.assertEquals(1603080000000, EchoLocateDateUtils.convertStringToLong("10/19/2020"))
//    }


    //This test is timezone dependent
//    @Test
//    fun `converting ISO8601 to schema date format test`() {
//        Assert.assertEquals(
//            "2020-10-19T00:00:00.000-0400",
//            EchoLocateDateUtils.convertToShemaDateFormat(1603080000000.toString())
//        )
//    }

    //This test is timezone dependent
//    @Test
//    fun `converting timestamp to ISO8601 format test`() {
//        Assert.assertEquals(
//            "2020-10-19T00:00:00.000-0400",
//            EchoLocateDateUtils.getFormattedTime(1603080000000)
//        )
//    }

//    @Test
//    fun convertFileNameFormatTest() {
//        val date = Date(1603080000000)
//        //This method we are testing gives back the date in 12 hour format
//        Assert.assertEquals("10192020-12-00-00", EchoLocateDateUtils.convertToFileNameFormat(date))
//    }

}