package com.tmobile.mytmobile.echolocate.coverage.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.coverage.database.CoverageDatabaseConstants

/**
 * Class to declare all the variables of PrimaryCell Entity
 * These are columns stored in the room data base
 */
@Entity(
    tableName = CoverageDatabaseConstants.COVERAGE_PRIMARY_CELL_TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = BaseEchoLocateCoverageEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class CoveragePrimaryCellEntity(
    /**
     *  The cell technology the cell tower is using to transmit to this device
     */
    val cellType: String?

) : BaseChildCoverageEntity("", "", "")