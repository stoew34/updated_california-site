package com.tmobile.mytmobile.echolocate.coverage.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.coverage.database.CoverageDatabaseConstants

/**
 * Class to declare all the variables of NrCellEntity
 * These are columns stored in the room data base
 */
@Entity(
    tableName = CoverageDatabaseConstants.COVERAGE_NR_CELL_TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = BaseEchoLocateCoverageEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class CoverageNrCellEntity(
    /**
     *  RSRP on CSI beam. Reference: 3GPP TS 38.215.
     */
    val nrCsiRsrp: String?,

    /**
     *  RSRQ on CSI beam Reference: 3GPP TS 38.215.
     */
    val nrCsiRsrq: String?,

    /**
     *  SINR on CSI beam 3GPP TS 38.215 Sec 5.1.*, 3GPP TS 38.133 10.1.16.1 Range: -23 dB to 23 dB
     */
    val nrCsiSinr: String?,

    /**
     *  RSRP on SS beam Reference: 3GPP TS 38.215.
     */
    val nrSsRsrp: String?,

    /**
     *  RSRQ on SS beam Reference: 3GPPTS38.215.
     */
    val nrSsRsrq: String?,

    /**
     *  SINR on SS beam
     */
    val nrSsSinr: String?,

    /**
     *  Nrstatus from ServiceState
     */
    val nrStatus: String?,

    val nrDbm: String?,

    val nrLevel: String?,

    val nrAsuLevel: String?,

    /**
     *  New Radio Absolute RadioFrequency ChannelNumber.
     */
    val nrArfcn: String?,

    /**
     *  NR (NewRadio5G) CellIdentity
     */
    val nrCi: String?,

    val nrPci: String?,

    /**
     *  Tracking area code used for LTE and NR
     */
    val nrTac: String?

) : BaseChildCoverageEntity("", "", "")