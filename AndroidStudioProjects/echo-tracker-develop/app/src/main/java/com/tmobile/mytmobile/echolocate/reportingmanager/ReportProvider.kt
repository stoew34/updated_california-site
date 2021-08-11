package com.tmobile.mytmobile.echolocate.reportingmanager

import android.content.Context
import com.tmobile.myaccount.events.diagnostics.pojos.collector.event.ClientSideEvent
import com.tmobile.mytmobile.echolocate.BuildConfig
import com.tmobile.mytmobile.echolocate.reporting.IBaseReportDataRequestor
import com.tmobile.mytmobile.echolocate.reporting.IReportProvider
import com.tmobile.mytmobile.echolocate.reporting.manager.ReportManager
import com.tmobile.mytmobile.echolocate.reporting.utils.ReportingModuleSharedPrefs
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import java.util.concurrent.LinkedBlockingQueue

/**
 *  class ReportProvider
 *      -singleton class provides for single instance of ReportManager
 */
class ReportProvider private constructor(val context: Context) : IReportProvider {

    private var reportManager: ReportManager? = null
    private var queue: LinkedBlockingQueue<String> = LinkedBlockingQueue()

    companion object {
        @Volatile
        private var INSTANCE: IReportProvider? = null

        /** Access to singleton ReportProvider object */
        fun getInstance(context: Context): IReportProvider {
            return INSTANCE
                ?: synchronized(this) {
                    val instance: IReportProvider =
                        ReportProvider(
                            context
                        )
                    INSTANCE = instance
                    instance
                }
        }
    }

    /**
     * Public API to initReportingModule module. This API is responsible for preparing the module
     * to start accepting the incoming data, process and store it.
     * If this API is not called, the reporting module will not start the data collection.
     */
    override fun initReportingModule(): IReportProvider {

        if (reportManager == null) {
            reportManager = ReportManager.getInstance(context)
        }

        if (reportManager != null && !reportManager!!.isManagerInitialized()) {
            reportManager?.initReportManager()
        }
        return this
    }

    /** Report types register */
    override fun registerReportTypes(reportType: IBaseReportDataRequestor) {
        if (reportManager == null) {
            // Ideally initialization should have been before,
            // but if it is not, it should be tracked why it is not for debug builds.
            check(!BuildConfig.DEBUG) { "Please initReportingModule reporting module before registration" }
            initReportingModule()
        }
        reportManager?.addReportTypes(reportType)

    }

    /** Report types unregister */
    override fun unRegisterReportTypes(reportType: IBaseReportDataRequestor) {
        reportManager?.removeReportTypes(reportType)
    }

    /**
     * Public API to stop Reporting module.
     * If this function is called, the Reporting module will stop working.
     *
     * Call initReportingModule [initReportingModule] to start Reporting module again.
     */
    override fun stopReportingModule() {
        reportManager?.stopReportDataCollection()
        reportManager = null
    }

    /** This fun used for instant Request for Reports*/
    override fun instantRequestReportsFromAllModules(androidWorkId: String?) {
        reportManager?.requestReportsFromModules(androidWorkId)
    }

    /** This fun used for instant Send Reports from data collection modules*/
    override fun performReportSending(reportEventList: List<ClientSideEvent>) {
        reportManager?.receiveReportsFromModules(reportEventList)
    }

    /**
     * This function is used to check if reporting module is enabled or not
     */
    override fun isReportingModuleEnabled(): Boolean {
        return (reportManager != null && reportManager!!.isManagerInitialized())
    }

    override fun idDropBox(fileID: Collection<String>): Boolean {
        val recieved: Boolean

        EchoLocateLog.eLogD("DS:flow file taken :$fileID")
        queue.addAll(fileID)

        recieved = true
        return recieved
    }


    internal class Builder internal constructor(val context: Context) {

        private var reportProvider: IReportProvider

        init {
            reportProvider = getInstance(context)
            ReportingModuleSharedPrefs.init(context, BuildConfig.FLAVOR)
        }

        internal fun build(): IReportProvider {
            return reportProvider
        }

        /**
         * @param token : Token from asdk library
         */
        fun setToken(token: String?): Builder {
            ReportingModuleSharedPrefs.tokenObject = token
            return this
        }

        /**
         *  @param clientAppReportingUrl : Server Url to send reports to
         */
        fun setClientAppEnvironment(clientAppEnvironment: String): Builder {
            ReportingModuleSharedPrefs.clientAppEnvironment = clientAppEnvironment
            return this
        }

        /**
         * @param clientAppEnvironment : Environment name of client app
         */
        fun setClientAppReportUrl(clientAppReportingUrl: String): Builder {
            ReportingModuleSharedPrefs.clientAppReportingUrl = clientAppReportingUrl
            return this
        }

        /**
         * @param clientAppBuildTypeDebug : Build type of client app
         */
        fun setClientAppBuildTypeDebug(clientAppBuildTypeDebug: Boolean): Builder {
            ReportingModuleSharedPrefs.clientAppBuildTypeDebug = clientAppBuildTypeDebug
            return this
        }

        /**
         * @param clientAppVersionName : Version name of client app
         */
        fun setClientAppVersionName(clientAppVersionName: String): Builder {
            ReportingModuleSharedPrefs.clientAppVersionName = clientAppVersionName
            return this
        }

        /**
         * @param clientAppVersionCode : Version code of client app
         */
        fun setClientAppVersionCode(clientAppVersionCode: String): Builder {
            ReportingModuleSharedPrefs.clientAppVersionCode = clientAppVersionCode
            return this
        }

        /**
         * @param clientAppApplicationId : Application Id of client app
         */
        fun setClientAppApplicationId(clientAppApplicationId: String): Builder {
            ReportingModuleSharedPrefs.clientAppApplicationId = clientAppApplicationId
            return this
        }

        /**
         *  @param clientAppSaveToFile : Flag to determine saving logs in debug mode depending on client app flavor
         */
        fun setClientAppSaveToFile(clientAppSaveToFile: Boolean): Builder {
            ReportingModuleSharedPrefs.clientAppSaveToFile = clientAppSaveToFile
            return this
        }
    }


}