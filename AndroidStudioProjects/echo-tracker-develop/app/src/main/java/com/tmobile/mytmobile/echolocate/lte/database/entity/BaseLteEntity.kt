package com.tmobile.mytmobile.echolocate.lte.database.entity

import androidx.room.PrimaryKey

/**
 * Base class for all the entities
 */
open class BaseLteEntity(

    /**
     * session id of the triggered intent
     */
    var sessionId: String,

    /*
     * its a unique id which will get generated at the time of insertion
     */
    @PrimaryKey(autoGenerate = false)
    var uniqueId: String

)