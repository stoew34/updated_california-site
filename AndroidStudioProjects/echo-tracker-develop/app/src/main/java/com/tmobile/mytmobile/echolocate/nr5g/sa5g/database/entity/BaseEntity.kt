package com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity

import androidx.room.PrimaryKey

/**
 * class that declares the base type
 */
open class BaseEntity(
    /**
     * session id of the triggered intent
     */
    var sessionId: String,

    /**
     * its a unique id which will get generated at the time of insertion
     */
    @PrimaryKey(autoGenerate = false)
    var uniqueId: String
)