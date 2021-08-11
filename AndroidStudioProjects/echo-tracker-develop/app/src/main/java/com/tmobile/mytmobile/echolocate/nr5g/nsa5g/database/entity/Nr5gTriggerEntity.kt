package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.Nr5gDatabaseConstants

/**
 * class that declares all variables of Nr5gTriggerEntity
 * These are columns stored in the room data base for Nr5gTriggerEntity
 */
@Entity(
    tableName = Nr5gDatabaseConstants.NR5G_TRIGGER_TABLE_NAME, foreignKeys = [ForeignKey(
        entity = BaseEchoLocateNr5gEntity::class,
        parentColumns = arrayOf("sessionId"),
        childColumns = arrayOf("sessionId"),
        onDelete = ForeignKey.CASCADE
    )]
)

data class Nr5gTriggerEntity(

    /**
     * Returns timestamp in UNIX epoch time as a UTC string, "yyyy-MM-dd'T'HH:mm:ss.SSSZ" e.g.
     * 2019-06-24T18:57:23.567+0000
     * Notes:  This timestamp can be rendered as an integer string which will correspond to the above UTC
     * dates.
     */
    val timestamp: String,

    /**
     * Returns the ID of the nr5gTrigger that was fired.
    hourly nr5gTrigger - 100
    App nr5gTrigger - 200
    Screen nr5gTrigger - 300
    Notes: Each nr5gTrigger should have a unique id which should be reported as part of the report.
     */
    val triggerId: Int,

    /**
     * Use the ApplicationPackageName in the following OEM intent requirement description.
     * Returns the ID of the nr5gTrigger that was fired.
     */
    val triggerApp: String,

    /**
     * Collect and store the Echo locate data matrix.
     *
     * @param daoContainer       Dao container
     * @param triggerId          Trigger Id for which data is being collected
     * @param triggerName        Trigger name for which data is being collected
     * @param actualTriggerDelay in case of app, the initial app start is a trigger.
     *                           Then, will need a few more data collections with the set interval:
     *                           10 seconds after app start, 20 seconds after app start, 30 seconds after app start.
     *                           Actual trigger delay will happen after some delay
     *                           Actual trigger delay is the delay in when launch time trigger and  data is being collected time.
     *                           For example 10 sec trigger happen at 10:40 and data collected at 10:55 then return delta (10:55 - 10:40)
     */
    /**
     * Returns the delta between launch Time and  DataCollectTime of trigger.
     *
     * @return Actual trigger delay in case of app, the initial app start is a trigger.
     * Then, will need a few more data collections with the set interval:
     * 10 seconds after app start, 20 seconds after app start, 30 seconds after app start.
     * Actual trigger delay will happen after some delay.
     * Actual trigger delay is the delta of launch time trigger and data is being collected time.
     * For example 10 sec trigger happen at 10:40 and data collected at 10:55 then return delta (10:55 - 10:40)
     * System.currentTimeMillis() - timestamp;
     */
    val triggerDelay: Int
) : BaseEntity("", "")