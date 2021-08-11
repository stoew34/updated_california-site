package com.tmobile.mytmobile.echolocate.coverage.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.coverage.database.CoverageDatabaseConstants

/**
 * Class to declare all the variables of CellSignalStrengthEntity
 * These are columns stored in the room data base
 */

@Entity(
    tableName = CoverageDatabaseConstants.COVERAGE_CELL_SIGNAL_STRENGTH_TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = BaseEchoLocateCoverageEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class CoverageCellSignalStrengthEntity(
    /**
     * Arbitrary Strength Unit is an integer value proportional to the received signal strength measured by the mobile phone.
     * Depending on the type of the connected network, this can be used to calculate RSRP for 5G, LTE, RSCP for 3G and RSSI for 2G.
     */
    val asu: String?,

    /**
     * dBm measurement received by the device. This can be CSI-RSRP for 5G and RSRP for LTE and RSCP for 3G and RSSI for 2G.
     */
    val dBm: String?,

    val bandwidth: String?,

    /**
     * LTE RSRP
     */
    val rsrp: String?,

    /**
     * LTE RSRQ
     */
    val rsrq: String?,

    /**
     * LTE Reference Signal SNR Usually available on Samsung Exynos chipset devices only
     */
    val rssnr: String?,

    /**
     * LTE CQI Usually available on Samsung Exynos chipset devices only
     */
    val cqi: String?,

    /**
     * An integer value used in a method to sync the device with the cell.
     */
    val timingAdvance: String?

) : BaseChildCoverageEntity("", "", "")