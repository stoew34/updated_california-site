package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model

/**
 * Model class that holds call Nr5gMmwCellLog data
 *
 * Serialized name annotation for all variables to be in JSON,
 * and it will be helpful while doing progaurding
 */
data class Nr5gMmwCellLog(
    /**
     *  Returns timestamp in UNIX epoch time as a UTC string, "yyyy-MM-dd'T'HH:mm:ss.SSSZ" e.g.
     *  2019-06-24T18:57:23.567+0000

     *  Notes:  This timestamp can be rendered as an integer string which will correspond to the above UTC
     *  dates.
     */
    val timestamp: String,

    /**
     * Returns the enumerated type of network as an integer which may have the following values:

     *  [-2] – NOT_AVAILABLE
     *  [0] – SEARCHING
     *  [1] – LTE orLTE with EN-DC
     *  [2] – UMTS
     *  [3] – EDGE
     *  [4] – GPRS

     *  Notes: Returns -2 if no data is available
     */
    val networkType: Int,

    /**
     * Returns the PCI (physical cell ID) of the NR PSCell when NR RRC is in CONNECTED state.
     *  [-999] if connected network is not LTE
     *  [-2] value not available even in LTE

     *  Notes: Returns -999 if ‘networkType’ is not 1 (LTE or LTE EN-DC)
     *  Returns -2 if the value is not available even if ‘networkType’ is 1
     */
    val nrPscellPci: Int,

    /**
     * Returns the beam index of the connected SSB beam (wide beam for control data transmission) from the
     * NR PSCell.

     *  [-999] if connected network is not LTE
     *  [-2] value not available even in LTE

     *  Notes: Returns -999 if ‘networkType’ is not 1 (LTE or LTE EN-DC)
     *  Returns -2 if the value is not available even if ‘networkType’ is 1
     */
    val ssbBeamIndex: Int,

    /**
     * Returns the NR PSCell BRSRP (Beam RSRP) for the connected SSB beam (wide beam for control data
     * transmission) from the NR PSCell in dBm.

     *  [-999] if connected network is not LTE
     *  [-150] value not available even in LTE
     */
    val ssbBrsrp: Float,

    /**
     * Returns the NR PSCell BRSRQ (Beam RSRQ) for the connected SSB beam (wide beam for control data
     * transmission) from the NR PSCell in dB.

     *  [-999] if connected network is not LTE
     *  [-50] value not available even in LTE
     */
    val ssbBrsrq: Float,

    /**
     * Returns the NR PSCell SNR for the connected SSB beam (wide beam for control data transmission) from
     * the NR PSCell in dB.

     *  [-999] if connected network is not LTE
     *  [-50] value not available even in LTE
     */
    val ssbSnr: Float,

    /**
     * Returns the beam index of the PDSCH beam (narrow beam for user data transmission) from the NR PSCell.

     *  [-999] if connected network is not LTE
     *  [-2] value not available even in LTE

     *  Notes: Returns -999 if ‘networkType’ is not 1 (LTE or LTE EN-DC)
     *  Returns -2 if the value is not available even if ‘networkType’ is 1
     */
    val pdschBeamIndex: Int,

    /**
     * Returns the NR PSCell BRSRP (Beam RSRP) for the PDSCH beam (narrow beam for user data transmission)
     * from the NR PSCell in dBm.

     *  [-999] if connected network is not LTE
     *  [-150] value not available even in LTE
     */
    val pdschBrsrp: Float,

    /**
     * Returns the NR PSCell BRSRQ (Beam RSRQ) for the PDSCH beam (narrow beam for user data transmission)
     * from the NR PSCell in dB.

     *  [-999] if connected network is not LTE
     *  [-50] value not available even in LTE
     */
    val pdschBrsrq: Float,

    /**
     *   Returns the NR PSCell SNR for the PDSCH beam (narrow beam for user data transmission)
     *   from the NR PSCell in dB.

     *   [-999] if connected network is not LTE
     *  [-50] value not available even in LTE

     */
    val pdschSnr: Float,

    /**
     * Returns the band name of the NR frequency on which the UE is connected to the 5G NR PSCell.

     *  [-999] if connected network is not LTE
     *  [-2] value not available even in LTE

     *  Notes: Returns -999 if ‘networkType’ is not 1 (LTE or LTE EN-DC)
     *  Returns -2 if the value is not available even if ‘networkType’ is 1
     */
    val nrBandName: String,

    /**
     * Returns the NR bandwidth in MHz on which the UE is connected to the 5G NR PSCell.

     *  [-999] if connected network is not LTE
     *  [-2] value not available even in LTE

     *  Notes: For instance, "n261" for NR band n261
     *  Returns -999 if ‘networkType’ is not 1 (LTE or LTE EN-DC)
     *  Returns -2 if the value is not available even if ‘networkType’ is 1
     */
    val nrBandwidth: Int,

    /**
     * Returns the number of all the SSB beams from the 5G NR PSCell detected on the UE.

     *  [-999] if connected network is not LTE
     *  [-2] value not available even in LTE

     *  Notes: Returns 0 if UE detected no SSB beams from the 5G NR PSCell
     *  Returns -999 if ‘networkType’ is not 1 (LTE or LTE EN-DC)
     *  Returns -2 if the value is not available even if ‘networkType’ is 1
     *  Returns -2 if UE failed to verify how many SSB beams were available from the 5G NR PSCell
     *  (0 indicates 'No possible beams’ and -2 indicates 'Possible beams but unknown number’)
     */
    val numberOfSsBBeams: Int?

)