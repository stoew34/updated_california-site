package com.tmobile.mytmobile.echolocate

import android.app.Application
import android.content.Context
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.security.ProviderInstaller
import com.tmobile.mytmobile.echolocate.analytics.utils.AnalyticsSharedPreference
import com.tmobile.mytmobile.echolocate.appstart.AppStartSharedPreference
import com.tmobile.mytmobile.echolocate.appstart.managing.AppStartProvider
import com.tmobile.mytmobile.echolocate.autoupdate.AutoUpdatePreference
import com.tmobile.mytmobile.echolocate.schedulermanager.SchedulerComponent
import com.tmobile.mytmobile.echolocate.stetho.StethoInitializer
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.authentication.utils.TokenSharedPreference
import com.tmobile.mytmobile.echolocate.configmanager.ConfigProvider
import io.reactivex.plugins.RxJavaPlugins
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch


/**
 * This is the base class within an EchoLocate app that
 * contains all components such as activities and services which instantiates
 * before any other class when the process for your application/package is created.
 */
class EchoLocateApplication : Application() {

    val appContext = this
    @UseExperimental(ObsoleteCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        mInstance = this
        // Add the Rx java global error handler.
        handleRxJavaPluginExceptions()

        StethoInitializer().start(this)

        GlobalScope.launch(Dispatchers.IO) {
            SchedulerComponent.getInstance(appContext).initialize()
            initAppStartModule()
            installPlayServicesIfNeeded()
        }
    }

    companion object {
        lateinit var mInstance: EchoLocateApplication
        fun getContext(): Context? {
            return mInstance.applicationContext
        }
    }

    /** This function is used to initialize the AppStartModule:
     *    this module initiates all other modules.
     *
     * The function will create a new worker thread and use that to initialize the app start module
     */
    fun initAppStartModule() {
        TokenSharedPreference.init(this)
        AnalyticsSharedPreference.init(this)
        AutoUpdatePreference.init(this)
        var configProvider = ConfigProvider.getInstance(this)
        configProvider.initConfigPreferences(this)
        AppStartSharedPreference.init(this)
        val appStartProvider = AppStartProvider.getInstance(this) as AppStartProvider
        appStartProvider.initializeAppStart()
    }

    /**
     * Handle the global RX plugin exception. This could happen for various reasons.
     *
     * Read more at:
     *
     * How to handle this: https://github.com/ReactiveX/RxJava/wiki/What's-different-in-2.0#error-handling
     * Why this happens: https://github.com/ReactiveX/RxJava/wiki/What's-different-in-2.0#error-handling
     */
    private fun handleRxJavaPluginExceptions() {
        RxJavaPlugins.setErrorHandler {
            EchoLocateLog.eLogE("There is an error in the Rx Plugin at the global level. This could be a potential app bug, library bug or simply undeliverable exception")
        }
    }

    /**
     * This functions is used to install the security provider
     */
    private fun installPlayServicesIfNeeded() {
        try {
            ProviderInstaller.installIfNeeded(this)
        } catch (e: GooglePlayServicesRepairableException) {
            /* Indicates that Google Play services is out of date, disabled, etc. */
            EchoLocateLog.eLogE("Diagnostic : Failed to install the play services : ${e.localizedMessage}")
            return
        } catch (e: GooglePlayServicesNotAvailableException) {
            /*
            Indicates a non-recoverable error; the ProviderInstaller is not able
            to install an up-to-date Provider.
            */
            EchoLocateLog.eLogE("Diagnostic : Failed to install the play services : ${e.localizedMessage}")
            return
        }
        /*
        If this is reached,the provider was already up-to-date,
        or was successfully updated.
        */
        EchoLocateLog.eLogD("Diagnostic : Play services is installed/up-to-date")
    }

}
