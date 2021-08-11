package com.tmobile.mytmobile.echolocate.coverage.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.coverage.database.CoverageDatabaseConstants

/**
 * Class declares all the variables of Coverage Oesm Entity
 * These are columns stored in the coverage database
 */
@Entity(
    tableName = CoverageDatabaseConstants.COVERAGE_OEMSV_TABLE_NAME, foreignKeys = [
        ForeignKey(
            entity = BaseEchoLocateCoverageEntity::class,
            parentColumns = arrayOf("sessionId"),
            childColumns = arrayOf("sessionId"),
            onDelete = ForeignKey.CASCADE
        )]
)
data class CoverageOEMSVEntity(

    /**
     * android os version
     */
    val androidVersion: String?,

    /**
     * Custom version is the version is the OEM specific custom version if available
     */
    val buildName: String?,

    /**
     * Android SDK Version E.g., "1.0" or "3.4b5".
     */
    val customVersion: String?,

    /**
     * Build name is either a changelist number, or a label like "M4-rc20".
     */
    val radioVersion: String?,

    /**
     * two digit device software version
     */
    val sv: String?

) : BaseParentCoverageEntity("", "")