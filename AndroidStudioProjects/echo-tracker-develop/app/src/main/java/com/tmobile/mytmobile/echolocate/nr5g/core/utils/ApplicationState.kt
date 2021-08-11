package com.tmobile.mytmobile.echolocate.nr5g.core.utils

enum class ApplicationState {
    /**
     *
    internal var state: Unsupported? = null.
     */
    UNSUPPORTED,

    /**
     * Application gained focus.
     */
    FOCUS_GAIN,

    /**
     * Application lost focus.
     */
    FOCUS_LOSS,

    /**
     * Device screen OFF.
     */
    SCREEN_OFF,

    /**
     * Periodic triggers.
     */
    PERIODIC,

    /**
     * Device screen ON.
     */
    SCREEN_ON_CODE,

    /**
     * Hourly code.
     */
    HOURLY_CODE

}