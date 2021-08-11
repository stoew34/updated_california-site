package com.tmobile.mytmobile.echolocate.coverage.database.entity

import androidx.room.PrimaryKey


open class BaseChildCoverageEntity(

    /**
     * session id of the triggered intent
     */
    @PrimaryKey(autoGenerate = false)
    var uniqueId: String,

    var baseEntityId: String,

    var sessionId: String

)