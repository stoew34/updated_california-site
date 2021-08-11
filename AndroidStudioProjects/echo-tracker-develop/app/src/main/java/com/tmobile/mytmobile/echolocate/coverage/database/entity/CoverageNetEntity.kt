package com.tmobile.mytmobile.echolocate.coverage.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.coverage.database.CoverageDatabaseConstants

/**
 * Class to declare all the variables of Net entity
 * These are columns stored in the room data base
 */
@Entity(
    tableName = CoverageDatabaseConstants.COVERAGE_NET_TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = BaseEchoLocateCoverageEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class CoverageNetEntity(
    /**
    Reports the type of network.
     */
    val connectivityType: String?,
    /**
    Indicates whether the device is currently roaming on this network. network info from which roaming state is determined. Ex:false
     */
    val roamingData: String?

) : BaseParentCoverageEntity("", "")