package com.tmobile.mytmobile.echolocate.coverage.delegates

/**
 * Created by Mahesh Shetye on 2020-04-23
 *
 * Trigger event source.
 */

enum class TriggerSource(val key: Int) {

    /**
     * Unknown trigger. Should not normally occur.
     */
    UNKNOWN(0),

    /**
     * Screen was turned on or off.
     */
    SCREEN_ACTIVITY(1),

    /**
     * Data session attempt was made.
     */
    DATA_SESSION_ATTEMPT(2),

    /**
     * Data session ended.
     */
    DATA_SESSION_END(3),

    /**
     * Voice call started (incoming or outgoing).
     */
    VOICE_CALL_STARTED(4),

    /**
     * Voice call ended (incoming or outgoing).
     */
    VOICE_CALL_ENDED(5),

    /**
     * Device diagnostic was performed.
     */
    DIAGNOSTIC_PERFORMED(6)
}