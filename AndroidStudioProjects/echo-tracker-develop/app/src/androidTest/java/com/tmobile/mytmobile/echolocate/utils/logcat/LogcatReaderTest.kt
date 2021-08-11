//package com.tmobile.mytmobile.echolocate.utils.logcat
//
//import junit.framework.Assert.assertNotNull
//import org.junit.Before
//import org.junit.FixMethodOrder
//import org.junit.Test
//import org.junit.runners.MethodSorters
//
///**
// * This test class responsible to provide the implementation logic for logcat reader
// *
// */
//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
//class LogcatReaderTest {
//
//    private var reader: LogcatReader? = null
//    @Before
//    @Throws(Exception::class)
//    fun setUp() {
//
//        reader = LogcatReader.getInstance()
//
//
//    }
//
//
//    @Test
//    @Throws(Exception::class)
//    fun testA_ReadLine() {
//        assertNotNull(reader?.readLine())
//    }
//
//    @Test
//    @Throws(Exception::class)
//    fun testB_destroy() {
//        reader?.readLine()
//        reader?.destroy()
//
//    }
//}