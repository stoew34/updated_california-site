package com.tmobile.mytmobile.echolocate.coverage.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.coverage.database.CoverageDatabaseConstants

/**
 * Class to declare all the variables of CellIdentityEntity
 * These are columns stored in the room data base
 */

@Entity(
    tableName = CoverageDatabaseConstants.COVERAGE_CELL_IDENTITY_TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = BaseEchoLocateCoverageEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class CoverageCellIdentityEntity(
    /**
     * Cell ID is an integer value that identifies a specific cell tower. This is also called CGI and a concatenation of the eNodeB ID and the Cell ID.
     */
    val cellId: String?,

    /**
     * difference between Cell info Time and event time in seconds
     */
    val cellInfoDelay: String?,

    /**

     */
    val networkName: String?,

    /**
     * Mobile country code
     */
    val mcc: String?,

    /**
     * Mobile network code
     */
    val mnc: String?,

    /**
     * Radio frequency channel name for LTE. Range falls between 0-65535
     */
    val earfcn: String?,

    /**
     * Tracking area code used for LTE and NR
     */
    val tac: String?,

    /**
     * area code used for GSM.
     */
    val lac: String?

) : BaseChildCoverageEntity("", "", "")