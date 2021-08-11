package com.tmobile.mytmobile.echolocate.voice.utils

/**
 * Enum that holds the call state
 */
enum class CallState(val key: String) {

    /**
     * ATTEMPTING
     *
     * enum used is used to check call attempting
     * value ATTEMPTING
     */
    ATTEMPTING("ATTEMPTING"),
    /**
     * ESTABLISHED
     *
     * enum used is used to check call established
     * value ESTABLISHED
     */
    ESTABLISHED("ESTABLISHED"),
    /**
     * CONNECTED
     *
     * enum used is used to check call connected or not
     * value CONNECTED
     */
    CONNECTED("CONNECTED"),
    /**
     * DISCONNECTING
     *
     * enum used is used to check call disconnecting or not
     * value DISCONNECTING
     */
    DISCONNECTING("DISCONNECTING"),
    /**
     * FAILED
     *
     * enum used is used to check call failed
     * value FAILED
     */
    FAILED("FAILED"),
    /**
     * HELD
     *
     * enum used is used to check call held
     * value HELD
     */
    HELD("HELD"),
    /**
     * ENDED
     *
     * enum used is to check call state ended or not
     * value ENDED
     */
    ENDED("ENDED"),
    /**
     * ENDED
     *
     * enum used is to check call state ended or not
     * value ENDED
     */
    INCOMING("INCOMING"),
    /**
     * MUTED
     *
     * enum used is to check call state muted or not
     * value MUTED
     */
    MUTED("MUTED"),
    /**
     * UNMUTED
     *
     * enum used is to check call state unmuted
     * value UNMUTED
     */
    UNMUTED("UNMUTED"),
    /**
     * CSFB_STARTED
     *
     * enum used is to check call state csfb started
     * value CSFB_STARTED
     */
    CSFB_STARTED("CSFB_STARTED"),
    /**
     * CSFB_SUCCESSFUL
     *
     * enum used is to check call state csfb successful
     * value CSFB_SUCCESSFUL
     */
    CSFB_SUCCESSFUL("CSFB_SUCCESSFUL"),
    /**
     * CSFB_FAILED
     *
     * enum used is to check call state csfb failed
     * value CSFB_FAILED
     */
    CSFB_FAILED("CSFB_FAILED"),
    /**
     * SRVCC_STARTED
     *
     * enum used is to check call state srvcc started
     * value SRVCC_STARTED
     */
    SRVCC_STARTED("SRVCC_STARTED"),
    /**
     * RVCC_SUCCESSFUL
     *
     * enum used is to check call state rvcc successful
     * value RVCC_SUCCESSFUL
     */
    RVCC_SUCCESSFUL("RVCC_SUCCESSFUL"),
    /**
     * SRVCC_FAILED
     *
     * enum used is to check call state srvcc failed
     * value SRVCC_FAILED
     */
    SRVCC_FAILED("SRVCC_FAILED"),
    /**
     * ASRVCC_STARTED
     *
     * enum used is to check call state asrvcc started
     * value ASRVCC_STARTED
     */
    ASRVCC_STARTED("ASRVCC_STARTED"),
    /**
     * ASRVCC_SUCCESSFUL
     *
     * enum used is to check call state asrvcc suffessful or not
     * value ASRVCC_SUCCESSFUL
     */
    ASRVCC_SUCCESSFUL("ASRVCC_SUCCESSFUL"),
    /**
     * ASRVCC_FAILED
     *
     * enum used is to check call state asrvcc failed or not
     * value ASRVCC_FAILED
     */
    ASRVCC_FAILED("ASRVCC_FAILED"),
    /**
     * EPDG_HO_STARTED
     *
     * enum is used to check call state epdg ho started or not
     * value EPDG_HO_STARTED
     */
    EPDG_HO_STARTED("EPDG_HO_STARTED"),
    /**
     * EPDG_HO_SUCCESSFUL
     *
     * enum used is call state epdg ho successful or not
     * value EPDG_HO_SUCCESSFUL
     */
    EPDG_HO_SUCCESSFUL("EPDG_HO_SUCCESSFUL"),
    /**
     * EPDG_HO_FAILED
     *
     * enum used is call state epdg ho failed or not
     * value EPDG_HO_FAILED
     */
    EPDG_HO_FAILED("EPDG_HO_FAILED")
}
