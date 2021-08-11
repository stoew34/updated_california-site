package com.tmobile.mytmobile.echolocate.autoupdate

import com.tmobile.echolocate.autoupdate.UpdateEvent

/**
 * Listener to pass the [UpdateEvent]
 */
interface OnUpdateChangedListener {
    /**
     * @param updateEvent [UpdateEvent]
     */
    fun onUpdateChanged(updateEvent: UpdateEvent)
}
