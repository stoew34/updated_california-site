package com.tmobile.mytmobile.echolocate.lte

import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.lte.database.EchoLocateLteDatabase
import com.tmobile.mytmobile.echolocate.lte.database.dao.LteDao
import com.tmobile.mytmobile.echolocate.lte.database.entity.*
import com.tmobile.mytmobile.echolocate.lte.model.*
import com.tmobile.mytmobile.echolocate.voice.model.LocationData
import org.junit.After
import org.junit.Before
import org.junit.Test

class LteModuleTest {

    private lateinit var lteDao: LteDao
    private lateinit var db: EchoLocateLteDatabase
    lateinit var context: Context

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, EchoLocateLteDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        lteDao = EchoLocateLteDatabase.getEchoLocateLteDatabase(context).lteDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testBaseEchoLocateLteEntity() {
        val baseEchoLocateLteEntity1 =
            BaseEchoLocateLteEntity(
                1,
                "timeStamp",
                "",
                "1",
                "1",
                "sessionId"
            )
        val baseEchoLocateLteEntity2 = baseEchoLocateLteEntity1.copy()
        assert(baseEchoLocateLteEntity1 == baseEchoLocateLteEntity2)
        assert(baseEchoLocateLteEntity1.hashCode() == baseEchoLocateLteEntity2.hashCode())
        assert(baseEchoLocateLteEntity1.toString() == baseEchoLocateLteEntity2.toString())
    }

    @Test
    fun testBaseLteEntity() {
        val baseLteEntity1 = BaseLteEntity("sessionID", "uniqueID")
        val baseLteEntity2 = BaseLteEntity(baseLteEntity1.sessionId, baseLteEntity1.uniqueId)
        assert(baseLteEntity1 == baseLteEntity2)
        baseLteEntity2.sessionId = "sessionId2"
        baseLteEntity2.uniqueId = "uniqueID2"
        assert(baseLteEntity1 != baseLteEntity2)
    }

    @Test
    fun testBearerConfigurationEntity() {
        val bearerConfigurationEntity1 = BearerConfigurationEntity(
            "1",
            "numOfBearers",
            "timeStamp"
        )
        val bearerConfigurationEntity2 = bearerConfigurationEntity1.copy()
        val bearerConfigurationEntity3 = BearerConfigurationEntity(bearerConfigurationEntity1.networkType,bearerConfigurationEntity1.numberOfBearers,bearerConfigurationEntity1.oemTimestamp)
        assert(bearerConfigurationEntity1 == bearerConfigurationEntity2)
        assert(bearerConfigurationEntity1 == bearerConfigurationEntity3)
        assert(bearerConfigurationEntity1.hashCode() == bearerConfigurationEntity2.hashCode())
        assert(bearerConfigurationEntity1.toString() == bearerConfigurationEntity2.toString())
    }

    @Test
    fun testBearerEntity() {
        val bearerEntity1 = BearerEntity(
            "apnName",
            1
        )
        val bearerEntity2 = bearerEntity1.copy()
        val bearerEntity3 = BearerEntity(
            bearerEntity1.apnName,
            bearerEntity1.qci
        )
        assert(bearerEntity1 == bearerEntity2)
        assert(bearerEntity1 == bearerEntity3)
        assert(bearerEntity1.hashCode() == bearerEntity2.hashCode())
        assert(bearerEntity1.toString() == bearerEntity2.toString())
    }

    @Test
    fun testCAEntity() {
        val caEntity1 = CAEntity(
            1,
            1,
            2,
            3,
            1,
            "modulation",
            "pci",
            "cellId",
            "locationId"
        )
        val caEntity2 = caEntity1.copy()
        val caEntity3 = CAEntity(
            caEntity1.earfcn,
            caEntity1.bandNumber,
            caEntity1.bandWidth,
            caEntity1.carrierNum,
            caEntity1.layers,
            caEntity1.modulation,
            caEntity1.pci,
            caEntity1.cellId,
            caEntity1.locationId
        )
        assert(caEntity1 == caEntity2)
        assert(caEntity1 == caEntity3)
        assert(caEntity1.hashCode() == caEntity2.hashCode())
        assert(caEntity1.toString() == caEntity2.toString())
    }

    @Test
    fun testCommonRfConfigurationEntity() {
        val commonRFConfigurationEntity1 = CommonRFConfigurationEntity(
            1,
            2,
            "ytContentId",
            "ytLink",
            3,
            4,
            "5",
            "timeStamp",
            6,
            7
        )
        val commonRFConfigurationEntity2 = commonRFConfigurationEntity1.copy()
        val commonRFConfigurationEntity3 = CommonRFConfigurationEntity(
            commonRFConfigurationEntity1.lteULaa,
            commonRFConfigurationEntity1.rrcState,
            commonRFConfigurationEntity1.ytContentId,
            commonRFConfigurationEntity1.ytLink,
            commonRFConfigurationEntity1.antennaConfigurationRx,
            commonRFConfigurationEntity1.antennaConfigurationTx,
            commonRFConfigurationEntity1.networkType,
            commonRFConfigurationEntity1.oemTimestamp,
            commonRFConfigurationEntity1.receiverDiversity,
            commonRFConfigurationEntity1.transmissionMode
        )
        assert(commonRFConfigurationEntity1 == commonRFConfigurationEntity2)
        assert(commonRFConfigurationEntity1 == commonRFConfigurationEntity3)
        assert(commonRFConfigurationEntity1.hashCode() == commonRFConfigurationEntity2.hashCode())
        assert(commonRFConfigurationEntity1.toString() == commonRFConfigurationEntity2.toString())
    }

    @Test
    fun testDownLinkCarrierInfoEntity() {
        val downLinkCarrierInfoEntity1 = DownLinkCarrierInfoEntity(
            "1",
            2,
            "timeStamp"
        )
        val downLinkCarrierInfoEntity2 = downLinkCarrierInfoEntity1.copy()
        val downLinkCarrierInfoEntity3 = DownLinkCarrierInfoEntity(
            downLinkCarrierInfoEntity1.networkType,
            downLinkCarrierInfoEntity1.numberAggregatedChannel,
            downLinkCarrierInfoEntity1.oemTimestamp
        )
        assert(downLinkCarrierInfoEntity1 == downLinkCarrierInfoEntity2)
        assert(downLinkCarrierInfoEntity1 == downLinkCarrierInfoEntity3)
        assert(downLinkCarrierInfoEntity1.hashCode() == downLinkCarrierInfoEntity2.hashCode())
        assert(downLinkCarrierInfoEntity1.toString() == downLinkCarrierInfoEntity2.toString())
    }

    @Test
    fun testDownLinkRfConfigurationEntity() {
        val downlinkRFConfigurationEntity1 = DownlinkRFConfigurationEntity(
            "1",
            "timeStamp"
        )
        val downlinkRFConfigurationEntity2 = downlinkRFConfigurationEntity1.copy()
        val downlinkRFConfigurationEntity3 = DownlinkRFConfigurationEntity(
            downlinkRFConfigurationEntity1.networkType,
            downlinkRFConfigurationEntity1.oemTimestamp
        )
        assert(downlinkRFConfigurationEntity1 == downlinkRFConfigurationEntity2)
        assert(downlinkRFConfigurationEntity1 == downlinkRFConfigurationEntity3)
        assert(downlinkRFConfigurationEntity1.hashCode() == downlinkRFConfigurationEntity2.hashCode())
        assert(downlinkRFConfigurationEntity1.toString() == downlinkRFConfigurationEntity2.toString())
    }

//    @Test
//    fun testLteLocationEntity() {
//        val lteLocationEntity1 = LteLocationEntity(
//            10.0,
//            0.0f,
//            10.0,
//            10.0,
//            0.0f,
//            1
//        )
//        val lteLocationEntity2 = lteLocationEntity1.copy()
//        val lteLocationEntity3 = LteLocationEntity()
//        val lteLocationEntity4 = LteLocationEntity(
//            lteLocationEntity1.altitude,
//            lteLocationEntity1.altitudePrecision,
//            lteLocationEntity1.latitude,
//            lteLocationEntity1.longitude,
//            lteLocationEntity1.altitudePrecision,
//            lteLocationEntity1.locationAge
//        )
//        assert(lteLocationEntity1 == lteLocationEntity2)
//        assert(lteLocationEntity1 == lteLocationEntity4)
//        assert(lteLocationEntity1 != lteLocationEntity3)
//        assert(lteLocationEntity1.hashCode() == lteLocationEntity2.hashCode())
//        assert(lteLocationEntity1.toString() == lteLocationEntity2.toString())
//    }

    @Test
    fun testLteOemsvEntity() {
        val lteOEMSVEntity1 = LteOEMSVEntity(
            "1",
            "androidVersion",
            "buildName",
            "customVersion",
            "radioVersion"
        )
        val lteOEMSVEntity2 = lteOEMSVEntity1.copy()
        val lteOEMSVEntity3 = LteOEMSVEntity(
            lteOEMSVEntity1.softwareVersion,
            lteOEMSVEntity1.androidVersion,
            lteOEMSVEntity1.buildName,
            lteOEMSVEntity1.customVersion,
            lteOEMSVEntity1.radioVersion
        )
        assert(lteOEMSVEntity1 == lteOEMSVEntity2)
        assert(lteOEMSVEntity1 == lteOEMSVEntity3)
        assert(lteOEMSVEntity1.hashCode() == lteOEMSVEntity2.hashCode())
        assert(lteOEMSVEntity1.toString() == lteOEMSVEntity2.toString())
    }

    @Test
    fun testLteSettingsEntity() {
        val lteSettingsEntity1 = LteSettingsEntity(
            1,
            2,
            3,
            4,
            "timeStamp",
            "roamingSettings",
            "rtt",
            "rttTranscript",
            5
        )
        val lteSettingsEntity2 = lteSettingsEntity1.copy()
        val lteSettingsEntity3 = LteSettingsEntity(
            lteSettingsEntity1.wifiCallingSetting,
            lteSettingsEntity1.wifiSetting,
            lteSettingsEntity1.mobileDataSettings,
            lteSettingsEntity1.networkModeSettings,
            lteSettingsEntity1.oemTimestamp,
            lteSettingsEntity1.roamingSetting,
            lteSettingsEntity1.rtt,
            lteSettingsEntity1.rttTranscript,
            lteSettingsEntity1.volteSetting
        )
        assert(lteSettingsEntity1 == lteSettingsEntity2)
        assert(lteSettingsEntity1 == lteSettingsEntity3)
        assert(lteSettingsEntity1.hashCode() == lteSettingsEntity2.hashCode())
        assert(lteSettingsEntity1.toString() == lteSettingsEntity2.toString())
    }

    @Test
    fun testNetworkIdentityEntity() {
        val networkIdentityEntity1 = NetworkIdentityEntity(
            "1",
            "2",
            "3",
            "4",
            "timeStamp",
            "wifiConnectionstatus"
        )
        val networkIdentityEntity2 = networkIdentityEntity1.copy()
        val networkIdentityEntity3 = NetworkIdentityEntity(
            networkIdentityEntity1.mcc,
            networkIdentityEntity1.mnc,
            networkIdentityEntity1.tac,
            networkIdentityEntity1.networkType,
            networkIdentityEntity1.oemTimestamp,
            networkIdentityEntity1.wifiConnectionStatus
        )
        assert(networkIdentityEntity1 == networkIdentityEntity2)
        assert(networkIdentityEntity1 == networkIdentityEntity3)
        assert(networkIdentityEntity1.hashCode() == networkIdentityEntity2.hashCode())
        assert(networkIdentityEntity1.toString() == networkIdentityEntity2.toString())
    }

    @Test
    fun testSecondCarrierEntity() {
        val secondCarrierEntity1 = SecondCarrierEntity(
            "rsrp",
            "rsrq",
            "rssi",
            "sinr"
        )
        val secondCarrierEntity2 = secondCarrierEntity1.copy()
        val secondCarrierEntity3 = SecondCarrierEntity(
            secondCarrierEntity1.rsrp,
            secondCarrierEntity1.rsrq,
            secondCarrierEntity1.rssi,
            secondCarrierEntity1.sinr
        )
        assert(secondCarrierEntity1 == secondCarrierEntity2)
        assert(secondCarrierEntity1 == secondCarrierEntity3)
        assert(secondCarrierEntity1.hashCode() == secondCarrierEntity2.hashCode())
        assert(secondCarrierEntity1.toString() == secondCarrierEntity2.toString())
    }

    @Test
    fun testSignalConditionEntity() {
        val signalConditionEntity1 = SignalConditionEntity(
            1,
            2,
            3,
            4,
            5,
            6,
            "networkType",
            "timeStamp"
        )
        val signalConditionEntity2 = signalConditionEntity1.copy()
        val signalConditionEntity3 = SignalConditionEntity(
            signalConditionEntity1.lteUlHeadroom,
            signalConditionEntity1.rachPower,
            signalConditionEntity1.rsrp,
            signalConditionEntity1.rsrq,
            signalConditionEntity1.rssi,
            signalConditionEntity1.sinr,
            signalConditionEntity1.networkType,
            signalConditionEntity1.oemTimestamp
        )
        assert(signalConditionEntity1 == signalConditionEntity2)
        assert(signalConditionEntity1 == signalConditionEntity3)
        assert(signalConditionEntity1.hashCode() == signalConditionEntity2.hashCode())
        assert(signalConditionEntity1.toString() == signalConditionEntity2.toString())
    }

    @Test
    fun testThirdCarrierEntity() {
        val thirdCarrierEntity1 = ThirdCarrierEntity(
            "rsrp",
            "rsrq",
            "rssi",
            "sinr"
        )
        val thirdCarrierEntity2 = thirdCarrierEntity1.copy()
        val thirdCarrierEntity3 = ThirdCarrierEntity(
            thirdCarrierEntity1.rsrp,
            thirdCarrierEntity1.rsrq,
            thirdCarrierEntity1.rssi,
            thirdCarrierEntity1.sinr
        )
        assert(thirdCarrierEntity1 == thirdCarrierEntity2)
        assert(thirdCarrierEntity1 == thirdCarrierEntity3)
        assert(thirdCarrierEntity1.hashCode() == thirdCarrierEntity2.hashCode())
        assert(thirdCarrierEntity1.toString() == thirdCarrierEntity2.toString())
    }

    @Test
    fun testUpLinkCarrierInfoEntity() {
        val uplinkCarrierInfoEntity1 = UplinkCarrierInfoEntity(
            "1",
            2,
            "timeStamp"

        )
        val uplinkCarrierInfoEntity2 = uplinkCarrierInfoEntity1.copy()
        val uplinkCarrierInfoEntity3 = UplinkCarrierInfoEntity(
            uplinkCarrierInfoEntity1.networkType,
            uplinkCarrierInfoEntity1.numberAggregateChannel,
            uplinkCarrierInfoEntity1.oemTimestamp

        )
        assert(uplinkCarrierInfoEntity1 == uplinkCarrierInfoEntity2)
        assert(uplinkCarrierInfoEntity1 == uplinkCarrierInfoEntity3)
        assert(uplinkCarrierInfoEntity1.hashCode() == uplinkCarrierInfoEntity2.hashCode())
        assert(uplinkCarrierInfoEntity1.toString() == uplinkCarrierInfoEntity2.toString())
    }

    @Test
    fun testUpLinkRfConfigurationEntity() {
        val upLinkRFConfigurationEntity1 = UpLinkRFConfigurationEntity(
            "1",
            "timeStamp"
        )
        val upLinkRFConfigurationEntity2 = upLinkRFConfigurationEntity1.copy()
        val upLinkRFConfigurationEntity3 = UpLinkRFConfigurationEntity(
            upLinkRFConfigurationEntity1.networkType,
            upLinkRFConfigurationEntity1.oemTimestamp
        )
        assert(upLinkRFConfigurationEntity1 == upLinkRFConfigurationEntity2)
        assert(upLinkRFConfigurationEntity1 == upLinkRFConfigurationEntity3)
        assert(upLinkRFConfigurationEntity1.hashCode() == upLinkRFConfigurationEntity2.hashCode())
        assert(upLinkRFConfigurationEntity1.toString() == upLinkRFConfigurationEntity2.toString())
    }

    @Test
    fun testBaseLteData() {
        val baseLteData1 = BaseLteData("sessionID", "uniqueID")
        val baseLteData2 = BaseLteData(baseLteData1.sessionId, baseLteData1.uniqueId)
        assert(baseLteData1 == baseLteData2)
        baseLteData2.sessionId = "sessionId2"
        baseLteData2.uniqueId = "uniqueID2"
        assert(baseLteData1 != baseLteData2)
    }

    @Test
    fun testBearerData() {
        val bearer1 = Bearer(
            "apnName",
            1
        )
        val bearer2 = bearer1.copy()
        val bearer3 = Bearer(
            bearer1.apnName,
            bearer1.qci
        )
        assert(bearer1 == bearer2)
        assert(bearer1 == bearer3)
        assert(bearer1.hashCode() == bearer2.hashCode())
        assert(bearer1.toString() == bearer2.toString())
    }

    @Test
    fun testBearerConfigurationData() {
        val bearer = Bearer(
            "apnName",
            1
        )
        val bearerList = mutableListOf<Bearer>()
        bearerList.add(bearer)
        val bearerConfiguration1 = BearerConfiguration(
            bearerList,
            "1",
            "numOfBearers",
            "timeStamp"
        )
        val bearerConfiguration2 = bearerConfiguration1.copy()
        val bearerConfiguration3 = BearerConfiguration(
            bearerConfiguration1.bearer,
            bearerConfiguration1.networkType,
            bearerConfiguration1.numberOfBearers,
            bearerConfiguration1.oemTimestamp
        )
        assert(bearerConfiguration1 == bearerConfiguration2)
        assert(bearerConfiguration1 == bearerConfiguration3)
        assert(bearerConfiguration1.hashCode() == bearerConfiguration2.hashCode())
        assert(bearerConfiguration1.toString() == bearerConfiguration2.toString())
    }


    @Test
    fun testCaData() {
        val caData1 = CAData(
            1,
            1,
            2,
            3,
            1,
            "modulation",
            "pci",
            "cellId",
            "locationId"
        )
        val caData2 = caData1.copy()
        val caData3 = CAData(
            caData1.earfcn,
            caData1.bandNumber,
            caData1.bandWidth,
            caData1.carrierNum,
            caData1.layers,
            caData1.modulation,
            caData1.pci,
            caData1.cellId,
            caData1.locationId
        )
        assert(caData1 == caData2)
        assert(caData1 == caData3)
        assert(caData1.hashCode() == caData2.hashCode())
        assert(caData1.toString() == caData2.toString())
    }

    @Test
    fun testCommonRfConfigurationData() {
        val commonRfConfig1 = CommonRFConfiguration(
            1,
            0,
            "ytContentId",
            "ytLink",
            2,
            3,
            "4",
            "timeStamp",
            5,
            6
        )
        val commonRfConfig2 = commonRfConfig1.copy()
        val commonRfConfig3 = CommonRFConfiguration(
            commonRfConfig1.lteULaa,
            commonRfConfig1.rrcState,
            commonRfConfig1.ytContentId,
            commonRfConfig1.ytLink,
            commonRfConfig1.antennaConfigurationRx,
            commonRfConfig1.antennaConfigurationTx,
            commonRfConfig1.networkType,
            commonRfConfig1.oemTimestamp,
            commonRfConfig1.receiverDiversity,
            commonRfConfig1.transmissionMode
        )
        assert(commonRfConfig1 == commonRfConfig2)
        assert(commonRfConfig1 == commonRfConfig3)
        assert(commonRfConfig1.hashCode() == commonRfConfig2.hashCode())
        assert(commonRfConfig1.toString() == commonRfConfig2.toString())
    }

    @Test
    fun testDownLinkCarrierInfoData() {
        val caDataList = getCADataList()

        val downlinkCarrierInfo1 = DownlinkCarrierInfo(
            caDataList,
            "1",
            2,
            "timeStamp"
        )
        val downlinkCarrierInfo2 = downlinkCarrierInfo1.copy()
        val downlinkCarrierInfo3 = DownlinkCarrierInfo(
            downlinkCarrierInfo1.ca,
            downlinkCarrierInfo1.networkType,
            downlinkCarrierInfo1.numberAggregatedChannel,
            downlinkCarrierInfo1.oemTimestamp
        )
        assert(downlinkCarrierInfo1 == downlinkCarrierInfo2)
        assert(downlinkCarrierInfo1 == downlinkCarrierInfo3)
        assert(downlinkCarrierInfo1.hashCode() == downlinkCarrierInfo2.hashCode())
        assert(downlinkCarrierInfo1.toString() == downlinkCarrierInfo2.toString())
    }

    @Test
    fun testDownLinkRfConfigurationData() {
        val caDataList = getCADataList()

        val downlinkRFConfiguration1 = DownlinkRFConfiguration(
            caDataList,
            "2",
            "timeStamp"
        )
        val downlinkRFConfiguration2 = downlinkRFConfiguration1.copy()
        val downlinkRFConfiguration3 = DownlinkRFConfiguration(
            downlinkRFConfiguration1.ca,
            downlinkRFConfiguration1.networkType,
            downlinkRFConfiguration1.oemTimestamp
        )
        assert(downlinkRFConfiguration1 == downlinkRFConfiguration2)
        assert(downlinkRFConfiguration1 == downlinkRFConfiguration3)
        assert(downlinkRFConfiguration1.hashCode() == downlinkRFConfiguration2.hashCode())
        assert(downlinkRFConfiguration1.toString() == downlinkRFConfiguration2.toString())
    }

    @Test
    fun testLocationData() {
        val location1 = LocationData(10.0, 0.0f, 10.0, 10.0, 0.0f, 1)
        val location2 = location1.copy()
        val location3 = LocationData()
        val location4 = LocationData(location1.altitude, location1.altitudePrecision, location1.latitude, location1.longitude, location1.altitudePrecision, location1.locationAge)

        assert(location1.equals(location2))
        assert(location1.equals(location4))
        assert(!location1.equals(location3))
        assert(location1.hashCode() == location2.hashCode())
        assert(location1.toString() == location2.toString())
    }

    @Test
    fun testLteData() {
        val lteOEMSV1 = LteOEMSV(
            "1",
            "androidVersion",
            "buildName",
            "customVersion",
            "radioVersion"
        )
        val bearer = Bearer(
            "apnName",
            1
        )
        val bearerList = mutableListOf<Bearer>()
        bearerList.add(bearer)
        val bearerConfiguration1 = BearerConfiguration(
            bearerList,
            "1",
            "numOfBearers",
            "timeStamp"
        )
        val commonRfConfig1 = CommonRFConfiguration(
            1,
            0,
            "ytContentId",
            "ytLink",
            2,
            3,
            "4",
            "timeStamp",
            5,
            6
        )
        val caDataList = getCADataList()

        val downlinkCarrierInfo1 = DownlinkCarrierInfo(
            caDataList,
            "1",
            2,
            "timeStamp"
        )
        val downlinkRFConfiguration1 = DownlinkRFConfiguration(
            caDataList,
            "2",
            "timeStamp"
        )
        val lteLocation1 = LteLocation(
            10.0,
            0.0f,
            10.0,
            10.0,
            0.0f,
            1
        )
        val networkIdentity1 = LteNetworkIdentity(
            caDataList,
            "2",
            "3",
            "4",
            "5",
            "timeStamp",
            "wifiConnectionstatus"
        )
        val lteSettings1 = LteSettings(
            1,
            2,
            3,
            4,
            "timeStamp",
            "roamingSettings",
            "rtt",
            "rttTranscript",
            5
        )
        val secondCarrier = SecondCarrier(
            "rsrp",
            "rsrq",
            "rssi",
            "sinr"
        )

        val thirdCarrier = ThirdCarrier(
            "rsrp",
            "rsrq",
            "rssi",
            "sinr"
        )
        val signalCondition1 = SignalCondition(
            1,
            2,
            3,
            4,
            5,
            6,
            "networkType",
            "timeStamp",
            secondCarrier,
            thirdCarrier
        )
        val upLinkRFConfiguration1 = UpLinkRFConfiguration(
            getCADataList(),
            "1",
            "timeStamp"
        )
        val uplinkCarrierInfo1 = UplinkCarrierInfo(
            getCADataList(),
            "2",
            3,
            "timeStamp"

        )
        val lteData1 = LteSingleSessionReport(
            1,
            "triggerTimestamp",
            "1",
            "schemaVersion",
            lteOEMSV1,
            bearerConfiguration1,
            commonRfConfig1,
            downlinkCarrierInfo1,
            downlinkRFConfiguration1,
            lteLocation1,
            networkIdentity1,
            lteSettings1,
            signalCondition1,
            upLinkRFConfiguration1,
            uplinkCarrierInfo1
        )
        val lteData2 = lteData1.copy()
        val lteData3 = LteSingleSessionReport(
            lteData1.trigger,
            lteData1.triggerTimestamp,
            lteData1.oemApiVersion,
            lteData1.schemaVersion,
            lteData1.oemsv,
            lteData1.bearerConfiguration,
            lteData1.commonRfConfiguration,
            lteData1.downlinkCarrierInfo,
            lteData1.downlinkRFConfiguration,
            lteData1.location,
            lteData1.networkIdentity,
            lteData1.lteSettings,
            lteData1.signalCondition,
            lteData1.upLinkRfConfiguration,
            lteData1.uplinkCarrierInfo
        )

        assert(lteData1.equals(lteData2))
        assert(lteData1.equals(lteData3))
        assert(lteData1.hashCode() == lteData2.hashCode())
        assert(lteData1.toString() == lteData2.toString())
    }

    @Test
    fun testLteLocationData() {
        val lteLocation1 = LteLocation(
            10.0,
            0.0f,
            10.0,
            10.0,
            0.0f,
            1
        )
        val lteLocation3 = LteLocation()
        val lteLocation2 = lteLocation1.copy()
        val lteLocation4 = LteLocation(
            lteLocation1.altitude,
            lteLocation1.altitudePrecision,
            lteLocation1.latitude,
            lteLocation1.longitude,
            lteLocation1.altitudePrecision,
            lteLocation1.locationAge
        )
        assert(lteLocation1 == lteLocation2)
        assert(lteLocation1 == lteLocation4)
        assert(lteLocation1 != lteLocation3)
        assert(lteLocation1.hashCode() == lteLocation2.hashCode())
        assert(lteLocation1.toString() == lteLocation2.toString())
    }

    @Test
    fun testLteOemsvData() {
        val lteOEMSV1 = LteOEMSV(
            "1",
            "androidVersion",
            "buildName",
            "customVersion",
            "radioVersion"
        )
        val lteOEMSV2 = lteOEMSV1.copy()
        val lteOEMSV3 = LteOEMSV(
            lteOEMSV1.softwareVersion,
            lteOEMSV1.androidVersion,
            lteOEMSV1.buildName,
            lteOEMSV1.customVersion,
            lteOEMSV1.radioVersion
        )
        assert(lteOEMSV1 == lteOEMSV2)
        assert(lteOEMSV1 == lteOEMSV3)
        assert(lteOEMSV1.hashCode() == lteOEMSV2.hashCode())
        assert(lteOEMSV1.toString() == lteOEMSV2.toString())
    }

    @Test
    fun testLteSettingsData() {
        val lteSettings1 = LteSettings(
            1,
            2,
            3,
            4,
            "timeStamp",
            "roamingSettings",
            "rtt",
            "rttTranscript",
            5
        )
        val lteSettings2 = lteSettings1.copy()
        val lteSettings3 = LteSettings(
            lteSettings1.wifiCallingSetting,
            lteSettings1.wifiSetting,
            lteSettings1.mobileDataSettings,
            lteSettings1.networkModeSettings,
            lteSettings1.oemTimestamp,
            lteSettings1.roamingSetting,
            lteSettings1.rtt,
            lteSettings1.rttTranscript,
            lteSettings1.volteSetting
        )
        assert(lteSettings1 == lteSettings2)
        assert(lteSettings1 == lteSettings3)
        assert(lteSettings1.hashCode() == lteSettings2.hashCode())
        assert(lteSettings1.toString() == lteSettings2.toString())
    }

    @Test
    fun testNetworkIdentityData() {
        val caDataList = getCADataList()
        val networkIdentity1 = LteNetworkIdentity(
            caDataList,
            "2",
            "3",
            "4",
            "5",
            "timeStamp",
            "wifiConnectionstatus"
        )
        val networkIdentity2 = networkIdentity1.copy()
        val networkIdentity3 = LteNetworkIdentity(
            networkIdentity1.ca,
            networkIdentity1.mcc,
            networkIdentity1.mnc,
            networkIdentity1.tac,
            networkIdentity1.networkType,
            networkIdentity1.oemTimestamp,
            networkIdentity1.wifiConnectionStatus
        )
        assert(networkIdentity1 == networkIdentity2)
        assert(networkIdentity1 == networkIdentity3)
        assert(networkIdentity1.hashCode() == networkIdentity2.hashCode())
        assert(networkIdentity1.toString() == networkIdentity2.toString())
    }

    @Test
    fun testSecondCarrierData() {
        val secondCarrier1 = SecondCarrier(
            "rsrp",
            "rsrq",
            "rssi",
            "sinr"
        )
        val secondCarrier2 = secondCarrier1.copy()
        val secondCarrier3 = SecondCarrier(
            secondCarrier1.rsrp,
            secondCarrier1.rsrq,
            secondCarrier1.rssi,
            secondCarrier1.sinr
        )
        assert(secondCarrier1 == secondCarrier2)
        assert(secondCarrier1 == secondCarrier3)
        assert(secondCarrier1.hashCode() == secondCarrier2.hashCode())
        assert(secondCarrier1.toString() == secondCarrier2.toString())
    }

    @Test
    fun testSignalConditionData() {
        val secondCarrier = SecondCarrier(
            "rsrp",
            "rsrq",
            "rssi",
            "sinr"
        )

        val thirdCarrier = ThirdCarrier(
            "rsrp",
            "rsrq",
            "rssi",
            "sinr"
        )

        val signalCondition1 = SignalCondition(
            1,
            2,
            3,
            4,
            5,
            6,
            "networkType",
            "timeStamp",
            secondCarrier,
            thirdCarrier
        )
        val signalCondition2 = signalCondition1.copy()
        val signalCondition3 = SignalCondition(
            signalCondition1.lteUlHeadroom,
            signalCondition1.rachPower,
            signalCondition1.rsrp,
            signalCondition1.rsrq,
            signalCondition1.rssi,
            signalCondition1.sinr,
            signalCondition1.networkType,
            signalCondition1.oemTimestamp,
            signalCondition1.secondCarrier,
            signalCondition1.thirdCarrier
        )
        assert(signalCondition1 == signalCondition2)
        assert(signalCondition1 == signalCondition3)
        assert(signalCondition1.hashCode() == signalCondition2.hashCode())
        assert(signalCondition1.toString() == signalCondition2.toString())
    }

    @Test
    fun testThirdCarrierData() {
        val thirdCarrier1 = ThirdCarrier(
            "rsrp",
            "rsrq",
            "rssi",
            "sinr"
        )
        val thirdCarrier2 = thirdCarrier1.copy()
        val thirdCarrier3 = ThirdCarrier(
            thirdCarrier1.rsrp,
            thirdCarrier1.rsrq,
            thirdCarrier1.rssi,
            thirdCarrier1.sinr
        )
        assert(thirdCarrier1 == thirdCarrier2)
        assert(thirdCarrier1 == thirdCarrier3)
        assert(thirdCarrier1.hashCode() == thirdCarrier2.hashCode())
        assert(thirdCarrier1.toString() == thirdCarrier2.toString())
    }

    @Test
    fun testUpLinkCarrierInfoData() {
        val uplinkCarrierInfo1 = UplinkCarrierInfo(
            getCADataList(),
            "2",
            3,
            "timeStamp"

        )
        val uplinkCarrierInfo2 = uplinkCarrierInfo1.copy()
        val uplinkCarrierInfo3 = UplinkCarrierInfo(
            uplinkCarrierInfo1.ca,
            uplinkCarrierInfo1.networkType,
            uplinkCarrierInfo1.numberAggregateChannel,
            uplinkCarrierInfo1.oemTimestamp

        )
        assert(uplinkCarrierInfo1 == uplinkCarrierInfo2)
        assert(uplinkCarrierInfo1 == uplinkCarrierInfo3)
        assert(uplinkCarrierInfo1.hashCode() == uplinkCarrierInfo2.hashCode())
        assert(uplinkCarrierInfo1.toString() == uplinkCarrierInfo2.toString())
    }

    @Test
    fun testUpLinkRfConfigurationData() {
        val upLinkRFConfiguration1 = UpLinkRFConfiguration(
            getCADataList(),
            "1",
            "timeStamp"
        )
        val upLinkRFConfiguration2 = upLinkRFConfiguration1.copy()
        val upLinkRFConfiguration3 = UpLinkRFConfiguration(
            upLinkRFConfiguration1.ca,
            upLinkRFConfiguration1.networkType,
            upLinkRFConfiguration1.oemTimestamp
        )
        assert(upLinkRFConfiguration1 == upLinkRFConfiguration2)
        assert(upLinkRFConfiguration1 == upLinkRFConfiguration3)
        assert(upLinkRFConfiguration1.hashCode() == upLinkRFConfiguration2.hashCode())
        assert(upLinkRFConfiguration1.toString() == upLinkRFConfiguration2.toString())
    }

    @Test
    fun testInsertCAEntity(){
        val baseEchoLocateLteEntity =
            BaseEchoLocateLteEntity(
                1,
                "timeStamp",
                "",
                "1",
                "1",
                "sessionId"
            )
        lteDao.insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity)

        val thirdCarrierEntity = ThirdCarrierEntity(
            "rsrp",
            "rsrq",
            "rssi",
            "sinr"
        )
        thirdCarrierEntity.sessionId = "sessionId"
        thirdCarrierEntity.uniqueId = "UniqueId"
        lteDao.insertThirdCarrierEntity(thirdCarrierEntity)
        val thirdCarrierList = lteDao.getThirdCarrierEntity()
        assert(thirdCarrierList.isNotEmpty())

    }

    private fun getCADataList(): List<CAData> {
        val caData = CAData(
            1,
            1,
            2,
            3,
            1,
            "modulation",
            "pci",
            "cellId",
            "locationId"
        )
        val caDataList = mutableListOf<CAData>()
        caDataList.add(caData)
        return caDataList
    }



}