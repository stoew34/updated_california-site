package com.tmobile.mytmobile.echolocate.nr5g.sa5g.model

/**
 * Model class that holds Sa5gTrigger data to form a JSON
 *
 * Serialized name annotation for all variables to be in JSON,
 * and it will be helpful while doing progaurding
 */
data class Sa5gTrigger(

    /**
     * Returns timestamp in UNIX epoch time as a UTC string, "yyyy-MM-dd'T'HH:mm:ss.SSSZ" e.g.
     * 2019-06-24T18:57:23.567+0000
     * Notes:  This timestamp can be rendered as an integer string which will correspond to the above UTC
     * dates.
     */
    val timestamp: String?,

    /**
     * Returns the ID of the Sa5gTrigger that was fired.
    hourly Sa5gTrigger - 100
    App Sa5gTrigger - 200
    Screen Sa5gTrigger - 300
    Notes: Each Sa5gTrigger should have a unique id which should be reported as part of the report.
     */
    val triggerId: Int?,

    /**
     * Use the ApplicationPackageName in the following OEM intent requirement description.
     * Returns the ID of the Sa5gTrigger that was fired.
     */
    val triggerApp: String?,

    /**
     *  Returns the ID of the Sa5gTrigger that was fired.
     *  Notes: Each Sa5gTrigger should have a unique id which should be reported as part of the report.
     */
    val triggerDelay: Int?
)