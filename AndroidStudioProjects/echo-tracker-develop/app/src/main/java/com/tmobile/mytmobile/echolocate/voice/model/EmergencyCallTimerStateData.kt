package com.tmobile.mytmobile.echolocate.voice.model

/**
 * EmergencyCallTimerState INTENT ENTRY DATA
 * OEM shall broadcast an "diagandroid.phone.emergencyCallTimerState" message
 * whenever the state of E1, E2, or E3 timer for E911call on a device is changed.
 */
data class EmergencyCallTimerStateData(

    /**
     * TimerName such as E1, E2, or E3.
     * For example, if an E911 call attempted in IMS, the E1 timer will be initiated.
     * Then, report this intent with TimerName set to "E1"
     */
    val timerName: String,

    /**
     * TimerState. TimerState sets STARTED, CANCELLED, EXPIRED.
     * The STARTED means that a timer just started. The CANCELLED means that the timer
     * was just cancelled before it was expired (for example, the expiry times at the time of
     * writing this TRD are 10 seconds for E1, 5 seconds for E2, and 25 seconds for E3).
     * The EXPIRED means that the timer was terminated because it reached the expiry time.
     * For instance, if the E1 timer (if the expiry time is 10 seconds) was terminated
     * after 10 seconds, report 'EXPIRED'. The expiry time may change in the future and
     * will be handled in a different TRD.
     */
    val timerState: String,

    /**
     * Timestamp when the event is received by the application.
     * Returns ISO timestamp with format yyyy-MM-dd'T'HH:mm:ss.SSSZ.
     * For example: 2018-05-16T16:14:10.456-0700
     */
    var eventTimestamp: String,

    /**
     * Convert the timestamp as received in the intent from the OEMs to ISO timestamp
     * with format yyyy-MM-dd'T'HH:mm:ss.SSSZ. For example: 2018-05-16T16:14:10.456-0700
     */
    val oemTimestamp: String,

    /**
     * Base event Info data
     */
    var eventInfo: EventInfo?
)