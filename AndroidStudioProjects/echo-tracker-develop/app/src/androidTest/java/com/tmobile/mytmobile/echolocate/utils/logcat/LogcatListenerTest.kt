package com.tmobile.mytmobile.echolocate.utils.logcat

import com.tmobile.mytmobile.echolocate.lte.utils.LogcatlistenerItem
import com.tmobile.mytmobile.echolocate.lte.utils.logcat.LogcatListener
import junit.framework.Assert.*
import org.junit.Before
import org.junit.Test

class LogcatListenerTest {

    private val ITEM =
        LogcatlistenerItem("ID", "ACTION", listOf(".*"), LogcatListener.Type.CONTINUOUS)
    private val ITEM1 =
        LogcatlistenerItem("ID2", "ACTION2", listOf(".*"), LogcatListener.Type.CONTINUOUS)


    private var logcatListener: LogcatListener? = null


    @Before
    @Throws(Exception::class)
    fun setUp() {
        logcatListener = LogcatListener.getInstance()
    }


    @Test
    fun testAddListener() {
        logcatListener?.start()
        logcatListener?.addListener(ITEM)
        logcatListener?.addListener(ITEM1)
        logcatListener?.removeListener(ITEM)
        logcatListener?.isEmptyListenersList()?.let { assertFalse(it) }
    }

//    @Test
//    fun testRemoveListener() {
//        logcatListener?.start()
//        logcatListener?.removeListener(ITEM)
//
//        logcatListener?.isRunning()?.let { assertFalse(it) }
//        logcatListener?.isEmptyListenersList()?.let { assertTrue(it) }
//    }


    @Test
    fun testRemoveAllListeners() {
        logcatListener?.start()
        logcatListener?.removeAllListeners()
        logcatListener?.isRunning()?.let { assertFalse(it) }
        logcatListener?.isEmptyListenersList()?.let { assertTrue(it) }
    }

    @Test
    fun testStop() {
        logcatListener?.start()
        logcatListener?.stop()
        logcatListener?.isRunning()?.let { assertFalse(it) }
    }

}