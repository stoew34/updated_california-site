package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.Nr5gDatabaseConstants

/**
 * class that declares all variables of EndcUpLinkLogEntity
 * These are columns stored in the room data base for EndcUpLinkLogEntity
 */
@Entity(
    tableName = Nr5gDatabaseConstants.NR5G_END_C_UPLINK_LOG_TABLE_NAME, foreignKeys = [ForeignKey(
        entity = BaseEchoLocateNr5gEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class EndcUplinkLogEntity(

    /**
     * Returns timestamp in UNIX epoch time as a UTC string, "yyyy-MM-dd'T'HH:mm:ss.SSSZ" e.g.
     * 2019-06-24T18:57:23.567+0000
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
     *  Returns an integer which corresponds to the type of network where uplink data is delivered (PUSCH
     *  channel is established)

     *  [-999] – if connected network is not LTE
     *  [-2] – value not available even in LTE
     *  [1] – PUSCH is on 5G band
     *  [2] – PUSCH is on LTE band
     *  [3] – PUSCH is on both 5G and LTE band simultaneously

     *  Notes: Returns -999 if ‘networkType’ is not 1 (LTE or LTE EN-DC)
     *  Returns -2 if the value is not available even if ‘networkType’ is 1
     */
    val uplinkNetwork: Int
) : BaseEntity("", "")