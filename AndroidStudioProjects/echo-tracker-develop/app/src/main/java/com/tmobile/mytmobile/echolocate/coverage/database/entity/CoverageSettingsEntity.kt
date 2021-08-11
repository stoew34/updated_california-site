package com.tmobile.mytmobile.echolocate.coverage.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.coverage.database.CoverageDatabaseConstants

/**
 * Class to declare all the variables of SettingsEntity
 * These are columns stored in the room data base
 */
@Entity(
    tableName = CoverageDatabaseConstants.COVERAGE_SETTINGS_TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = BaseEchoLocateCoverageEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class CoverageSettingsEntity(
    /**
     *  Voice over LTE status. ENABLED(channelCode = 0),DISABLED(channelCode = 1),UNSUPPORTED(channelCode = -1)
     */
    val volteState: String?,

    /**
     *  Settings.Global.DATA_ROAMING. Example: true
     */
    val dataRoamingEnabled: String?

) : BaseParentCoverageEntity("", "")

