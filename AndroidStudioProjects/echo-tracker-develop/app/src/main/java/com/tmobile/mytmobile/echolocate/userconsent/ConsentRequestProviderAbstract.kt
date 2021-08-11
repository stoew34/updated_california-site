package com.tmobile.mytmobile.echolocate.userconsent

import com.tmobile.mytmobile.echolocate.userconsent.userconsentevents.UserConsentResponseEvent
import io.reactivex.Observable

/**
 *  abstract class for exposing APIs
 */

abstract class ConsentRequestProviderAbstract {

    /**
     * API for getting user consent boolean flags
     *
     *      this api accesses ContentManager.kt to check for current flag status.
     *      The ContentManager.kt then accesses stored cache to return current flag
     *      status, if cached flag values are empty, ContentManager.kt then accesses TMO app
     *      for current flag status, updates cache, then returns current flag status to calling
     *      method
     *
     * @return UserConsentResponseEvent
     */

    abstract fun getUserConsentFlags(): UserConsentResponseEvent?

    /**
     * API for getting consent updates
     *
     *      api for receiving broadcast status change/updates from TMO app.
     *
     * @return Observable<UserConsentResponseEvent>
     */
    abstract fun getUserConsentUpdates(): Observable<UserConsentResponseEvent>

    abstract fun stopConsentModule()
}