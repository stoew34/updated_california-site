package com.tmobile.mytmobile.echolocate.lte.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.lte.database.LteDatabaseConstants

/**
 * Class that declare all the variables of third carrier entity.
 * These are columns stored in the voice room data base
 */
@Entity(
    tableName = LteDatabaseConstants.LTE_THIRD_CARRIER_TABLE_NAME, foreignKeys = [ForeignKey(
        entity = BaseEchoLocateLteEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)

data class ThirdCarrierEntity(

    /**
     * RSRP value of SCell2 in 2CA or 3CA in dbm. Report -150 if RSRP is not available even in
     * LTE. Report -999 if it's not LTE.
     */
    val rsrp: String?,

    /**
     * RSRQ value of SCell2 in 2CA or 3CA in dbm. Report -50 if RSRQ is not available even in
     * LTE. Report -999 if it's not LTE.
     */
    val rsrq: String?,

    /**
     * RSSI value of SCell2 in 2CA or 3CA in dbm. Report -150 if SINR is not available even in LTE.
     */
    val rssi: String?,

    /**
     * SINR value of SCell2 in 2CA or 3CA in db. Report -50 if SINR is not available even in LTE.
     */
    val sinr: String?
) : BaseLteEntity("", "")