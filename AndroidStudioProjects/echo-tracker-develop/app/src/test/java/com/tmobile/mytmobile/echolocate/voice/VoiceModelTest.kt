package com.tmobile.mytmobile.echolocate.voice

import com.tmobile.mytmobile.echolocate.standarddatablocks.DeviceInfo
import com.tmobile.mytmobile.echolocate.standarddatablocks.OEMSV
import com.tmobile.mytmobile.echolocate.voice.repository.database.entity.*
import com.tmobile.mytmobile.echolocate.voice.model.*
import org.junit.Test

class VoiceModelTest {
    @Test
    fun testAppTriggeredCallData() {
        val voiceLocation1 = LocationData(10.0, 0.0f, 10.0, 10.0, 0.0f, 1)
        val cellInfo1 = CellInfo(
            "ecio",
            "rscp",
            "rsrp",
            "rsrq",
            "rssi",
            "sinr",
            "snr",
            "lac",
            "networkBand",
            "1",
            "networkType"
        )
        val eventInfo1 = EventInfo(cellInfo1, voiceLocation1)
        val appData1 = AppTriggeredCallData(
            "appname",
            "appPackageId",
            "1",
            "appVersionName",
            "oemTimestamp",
            "eventTimestamp",
            eventInfo1
        )
        val appData2 = appData1.copy()
        assert(appData1 == appData2)
        assert(appData1.hashCode() == appData2.hashCode())
        assert(appData1.toString() == appData2.toString())
    }

    @Test
    fun testAppTriggeredCallDataEntity() {
        val appDataEntity1 = AppTriggeredCallDataEntity(
            "appname",
            "appPackageId",
            "1",
            "appVersionName",
            "oemTimestamp",
            "eventTimestamp"
        )
        val appDataEntity2 = appDataEntity1.copy()
        assert(appDataEntity1 == appDataEntity2)
        assert(appDataEntity1.hashCode() == appDataEntity2.hashCode())
        assert(appDataEntity1.toString() == appDataEntity2.toString())
    }

    @Test
    fun testBaseVoiceData() {
        val basevoiceData1 = BaseVoiceData("callID", "uniqueID")
        val baseVoiceData2 = BaseVoiceData(basevoiceData1.callId, basevoiceData1.uniqueId)
        baseVoiceData2.callId = "callId2"
        baseVoiceData2.uniqueId = "uniqueID2"
        assert(basevoiceData1 != baseVoiceData2)

    }

    @Test
    fun testBaseEchoLocateEntity() {
        val baseEchoLocateVoiceEntity1 =
            BaseEchoLocateVoiceEntity(
                "sessionId",
                "RAW",
                "callNumber",
                "clientVersion",
                5L,
                5L,
                1,
                NetworkIdentity("", "")
            )
        val baseEchoLocateVoiceEntity2 = baseEchoLocateVoiceEntity1.copy()
        assert(baseEchoLocateVoiceEntity1 == baseEchoLocateVoiceEntity2)
        assert(baseEchoLocateVoiceEntity1.hashCode() == baseEchoLocateVoiceEntity2.hashCode())
//        assert(!voiceDao.getBaseEchoLocateVoiceEntity("RAW").isNullOrEmpty())

    }

    @Test
    fun testCallSessions() {
        val carrierConfigData = CarrierConfigData(
            carrierVoiceConfig = "ON_VONR",
            carrierVoWiFiConfig = "ENABLED_5GSA",
            bandConfig = listOf(BandConfig("SAn71Enabled", "true")),
            carrierConfigVersion = "1",
            eventTimestamp = "1608332419"
        )
        val networkIdentity1 = NetworkIdentity("mcc", "mnc")
        val oemSoftwareVersion1 =
            OEMSV(
                "1",
                "customVersion",
                "radioVersion",
                "buildName",
                "androidVersion"
            )
        val deviceIntents1 =
            DeviceIntents(listOf(), listOf(), listOf(), listOf(), listOf(), listOf(), listOf(), listOf(), carrierConfigData)
        val callSessions1 =
            CallSessions(networkIdentity1, "", "", "", oemSoftwareVersion1, deviceIntents1)
        val callSessions2 = callSessions1.copy()
        assert(callSessions1 == callSessions2)
        assert(callSessions1.hashCode() == callSessions2.hashCode())
        assert(callSessions1.toString() == callSessions2.toString())
        assert(callSessions1.networkIdentity == callSessions2.networkIdentity)
        assert(callSessions1.callId == callSessions2.callId)
        assert(callSessions1.callNumber == callSessions2.callNumber)
        assert(callSessions1.clientVersion == callSessions2.clientVersion)
        assert(callSessions1.OEMSV == callSessions2.OEMSV)
        assert(callSessions1.deviceIntents == callSessions2.deviceIntents)
    }

    @Test
    fun testCallSettingData() {
        val voiceLocation1 = LocationData(10.0, 0.0f, 10.0, 10.0, 0.0f, 1)
        val cellInfo1 = CellInfo(
            "ecio",
            "rscp",
            "rsrp",
            "rsrq",
            "rssi",
            "sinr",
            "snr",
            "lac",
            "networkBand",
            "1",
            "networkType"
        )
        val eventInfo1 = EventInfo(cellInfo1, voiceLocation1)
        val callSettingData1 = CallSettingData(
            "voLteStatus",
            "wfcStatus",
            "wfcPreference",
            "oemTimestamp",
            "eventTimestamp",
            eventInfo1
        )
        val callSettingData2 = callSettingData1.copy()
        assert(callSettingData1 == callSettingData2)
        assert(callSettingData1.hashCode() == callSettingData2.hashCode())
        assert(callSettingData1.toString() == callSettingData2.toString())
    }

    @Test
    fun testCallSettingDataEntity() {
        val callSettingDataEntity1 = CallSettingDataEntity(
            "voLteStatus",
            "wfcStatus",
            "wfcPreference",
            "oemTimestamp",
            "eventTimestamp"
        )
        val callSettingDataEntity2 = callSettingDataEntity1.copy()
        assert(callSettingDataEntity1 == callSettingDataEntity2)
        assert(callSettingDataEntity1.hashCode() == callSettingDataEntity2.hashCode())
        assert(callSettingDataEntity1.toString() == callSettingDataEntity2.toString())
    }

    @Test
    fun testCarrierConfigData() {
        val carrierConfigData = CarrierConfigData(
            carrierVoiceConfig = "ON_VONR",
            carrierVoWiFiConfig = "ENABLED_5GSA",
            bandConfig = listOf(BandConfig("SAn71Enabled", "true")),
//            standaloneBands5gValues = arrayOf("true", "false", "-1", "-2"),
            carrierConfigVersion = "1",
            eventTimestamp = "1608332419"
        )
        val carrierConfigData1 = carrierConfigData.copy()
        assert(carrierConfigData == carrierConfigData1)
        assert(carrierConfigData.hashCode() == carrierConfigData1.hashCode())
        assert(carrierConfigData.toString() == carrierConfigData1.toString())
    }

    @Test
    fun testCarrierConfigEntity() {
        val carrierConfigDataEntity = CarrierConfigDataEntity(
            carrierVoiceConfig = "ON_VONR",
            carrierVoWiFiConfig = "ENABLED_5GSA",
            standaloneBands5gKeys = "[\"SAn71Enabled\",\"SAn66Enabled\",\"NONE\",\"ERROR\"]",
            standaloneBands5gValues = "[\"true\", \"false\", \"-1\", \"-2\"]",
            carrierConfigVersion = "1",
            eventTimestamp = "1608332419"
        )
        val carrierConfigDataEntity1 = carrierConfigDataEntity.copy()
        assert(carrierConfigDataEntity == carrierConfigDataEntity1)
        assert(carrierConfigDataEntity.hashCode() == carrierConfigDataEntity1.hashCode())
        assert(carrierConfigDataEntity.toString() == carrierConfigDataEntity1.toString())

    }

    @Test
    fun testEmergencyCallTimerStateData() {
        val voiceLocation1 = LocationData(10.0, 0.0f, 10.0, 10.0, 0.0f, 1)
        val cellInfo1 = CellInfo(
            "ecio",
            "rscp",
            "rsrp",
            "rsrq",
            "rssi",
            "sinr",
            "snr",
            "lac",
            "networkBand",
            "1",
            "networkType"
        )
        val eventInfo1 = EventInfo(cellInfo1, voiceLocation1)
        val emergencyCallTimerStateData = EmergencyCallTimerStateData(
            timerName = "stub",
            timerState = "Stub",
            eventTimestamp = "",
            oemTimestamp = "",
            eventInfo = eventInfo1
        )
        val emergencyCallTimerStateData1 = emergencyCallTimerStateData.copy()
        assert(emergencyCallTimerStateData == emergencyCallTimerStateData1)
        assert(emergencyCallTimerStateData.hashCode() == emergencyCallTimerStateData.hashCode())
        assert(emergencyCallTimerStateData.toString() == emergencyCallTimerStateData1.toString())
    }

    @Test
    fun testEmergencyCallTimerStateDataEntity() {
        val emergencyCallTimerStateEntity = EmergencyCallTimerStateEntity(
            timerName = "stub",
            timerState = "Stub",
            eventTimestamp = "",
            oemTimestamp = ""
        )
        val emergencyCallTimerStateEntity1 = emergencyCallTimerStateEntity.copy()
        assert(emergencyCallTimerStateEntity == emergencyCallTimerStateEntity1)
        assert(emergencyCallTimerStateEntity.hashCode() == emergencyCallTimerStateEntity1.hashCode())
        assert(emergencyCallTimerStateEntity.toString() == emergencyCallTimerStateEntity1.toString())
    }

    @Test
    fun testCellInfo() {
        val cellInfo1 = CellInfo(
            "ecio",
            "rscp",
            "rsrp",
            "rsrq",
            "rssi",
            "sinr",
            "snr",
            "lac",
            "networkBand",
            "1",
            "networkType"
        )
        val cellInfo2 = cellInfo1.copy()
        assert(cellInfo1 == cellInfo2)
        assert(cellInfo1.hashCode() == cellInfo2.hashCode())
        assert(cellInfo1.toString() == cellInfo2.toString())
    }

    @Test
    fun testCellInfoEntity() {
        val cellInfoEntity1 = CellInfoEntity(
            "ecio",
            "rscp",
            "rsrp",
            "rsrq",
            "rssi",
            "sinr",
            "snr",
            "lac",
            "networkBand",
            "1",
            "networkType"
        )
        val cellInfoEntity2 = cellInfoEntity1.copy()
        assert(cellInfoEntity1 == cellInfoEntity2)
        assert(cellInfoEntity1.hashCode() == cellInfoEntity2.hashCode())
        assert(cellInfoEntity1.toString() == cellInfoEntity2.toString())
    }

    @Test
    fun testDetailedCallStateData() {
        val voiceLocation1 = LocationData(10.0, 0.0f, 10.0, 10.0, 0.0f, 1)
        val cellInfo1 = CellInfo(
            "ecio",
            "rscp",
            "rsrp",
            "rsrq",
            "rssi",
            "sinr",
            "snr",
            "lac",
            "networkBand",
            "1",
            "networkType"
        )
        val eventInfo1 = EventInfo(cellInfo1, voiceLocation1)
        val detailedCallStateData1 = DetailedCallStateData(
            "callCode",
            "callState",
            "oemTimestamp",
            "eventTimestamp",
            eventInfo1
        )
        val detailedCallStateData2 = detailedCallStateData1.copy()
        assert(detailedCallStateData1 == detailedCallStateData2)
        assert(detailedCallStateData1.hashCode() == detailedCallStateData2.hashCode())
        assert(detailedCallStateData1.toString() == detailedCallStateData2.toString())
    }

    @Test
    fun testDetailedCallStateEntity() {
        val detailedCallState1 = DetailedCallStateEntity(
            "callCode",
            "callState",
            "oemTimestamp",
            "eventTimestamp"
        )
        val detailedCallStateEntity2 = detailedCallState1.copy()
        assert(detailedCallState1 == detailedCallStateEntity2)
        assert(detailedCallState1.hashCode() == detailedCallStateEntity2.hashCode())
        assert(detailedCallState1.toString() == detailedCallStateEntity2.toString())
    }

    @Test
    fun testDeviceInfo() {
        val deviceInfo1 = DeviceInfo("uuid1", "imei", "imsi", "msisdn", "testSessionId")
        val deviceInfo2 = DeviceInfo(
            deviceInfo1.uuid,
            deviceInfo1.imei,
            deviceInfo1.imsi,
            deviceInfo1.msisdn,
            deviceInfo1.testSessionID
        )
        val deviceInfo3 = deviceInfo2.copy(deviceInfo2.uuid)

        assert(deviceInfo1 == deviceInfo2)
        assert(deviceInfo1.hashCode() == deviceInfo2.hashCode())
        assert(deviceInfo1.toString() == deviceInfo2.toString())
        assert(deviceInfo2 == deviceInfo3)

        assert(deviceInfo1.uuid == deviceInfo2.uuid)
        assert(deviceInfo1.imei == deviceInfo2.imei)
        assert(deviceInfo1.imsi == deviceInfo2.imsi)
        assert(deviceInfo1.msisdn == deviceInfo2.msisdn)
        assert(deviceInfo1.testSessionID == deviceInfo2.testSessionID)
    }

    @Test
    fun testDeviceInfoEntity() {
        val deviceInfo1 = DeviceInfoEntity(
            "uuid1",
            "imei",
            "imsi",
            "msisdn",
            "testSessionId"
        )
        val deviceInfo2 = DeviceInfoEntity(
            deviceInfo1.uuid,
            deviceInfo1.imei,
            deviceInfo1.imsi,
            deviceInfo1.msisdn,
            deviceInfo1.testSessionID
        )
        val deviceInfo3 = deviceInfo2.copy(deviceInfo2.uuid)
        assert(deviceInfo1 == deviceInfo2)
        assert(deviceInfo1.hashCode() == deviceInfo2.hashCode())
        assert(deviceInfo1.toString() == deviceInfo2.toString())
        assert(deviceInfo2 == deviceInfo3)
    }

    @Test
    fun testDeviceIntents() {
        val carrierConfigData = CarrierConfigData(
            carrierVoiceConfig = "ON_VONR",
            carrierVoWiFiConfig = "ENABLED_5GSA",
            bandConfig = listOf(BandConfig("SAn71Enabled", "true")),
//            standaloneBands5gValues = arrayOf("true", "false", "-1", "-2"),
            carrierConfigVersion = "1",
            eventTimestamp = "1608332419"
        )
        val deviceIntents1 =
            DeviceIntents(listOf(), listOf(), listOf(), listOf(), listOf(), listOf(), listOf(), listOf(), carrierConfigData)
        val deviceIntents2 = deviceIntents1.copy()
        val deviceIntents3 = DeviceIntents(deviceIntents1.appTriggeredCallData,deviceIntents1.callSettingData,deviceIntents1.detailedCallStateData,
            deviceIntents1.imsSignallingData,deviceIntents1.rtpdlStateData,deviceIntents1.uiCallStateData,deviceIntents1.radioHandoverData,
        deviceIntents1.emergencyCallTimerState, deviceIntents1.carrierConfig)
        assert(deviceIntents1 == deviceIntents2)
        assert(deviceIntents1.hashCode() == deviceIntents2.hashCode())
        assert(deviceIntents1 == deviceIntents3)
    }

    @Test
    fun testEventInfo() {
        val voiceLocation1 = LocationData(10.0, 0.0f, 10.0, 10.0, 0.0f, 1)
        val cellInfo1 = CellInfo("", "", "", "", "", "", "", "", "", "1", "")
        val eventInfo1 = EventInfo(cellInfo1, voiceLocation1)
        val eventInfo2 = eventInfo1.copy()
        assert(eventInfo1 == eventInfo2)
        assert(eventInfo1.hashCode() == eventInfo2.hashCode())

    }

//    @Test
//    fun testEventInfoEntity() {
//        val voiceLocation1 = LocationData(10.0, 0.0f, 10.0, 10.0, 0.0f, 1)
//        val cellInfo1 = CellInfo("", "", "", "", "", "", "", "", "", "1", "")
//        val eventInfoEntity1 = EventInfoEntity(listOf(cellInfo1), listOf(voiceLocation1))
//        val eventInfoEntity2 = EventInfoEntity(eventInfoEntity1.cellInfo, eventInfoEntity1.location)
//
//        assert(eventInfoEntity1.equals(eventInfoEntity2))
//        assert(eventInfoEntity1.hashCode() == eventInfoEntity2.hashCode())
//    }

    @Test
    fun testImsSignallingData() {
        val voiceLocation1 = LocationData(10.0, 0.0f, 10.0, 10.0, 0.0f, 1)
        val cellInfo1 = CellInfo(
            "ecio",
            "rscp",
            "rsrp",
            "rsrq",
            "rssi",
            "sinr",
            "snr",
            "lac",
            "networkBand",
            "1",
            "networkType"
        )
        val eventInfo1 = EventInfo(cellInfo1, voiceLocation1)
        val imsSignallingData1 = ImsSignallingData(
            "sipCallId",
            "sipCseq",
            "sipLine1",
            "sipOrigin",
            "sipReason",
            "sipSDP",
            "oemTimestamp",
            "eventTimestamp",
            eventInfo1
        )
        val imsSignallingData2 = imsSignallingData1.copy()
        assert(imsSignallingData1 == imsSignallingData2)
        assert(imsSignallingData1.hashCode() == imsSignallingData2.hashCode())
        assert(imsSignallingData1.toString() == imsSignallingData2.toString())
    }

    @Test
    fun testImsSignallingEntity() {
        val imsSignallingEntity1 = ImsSignallingEntity(
            "sipCallId",
            "sipCseq",
            "sipLine1",
            "sipOrigin",
            "sipReason",
            "sipSDP",
            "oemTimestamp",
            "eventTimestamp"
        )
        val imsSignallingEntity2 = imsSignallingEntity1.copy()
        assert(imsSignallingEntity1 == imsSignallingEntity2)
        assert(imsSignallingEntity1.hashCode() == imsSignallingEntity2.hashCode())
        assert(imsSignallingEntity1.toString() == imsSignallingEntity2.toString())
    }

    @Test
    fun testNetworkIdentity() {
        val networkIdentity1 = NetworkIdentity("mcc", "mnc")
        val networkIdentity2 = networkIdentity1.copy()
        assert(networkIdentity1 == networkIdentity2)
        assert(networkIdentity1.hashCode() == networkIdentity2.hashCode())

    }

    @Test
    fun testNetworkIdentityEntity() {
        val networkIdentityEntity1 = NetworkIdentityEntity("mcc", "mnc")
        val networkIdentityEntity2 = networkIdentityEntity1.copy()
        val networkIdentityEntity3 =
            NetworkIdentityEntity(networkIdentityEntity1.mcc, networkIdentityEntity1.mnc)
        assert(networkIdentityEntity1 == networkIdentityEntity2)
        assert(networkIdentityEntity1.hashCode() == networkIdentityEntity1.hashCode())
        assert(networkIdentityEntity1 == networkIdentityEntity3)
        assert(networkIdentityEntity1.toString() == networkIdentityEntity3.toString())
    }

    @Test
    fun testOemSoftwareVersion() {
        val oemSoftwareVersion1 =
            OEMSV(
                "1",
                "customVarsion",
                "radioVersion",
                "buildName",
                "androidVersion"
            )
        val oemSoftwareVersion2 = oemSoftwareVersion1.copy()
        val oemSoftwareVersion3 =
            OEMSV(
                oemSoftwareVersion1.softwareVersion,
                oemSoftwareVersion1.customVersion,
                oemSoftwareVersion1.radioVersion,
                oemSoftwareVersion1.buildName,
                oemSoftwareVersion1.androidVersion
            )
        oemSoftwareVersion3.softwareVersion = oemSoftwareVersion2.softwareVersion
        oemSoftwareVersion3.customVersion = oemSoftwareVersion2.customVersion
        oemSoftwareVersion3.radioVersion = oemSoftwareVersion2.radioVersion
        oemSoftwareVersion3.buildName = oemSoftwareVersion2.buildName
        oemSoftwareVersion3.androidVersion = oemSoftwareVersion2.androidVersion
        assert(oemSoftwareVersion1 == oemSoftwareVersion2)
        assert(oemSoftwareVersion1.hashCode() == oemSoftwareVersion2.hashCode())
        assert(oemSoftwareVersion3 == oemSoftwareVersion2)
    }

    @Test
    fun testOemSoftwareVersionEntity() {
        val oemSoftwareVersionEntity1 =
            OEMSoftwareVersionEntity(
                "1",
                "customVarsion",
                "radioVersion",
                "buildName",
                "androidVersion"
            )
        val oemSoftwareVersionEntity2 = oemSoftwareVersionEntity1.copy()
//        voiceDao.insertOEMSoftwareVersionEntity(oemSoftwareVersionEntity1)
        assert(oemSoftwareVersionEntity1 == oemSoftwareVersionEntity2)
        assert(oemSoftwareVersionEntity1.hashCode() == oemSoftwareVersionEntity2.hashCode())
        assert(oemSoftwareVersionEntity1.toString() == oemSoftwareVersionEntity2.toString())
//        val oemSoftwareVersionEntity3 = voiceDao.getOEMSVEntity(oemSoftwareVersionEntity1.callId)
//        assert(oemSoftwareVersionEntity3 == oemSoftwareVersionEntity1)
    }

    @Test
    fun testRadioHandoverData() {
        val voiceLocation1 = LocationData(10.0, 0.0f, 10.0, 10.0, 0.0f, 1)
        val cellInfo1 = CellInfo(
            "ecio",
            "rscp",
            "rsrp",
            "rsrq",
            "rssi",
            "sinr",
            "snr",
            "lac",
            "networkBand",
            "1",
            "networkType"
        )
        val eventInfo1 = EventInfo(cellInfo1, voiceLocation1)
        val radioHandoverData1 = RadioHandoverData(
            "sipSDP",
            "oemTimestamp",
            "eventTimestamp",
            eventInfo1
        )
        val radioHandoverData2 = radioHandoverData1.copy()
        assert(radioHandoverData1 == radioHandoverData2)
        assert(radioHandoverData1.hashCode() == radioHandoverData2.hashCode())
        assert(radioHandoverData1.toString() == radioHandoverData2.toString())
    }

    @Test
    fun testRadioHandoverEntity() {
        val radioHandoverEntity1 = RadioHandoverEntity(
            "sipSDP",
            "oemTimestamp",
            "eventTimestamp"
        )
        val radioHandoverEntity2 = radioHandoverEntity1.copy()
        assert(radioHandoverEntity1 == radioHandoverEntity2)
        assert(radioHandoverEntity1.hashCode() == radioHandoverEntity2.hashCode())
        assert(radioHandoverEntity1.toString() == radioHandoverEntity2.toString())
    }

    @Test
    fun testRtpdlStateData() {
        val voiceLocation1 = LocationData(10.0, 0.0f, 10.0, 10.0, 0.0f, 1)
        val cellInfo1 = CellInfo(
            "ecio",
            "rscp",
            "rsrp",
            "rsrq",
            "rssi",
            "sinr",
            "snr",
            "lac",
            "networkBand",
            "1",
            "networkType"
        )
        val eventInfo1 = EventInfo(cellInfo1, voiceLocation1)
        val rtpdlStateData1 = RtpdlStateData(
            5.0,
            5.0,
            5.0,
            5.0,
            5.0,
            "oemTimestamp",
            "eventTimestamp",
            eventInfo1
        )
        val rtpdlStateData2 = rtpdlStateData1.copy()
        assert(rtpdlStateData1 == rtpdlStateData2)
        assert(rtpdlStateData1.hashCode() == rtpdlStateData2.hashCode())
        assert(rtpdlStateData1.toString() == rtpdlStateData2.toString())
    }

    @Test
    fun testRtpdlStateEntity() {
        val rtpdlStateEntity1 = RtpdlStateEntity(
            5.0,
            5.0,
            5.0,
            5.0,
            5.0,
            "oemTimestamp",
            "eventTimestamp"
        )
        val rtpdlStateEntity2 = rtpdlStateEntity1.copy()
        assert(rtpdlStateEntity1 == rtpdlStateEntity2)
        assert(rtpdlStateEntity1.hashCode() == rtpdlStateEntity2.hashCode())
        assert(rtpdlStateEntity1.toString() == rtpdlStateEntity2.toString())
    }

    @Test
    fun testUiCallStateData() {
        val voiceLocation1 = LocationData(10.0, 0.0f, 10.0, 10.0, 0.0f, 1)
        val cellInfo1 = CellInfo(
            "ecio",
            "rscp",
            "rsrp",
            "rsrq",
            "rssi",
            "sinr",
            "snr",
            "lac",
            "networkBand",
            "1",
            "networkType"
        )
        val eventInfo1 = EventInfo(cellInfo1, voiceLocation1)
        val uiCallStateData1 = UiCallStateData(
            "uiCallState",
            "oemTimestamp",
            "eventTimestamp",
            eventInfo1
        )
        val uiCallStateData2 = uiCallStateData1.copy()
        assert(uiCallStateData1 == uiCallStateData2)
        assert(uiCallStateData1.hashCode() == uiCallStateData2.hashCode())
        assert(uiCallStateData1.toString() == uiCallStateData2.toString())
    }

    @Test
    fun testUiCallStateEntity() {
        val uiCallStateEnity1 = UiCallStateEntity(
            "uiCallState",
            "oemTimestamp",
            "eventTimestamp"
        )
        val uiCallStateEntity2 = uiCallStateEnity1.copy()
        assert(uiCallStateEnity1 == uiCallStateEntity2)
        assert(uiCallStateEnity1.hashCode() == uiCallStateEntity2.hashCode())
        assert(uiCallStateEnity1.toString() == uiCallStateEntity2.toString())
    }

    @Test
    fun testVoiceData() {
        val voiceLocation1 = LocationData(10.0, 0.0f, 10.0, 10.0, 0.0f, 1)
        val deviceInfo1 = DeviceInfo("uuid1", "imei", "mssidn", "mssidn", "testSessionId")
        val voiceData1 =
            VoiceSingleSessionReport(1, "schemaVersion", "RAW", listOf(), voiceLocation1, deviceInfo1)
        val voiceData2 = voiceData1.copy()
        assert(voiceData1 == voiceData2)
        assert(voiceData1.hashCode() == voiceData2.hashCode())
        assert(voiceData1.toString() == voiceData2.toString())
        assert(voiceData1.numDiscardedIntents == voiceData2.numDiscardedIntents)
        assert(voiceData1.schemaVersion == voiceData2.schemaVersion)
        assert(voiceData1.status == voiceData2.status)
        assert(voiceData1.callSessions == voiceData2.callSessions)
        assert(voiceData1.location == voiceData2.location)
        assert(voiceData1.deviceInfo == voiceData2.deviceInfo)

    }

    @Test
    fun testVoiceLocation() {
        val voiceLocation1 = LocationData(10.0, 0.0f, 10.0, 10.0, 0.0f, 1)
        val voiceLocation3 = LocationData()
        val voiceLocation2 = voiceLocation1.copy()
        assert(voiceLocation1 == voiceLocation2)
        assert(voiceLocation1 != voiceLocation3)
        assert(voiceLocation1.hashCode() == voiceLocation2.hashCode())
        assert(voiceLocation1.toString() == voiceLocation2.toString())

    }

    @Test
    fun testVoiceLocationEntity() {
        val voiceLocationEntity1 = VoiceLocationEntity(10.0, 0.0f, 10.0, 10.0, 0.0f, 1)
        val voiceLocationEntity2 = voiceLocationEntity1.copy()
        assert(voiceLocationEntity1 == voiceLocationEntity2)
        assert(voiceLocationEntity1.hashCode() == voiceLocationEntity2.hashCode())
        assert(voiceLocationEntity1.toString() == voiceLocationEntity2.toString())

    }

    @Test
    fun testVoiceReportEntity() {
        val voiceReportEntity1 =
            VoiceReportEntity("voiceReportId", "json", 1, 5L, 5L, "eventTimestamp", "")
        val voiceReportEntity2 = voiceReportEntity1.copy()
        val voiceReportEntity3 =
            VoiceReportEntity(voiceReportEntity1.voiceReportId, voiceReportEntity1.json, voiceReportEntity1
                .numDiscardedIntents, voiceReportEntity1.startTime, voiceReportEntity1.endTime,voiceReportEntity1.eventTimestamp, "")
        voiceReportEntity3.voiceReportId = voiceReportEntity2.voiceReportId
        assert(voiceReportEntity1 == voiceReportEntity2)
        assert(voiceReportEntity1.hashCode() == voiceReportEntity2.hashCode())
        assert(voiceReportEntity1.toString() == voiceReportEntity2.toString())
        assert(voiceReportEntity3.voiceReportId == voiceReportEntity2.voiceReportId)
    }
}