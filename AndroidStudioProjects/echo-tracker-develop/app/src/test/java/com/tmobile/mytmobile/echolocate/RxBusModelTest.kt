package com.tmobile.mytmobile.echolocate

import com.tmobile.mytmobile.echolocate.configuration.events.configurationevents.*
import com.tmobile.mytmobile.echolocate.configuration.events.reportingevents.DIAReportResponseEvent
import com.tmobile.mytmobile.echolocate.configuration.events.reportingevents.DIAReportResponseParameters
import com.tmobile.mytmobile.echolocate.configuration.events.configurationevents.ConfigurationEvent
import com.tmobile.mytmobile.echolocate.configuration.events.configurationevents.ConfigChangeResponseEvent
import com.tmobile.mytmobile.echolocate.configuration.model.AutoUpdate
import com.tmobile.mytmobile.echolocate.configuration.model.Configuration
import com.tmobile.mytmobile.echolocate.configuration.model.HeartBeat
import com.tmobile.mytmobile.echolocate.configuration.model.Voice
import com.tmobile.mytmobile.echolocate.location.events.LocationResponseEvent
import com.tmobile.mytmobile.echolocate.location.model.LocationResponseParameters
import com.tmobile.mytmobile.echolocate.network.events.NetworkResponseEvent
import com.tmobile.mytmobile.echolocate.network.result.NetworkResponseDetails
import com.tmobile.mytmobile.echolocate.scheduler.WorkParameters
import com.tmobile.mytmobile.echolocate.scheduler.events.SchedulerResponseEvent
import com.tmobile.mytmobile.echolocate.userconsent.userconsentevents.UserConsentFlagsParameters
import com.tmobile.mytmobile.echolocate.userconsent.userconsentevents.UserConsentResponseEvent
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.junit.Test

class RxBusModelTest {
    @Test
    fun testConfigurationEvent() {
        val configurationEvent1 = ConfigurationEvent("configuration")
        val configurationEvent2 = configurationEvent1.copy()
        assert(configurationEvent1.equals(configurationEvent2))
        assert(configurationEvent1.hashCode() == configurationEvent2.hashCode())
        assert(configurationEvent1.toString() == configurationEvent2.toString())
    }

    @Test
    fun testUserConsentFlagsParameter() {
        val userConsentFlagsParameters1 = UserConsentFlagsParameters(true, true, true)
        val userConsentFlagsParameters2 = userConsentFlagsParameters1.copy()
        val userConsentFlagsParameters3 = UserConsentFlagsParameters()
        userConsentFlagsParameters3.isAllowedDeviceDataCollection = userConsentFlagsParameters1.isAllowedDeviceDataCollection
        userConsentFlagsParameters3.isAllowedIssueAssist = userConsentFlagsParameters1.isAllowedIssueAssist
        userConsentFlagsParameters3.isAllowedPersonalizedOffers = userConsentFlagsParameters1.isAllowedPersonalizedOffers
        val json = Json(JsonConfiguration.Stable)
        val jsonString1 =
            json.stringify(UserConsentFlagsParameters.serializer(), userConsentFlagsParameters1)
        val jsonString2 =
            json.stringify(UserConsentFlagsParameters.serializer(), userConsentFlagsParameters2)

        val obj1 = json.parse(UserConsentFlagsParameters.serializer(), jsonString1)

        assert(userConsentFlagsParameters1.equals(userConsentFlagsParameters2))
        assert(userConsentFlagsParameters1.hashCode() == userConsentFlagsParameters2.hashCode())
        assert(userConsentFlagsParameters1.toString() == userConsentFlagsParameters2.toString())
        assert(jsonString1 == jsonString2)
        assert(userConsentFlagsParameters1 == obj1)


    }

    @Test
    fun testUserConsentResponseEvent() {
        val userConsentFlagsParameters1 = UserConsentFlagsParameters(true, true, true)
        val userConsentResponseEvent1 = UserConsentResponseEvent(1, userConsentFlagsParameters1)
        val userConsentResponseEvent2 = userConsentResponseEvent1.copy()
        assert(userConsentResponseEvent1.equals(userConsentResponseEvent2))
        assert(userConsentResponseEvent1.hashCode() == userConsentResponseEvent2.hashCode())
        assert(userConsentResponseEvent1.toString() == userConsentResponseEvent2.toString())
    }

    @Test
    fun testSchedulerResponseEvent() {
        val workParametersBuilder1 = WorkParameters.Builder()
        val schedulerResponseEvent1 = SchedulerResponseEvent(5,"", 1, workParametersBuilder1.build())
        val schedulerResponseEvent2 = schedulerResponseEvent1.copy()
        assert(schedulerResponseEvent1.equals(schedulerResponseEvent2))
        assert(schedulerResponseEvent1.hashCode() == schedulerResponseEvent2.hashCode())
        assert(schedulerResponseEvent1.toString() == schedulerResponseEvent2.toString())
    }

    @Test
    fun testNetworkResponseEvent() {
        val networkResponseDetails1 = NetworkResponseDetails()
        val networkResponseEvent1 = NetworkResponseEvent(networkResponseDetails1)
        val networkResponseEvent2 = networkResponseEvent1.copy()
        assert(networkResponseEvent1.equals(networkResponseEvent2))
        assert(networkResponseEvent1.hashCode() == networkResponseEvent2.hashCode())
        assert(networkResponseEvent1.toString() == networkResponseEvent2.toString())
    }

    @Test
    fun testLocationResponseEvent() {
        val locationResponseParameters1 = LocationResponseParameters()
        val locationResponseEvent1 = LocationResponseEvent(locationResponseParameters1)
        val locationResponseEvent2 = locationResponseEvent1.copy()
        assert(locationResponseEvent1.equals(locationResponseEvent2))
        assert(locationResponseEvent1.hashCode() == locationResponseEvent2.hashCode())
        assert(locationResponseEvent1.toString() == locationResponseEvent2.toString())
    }

    @Test
    fun testAutoUpdateConfigEvent() {
        val autoUpdate1 = AutoUpdate(
            true,
            "fingerPrintHash",
            "sourceUrl",
            "appVersion",
            listOf(),
            "connectionType"
        )
        val autoUpdateConfigEvent1 = AutoUpdateConfigEvent(autoUpdate1)
        val autoUpdateConfigEvent2 = autoUpdateConfigEvent1.copy()
        assert(autoUpdateConfigEvent1.equals(autoUpdateConfigEvent2))
        assert(autoUpdateConfigEvent1.hashCode() == autoUpdateConfigEvent2.hashCode())
        assert(autoUpdateConfigEvent1.toString() == autoUpdateConfigEvent2.toString())
    }

    @Test
    fun testConfigChangeResponseEvent() {
        val configChangeResponseEvent1 =
            ConfigChangeResponseEvent(
                "configKey"
            )
        configChangeResponseEvent1.timeStamp=10L
        configChangeResponseEvent1.sourceComponent="source component"
        val configChangeResponseEvent2 =
            ConfigChangeResponseEvent(
                configChangeResponseEvent1.configKey
            )
        configChangeResponseEvent2.timeStamp=configChangeResponseEvent1.timeStamp
        configChangeResponseEvent2.sourceComponent=configChangeResponseEvent1.sourceComponent
//        assert(configChangeResponseEvent1 == configChangeResponseEvent2)
        assert(configChangeResponseEvent1.configKey == configChangeResponseEvent2.configKey)
        assert(configChangeResponseEvent1.timeStamp == configChangeResponseEvent2.timeStamp)
        assert(configChangeResponseEvent1.sourceComponent == configChangeResponseEvent2.sourceComponent)
    }

    @Test
    fun testConfigVersionEvent() {
        val configuration1 = Configuration(1, 1)
        val configVersionEvent1 = ConfigVersionEvent(configuration1)
        val configVersionEvent2 = configVersionEvent1.copy()
        assert(configVersionEvent1.equals(configVersionEvent2))
        assert(configVersionEvent1.hashCode() == configVersionEvent2.hashCode())
        assert(configVersionEvent1.toString() == configVersionEvent2.toString())
    }

    @Test
    fun testHeartbeatConfigEvent() {
        val heartBeat1 = HeartBeat(true, 1, 1)
        val heartbeatConfigEvent1 = HeartbeatConfigEvent(heartBeat1)
        val heartbeatConfigEvent2 = heartbeatConfigEvent1.copy()
        assert(heartbeatConfigEvent1.equals(heartbeatConfigEvent2))
        assert(heartbeatConfigEvent1.hashCode() == heartbeatConfigEvent2.hashCode())
        assert(heartbeatConfigEvent1.toString() == heartbeatConfigEvent2.toString())
    }

    @Test
    fun testVoiceConfigEvent(){
        val voice1 = Voice(true, 1, "", listOf(),10)
        val voiceConfigEvent1 = VoiceConfigEvent(voice1)
        val voiceConfigEvent2 = voiceConfigEvent1.copy()
        assert(voiceConfigEvent1.equals(voiceConfigEvent2))
        assert(voiceConfigEvent1.hashCode() == voiceConfigEvent2.hashCode())
        assert(voiceConfigEvent1.toString() == voiceConfigEvent2.toString())
    }

    @Test
    fun testVoiceReportResponseParameters() {
        val voiceReportResponseParameters1 = DIAReportResponseParameters("requestReportStatus","payload","reportId","reportType")
        val voiceReportResponseParameters2 = voiceReportResponseParameters1.copy()
        val voiceReportResponseParameters3 = DIAReportResponseParameters()
        voiceReportResponseParameters3.requestReportStatus = voiceReportResponseParameters1.requestReportStatus
        voiceReportResponseParameters3.payload = voiceReportResponseParameters1.payload
        voiceReportResponseParameters3.ReportId = voiceReportResponseParameters1.ReportId
        voiceReportResponseParameters3.reportType = voiceReportResponseParameters1.reportType
        val json = Json(JsonConfiguration.Stable)
        val jsonString1 =
            json.stringify(DIAReportResponseParameters.serializer(), voiceReportResponseParameters1)
        val jsonString2 =
            json.stringify(DIAReportResponseParameters.serializer(), voiceReportResponseParameters2)

        val obj1 = json.parse(DIAReportResponseParameters.serializer(), jsonString1)

        assert(voiceReportResponseParameters1.equals(voiceReportResponseParameters2))
        assert(voiceReportResponseParameters1.hashCode() == voiceReportResponseParameters2.hashCode())
        assert(voiceReportResponseParameters1.toString() == voiceReportResponseParameters2.toString())
        assert(jsonString1 == jsonString2)
        assert(voiceReportResponseParameters1 == obj1)
    }

    @Test
    fun testVoiceReportResponseEvent(){
        val voiceReportResponseParameters1 = DIAReportResponseParameters("requestReportStatus","payload","reportId","reportType")
        val voiceReportResponseEvent1 = DIAReportResponseEvent(voiceReportResponseParameters1)
        val voiceReportResponseEvent2 = voiceReportResponseEvent1.copy()
        assert(voiceReportResponseEvent1.equals(voiceReportResponseEvent2))
        assert(voiceReportResponseEvent1.hashCode() == voiceReportResponseEvent2.hashCode())
        assert(voiceReportResponseEvent1.toString() == voiceReportResponseEvent2.toString())
    }
}