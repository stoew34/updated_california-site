package com.tmobile.mytmobile.echolocate.utils.logcat

import com.tmobile.mytmobile.echolocate.lte.utils.logcat.Shell
import org.junit.Before
import org.junit.Test
import java.io.IOException

class ShellTest {
    private var shell: Shell? = null

    @Before
    fun setUp() {
        shell = Shell.getInstance()
    }

    @Test(expected = IOException::class)
    @Throws(Exception::class)
    fun testExec() {
        shell?.exec(arrayOf("should throw exception"))
    }

}