package com.tmobile.mytmobile.echolocate.dsdkHandshake.database.databasemodel

import androidx.room.Entity
import com.tmobile.mytmobile.echolocate.dsdkHandshake.utils.DsdkHandshakeUtils

@Entity(tableName = DsdkHandshakeUtils.DSDK_HANDSHAKE_TABLE_NAME,
        primaryKeys = ["voiceStopDataCollection", "lteStopDataCollection", "nr5gStopDataCollection"]
)
data class DsdkHandshakeParametersModel constructor(
    /**
     * This is the boolean value representing request to stop voice data collection
     */
    var voiceStopDataCollection: Boolean = false,
    /**
     * This is the boolean value representing request to stop lte data collection
     */
    var lteStopDataCollection: Boolean = false,
    /***
     * This is the boolean value representing request to stop nr5g data collection
     */
    var nr5gStopDataCollection: Boolean = false
)