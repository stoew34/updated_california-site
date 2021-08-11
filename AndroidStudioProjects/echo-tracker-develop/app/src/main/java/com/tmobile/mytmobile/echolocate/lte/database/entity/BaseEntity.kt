package com.tmobile.mytmobile.echolocate.lte.database.entity

import androidx.room.PrimaryKey


open class BaseEntity(

    /**
     * session id of the triggered intent
     */
    @PrimaryKey(autoGenerate = false)
    var baseEntityId: String,

    var sessionId: String,

    var uniqueId: String
)