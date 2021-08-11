package com.tmobile.mytmobile.echolocate.voice.utils

import org.junit.Test

class CallStateTest {

    @Test
    fun testCallState() {
        assert(CallState.valueOf("ATTEMPTING").key == "ATTEMPTING")
        assert(CallState.valueOf("ESTABLISHED").key == "ESTABLISHED")
        assert(CallState.valueOf("CONNECTED").key == "CONNECTED")
        assert(CallState.valueOf("DISCONNECTING").key == "DISCONNECTING")
        assert(CallState.valueOf("FAILED").key == "FAILED")
        assert(CallState.valueOf("HELD").key == "HELD")
        assert(CallState.valueOf("ENDED").key == "ENDED")
        assert(CallState.valueOf("INCOMING").key == "INCOMING")
        assert(CallState.valueOf("MUTED").key == "MUTED")
        assert(CallState.valueOf("UNMUTED").key == "UNMUTED")
        assert(CallState.valueOf("CSFB_STARTED").key == "CSFB_STARTED")
        assert(CallState.valueOf("CSFB_SUCCESSFUL").key == "CSFB_SUCCESSFUL")
        assert(CallState.valueOf("CSFB_FAILED").key == "CSFB_FAILED")
        assert(CallState.valueOf("SRVCC_STARTED").key == "SRVCC_STARTED")
        assert(CallState.valueOf("RVCC_SUCCESSFUL").key == "RVCC_SUCCESSFUL")
        assert(CallState.valueOf("ASRVCC_STARTED").key == "ASRVCC_STARTED")
        assert(CallState.valueOf("ASRVCC_SUCCESSFUL").key == "ASRVCC_SUCCESSFUL")
        assert(CallState.valueOf("ASRVCC_FAILED").key == "ASRVCC_FAILED")
        assert(CallState.valueOf("EPDG_HO_STARTED").key == "EPDG_HO_STARTED")
        assert(CallState.valueOf("EPDG_HO_SUCCESSFUL").key == "EPDG_HO_SUCCESSFUL")
        assert(CallState.valueOf("EPDG_HO_FAILED").key == "EPDG_HO_FAILED")
    }
}