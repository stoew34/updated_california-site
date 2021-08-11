package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.Nr5gDatabaseConstants

/**
 * class that declares all variables of EndcLteLogEntity
 * These are columns stored in the room data base for EndcLteLogEntity
 */
@Entity(
    tableName = Nr5gDatabaseConstants.NR5G_END_C_LTE_LOG_TABLE_NAME, foreignKeys = [ForeignKey(
        entity = BaseEchoLocateNr5gEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class EndcLteLogEntity(
    /**
     * Returns timestamp in UNIX epoch time as a UTC string, "yyyy-MM-dd'T'HH:mm:ss.SSSZ" e.g.
     * 2019-06-24T18:57:23.567+0000
     * Notes:  This timestamp can be rendered as an integer string which will correspond to the above UTC
     * dates.
     */
    val timestamp: String,

    /**
     *  Returns the enumerated type of network as an integer which may have the following values:
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
     * Returns the cell ID (CID) of the LTE cell that is the anchor for the 5G EN-DC connection.

     *  [-999] if connected network is not LTE
     *  [-2] value not available even in LTE

     *  Notes: Returns -999 if ‘networkType’ is not 1 (LTE or LTE EN-DC)
     *  Returns -2 if the value is not available even if ‘networkType’ is 1
     */
    val anchorLteCid: Long,

    /**
     *  Returns the Physical Cell ID (PCI) of the LTE cell that is the anchor for the 5G EN-DC connection.

     *  [-999] if connected network is not LTE
     *  [-2] value not available even in LTE

     *  Notes: Returns -999 if ‘networkType’ is not 1 (LTE or LTE EN-DC)
     *  Returns -2 if the value is not available even if ‘networkType’ is 1
     */
    val anchorLtePci: Int,

    /**
     *  Returns a value which indicates if the upperLayerIndication-r15 in SystemInformatinoBlockType2 was *
     *  received from the LTE serving cell, indicating that the LTE serving cell is EN-DC capable.

     *  [-999] if connected network is not LTE
     *  [-2] value not available even in LTE
     *  [1] (ON), the UE received the upperLayerIndication-r15 indicating that the LTE serving cell is EN-DC
     *  capable
     *  [2] (OFF), the UE didn’t receive the upperLayerIndication-r15 from the serving cell

     *  Notes: Returns -999 if ‘networkType’ is not 1 (LTE or LTE EN-DC)
     *  Returns -2 if the value is not available even if ‘networkType’ is 1
     */
    val endcCapability: Int,

    /**
     * Returns an integer which indicates the LTE RRC state.

     *  [-999] if connected network is not LTE
     *  [-2] value not available even in LTE
     *  [0] if RRC_IDLE
     *  [1] if RRC_CONNECTED

     *  Notes: Returns -999 if ‘networkType’ is not 1 (LTE or LTE EN-DC)
     *  Returns -2 if the value is not available even if ‘networkType’ is 1
     */
    val lteRrcState: Int
) : BaseEntity("", "")