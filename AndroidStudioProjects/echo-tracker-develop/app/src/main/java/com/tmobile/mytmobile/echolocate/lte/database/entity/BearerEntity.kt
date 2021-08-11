package com.tmobile.mytmobile.echolocate.lte.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.lte.database.LteDatabaseConstants

/**
 * Class that declare all the variables of Bearer Entity
 * These are columns stored in the room data base
 */
@Entity(
    tableName = LteDatabaseConstants.LTE_BEARER_TABLE_NAME,
    foreignKeys = [ForeignKey(
        entity = BaseEchoLocateLteEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)

data class BearerEntity(

    /**
     * The name of the APN for this data connection
     * E.g. fast.t-mobile.com
     * If no data connection exists, report -1.
     * If failed to check this info. Report -2
     * Equals to ‘-999’ if it’s not LTE
     */
    val apnName: String?,

    /**
     * Type of QCI
     */
    val qci: Int?
) : BaseEntity("", "", "")
