package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.Nr5gDatabaseConstants

/**
 * class that declares all variables of Nr5gStatusEntity
 * These are columns stored in the room data base for Nr5gStatusEntity
 */
@Entity(
    tableName = Nr5gDatabaseConstants.NR5G_GET_NR_STATUS_TABLE_NAME, foreignKeys = [ForeignKey(
        entity = BaseEchoLocateNr5gEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Nr5gStatusEntity(

    /**
     * Returns an integer which corresponds to the following values:

     * [-1] NR_STATUS_NONE - The device isn't camped on an LTE cell or the LTE cell doesn't support E-UTRA-NR
     * Dual Connectivity(EN-DC)
     * [1] NR_STATUS RESTRICTED - The device is camped on an LTE cell that supports E-UTRA-NR Dual
     * Connectivity(EN-DC) but either the
     * use of dual connectivity with NR(DCNR) is restricted or NR is not supported by the selected PLMN.
     * [2] NR_STATUS_NOT_RESTRICTED - The device is camped on an LTE cell that supports E-UTRA-NR Dual
     * Connectivity(EN-DC) and
     * both the use of dual connectivity with NR(DCNR) is not restricted and NR is supported by the selected
     * PLMN.
     * [3] NR_STATUS_CONNECTED - The device is camped on an LTE cell that supports E-UTRA-NR Dual Connectivit
     * (EN-DC) and
     * also connected to at least one 5G cell as a secondary serving cell.
     */
    val getNrStatus: Int
) : BaseEntity("", "")