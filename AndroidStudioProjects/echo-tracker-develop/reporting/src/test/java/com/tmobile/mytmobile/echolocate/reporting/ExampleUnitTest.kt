package com.tmobile.mytmobile.echolocate.reporting

import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.EchoLocateApplication
import com.tmobile.mytmobile.echolocate.analytics.AnalyticsReportProvider
import com.tmobile.mytmobile.echolocate.analytics.utils.AnalyticsSharedPreference
import com.tmobile.mytmobile.echolocate.appstart.AppStartSharedPreference
import com.tmobile.mytmobile.echolocate.appstart.managing.AppStartProvider
import com.tmobile.mytmobile.echolocate.authentication.utils.TokenSharedPreference
import com.tmobile.mytmobile.echolocate.autoupdate.AutoUpdatePreference
import com.tmobile.mytmobile.echolocate.configmanager.ConfigProvider
import com.tmobile.mytmobile.echolocate.configuration.ConfigKey
import com.tmobile.mytmobile.echolocate.configuration.model.Report
import com.tmobile.mytmobile.echolocate.configuration.model.Voice
import com.tmobile.mytmobile.echolocate.reporting.fake.FakeApplicationContext.FakeApplicationContext
import com.tmobile.mytmobile.echolocate.reporting.manager.ReportManager
import com.tmobile.mytmobile.echolocate.reporting.utils.ReportingModuleSharedPrefs
import com.tmobile.mytmobile.echolocate.reportingmanager.ReportProvider
import com.tmobile.mytmobile.echolocate.schedulermanager.SchedulerComponent
import com.tmobile.mytmobile.echolocate.stetho.StethoInitializer
import com.tmobile.mytmobile.echolocate.voice.VoiceReportProvider
import com.tmobile.mytmobile.echolocate.voice.model.VoiceReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    val context = EchoLocateApplication()
    val manager: ReportManager? = ReportManager(context)


    @Before
    fun configsetup() {

        GlobalScope.launch(Dispatchers.IO) {
            context.initAppStartModule()
        }
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        ConfigProvider.getInstance(appContext).initConfigPreferences(appContext)
        ConfigProvider.getInstance(appContext).initConfigModule(appContext)
        ReportingModuleSharedPrefs.init(appContext, BuildConfig.FLAVOR)
        ReportProvider.getInstance(appContext).initReportingModule()

        // Add the Rx java global error handler.

//        context.initAppStartModule()
//        context.getSharedPreferences(
//            ReportingModuleSharedPrefs.REPORT_COMPILER_PREF_FILE_NAME,
//            ReportingModuleSharedPrefs.MODE
//        )
//
//        GlobalScope.launch(Dispatchers.IO) {
//
//            var configProvider = ConfigProvider.getInstance(context)
//            configProvider.initConfigPreferences(context)

        }
// Problem:
        //in order to initalize report manager we need to intialize the preferences which is a lateinit variable
        //WHy isn't it getting initalized?




    @Test
    fun InitializeManagerTest() {

        val actual = manager?.isManagerInitialized()
        assertEquals(true, actual)
    }


    @Test
    fun AddandremoveReportType() {
        manager!!.addReportTypes(VoiceReportProvider.getInstance(context))
        manager.removeReportTypes(VoiceReportProvider.getInstance(context))
        val actual =  manager!!.getReportTypes().size
        assertEquals(0,  actual)
    }

    @Test
    fun AddReportType() {
        manager!!.addReportTypes(VoiceReportProvider.getInstance(context))
        val actual = manager!!.getReportTypes().size
        assertEquals(1,  actual)
    }

    @Test
    fun CheckReadySend() {
        manager!!.addReportTypes(VoiceReportProvider.getInstance(context))
        val actual = manager.checkIfReadyToSend()
        assertEquals(false, actual)
    }

}