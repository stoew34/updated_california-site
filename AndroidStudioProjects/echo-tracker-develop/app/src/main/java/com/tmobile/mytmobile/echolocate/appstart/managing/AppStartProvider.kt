package com.tmobile.mytmobile.echolocate.appstart.managing

import android.content.Context
import com.tmobile.mytmobile.echolocate.appstart.IAppStart


/**
 *  class AppStartProvider
 *      -singleton class provides for single instance of AppStartManager
 */
class AppStartProvider private constructor(val context: Context) : IAppStart {

    private var appStartManager: AppStartManager? = null

    companion object {
        @Volatile
        private var INSTANCE: IAppStart? = null

        /***
         * access to singleton AppStartProvider object
         *
         */
        fun getInstance(context: Context): IAppStart {
            return INSTANCE
                ?: synchronized(this) {
                    val instance: IAppStart = AppStartProvider(context)
                    INSTANCE = instance
                    instance
                }
        }
    }

    override fun initializeAppStart(): IAppStart {

        if (appStartManager == null) {
            appStartManager = AppStartManager.getInstance(context)
        }

        appStartManager?.initializeManager()

        return this
    }

}