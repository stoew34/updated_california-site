package com.tmobile.mytmobile.echolocate.nr5g

import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.EchoLocateNr5gDatabase
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.dao.Nr5gDao
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.*
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.model.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class Nr5GDatabaseTest {

    private lateinit var nr5GDao: Nr5gDao
    private lateinit var db: EchoLocateNr5gDatabase
    lateinit var context: Context

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, EchoLocateNr5gDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        nr5GDao = EchoLocateNr5gDatabase.getEchoLocateNr5gDatabase(context).nr5gDao()
    }

    @Test
    fun testBaseEchoLocateLteEntity() {
        val baseEcholocateNr5GEntity1 =
            BaseEchoLocateNr5gEntity(
                200,
                "RAWData",
                "timestamp",
                "123245"
            )
        val baseEcholocateNr5GEntity2 = baseEcholocateNr5GEntity1.copy()
        assert(baseEcholocateNr5GEntity1 == baseEcholocateNr5GEntity2)
        assert(baseEcholocateNr5GEntity1.hashCode() == baseEcholocateNr5GEntity2.hashCode())
        assert(baseEcholocateNr5GEntity1.toString() == baseEcholocateNr5GEntity2.toString())
    }

    @Test
    fun testBaseFiveGEntity() {
        val baseNr5GEntity1 = BaseEntity("sessionID", "uniqueID")
        val baseNr5GEntity2 = BaseEntity(baseNr5GEntity1.sessionId, baseNr5GEntity1.uniqueId)
        assert(baseNr5GEntity1 == baseNr5GEntity2)
        baseNr5GEntity2.sessionId = "sessionId2"
        baseNr5GEntity2.uniqueId = "uniqueID2"
        assert(baseNr5GEntity1 != baseNr5GEntity2)
    }

    @Test
    fun testConnectedWifiStatusEntityEntity() {
        val connectedWifiStatusEntity1 = ConnectedWifiStatusEntity(
            "bssId",
            "bssLoad",
            "ssId",
            0,
            "capabilities",
            1,
            2,
            "channelMode",
            3,
            4,
            "operatorFriendlyName",
            5,
            6
        )
        val connectedWifiStatusEntity2 = connectedWifiStatusEntity1.copy()
        assert(connectedWifiStatusEntity1 == connectedWifiStatusEntity2)
        assert(connectedWifiStatusEntity1.hashCode() == connectedWifiStatusEntity2.hashCode())
        assert(connectedWifiStatusEntity1.toString() == connectedWifiStatusEntity2.toString())
    }

    @Test
    fun testEndcLteLogEntity() {
        val endcLteLogEntity1 = EndcLteLogEntity(
            "timeStamp",
            1,
            2,
            0,
            3,
            1
        )
        val endcLteLogEntity2 = endcLteLogEntity1.copy()
        assert(endcLteLogEntity1 == endcLteLogEntity2)
        assert(endcLteLogEntity1.hashCode() == endcLteLogEntity2.hashCode())
        assert(endcLteLogEntity1.toString() == endcLteLogEntity2.toString())
    }

    @Test
    fun testEndcUpLinkLogEntity() {
        val endcUplinkLogEntity1 = EndcUplinkLogEntity(
            "timeStamp",
            1,
            2
        )
        val endcUplinkLogEntity2 = endcUplinkLogEntity1.copy()
        assert(endcUplinkLogEntity1 == endcUplinkLogEntity2)
        assert(endcUplinkLogEntity1.hashCode() == endcUplinkLogEntity2.hashCode())
        assert(endcUplinkLogEntity1.toString() == endcUplinkLogEntity2.toString())
    }

    @Test
    fun testFiveGUiLogEntity() {
        val fiveGUiLogEntity1 = Nr5gUiLogEntity(
            "timeStamp",
            1,
            "uiNetworkType",
            "uiDataTransmission",
            2,
            3
        )
        val fiveGUiLogEntity2 = fiveGUiLogEntity1.copy()
        assert(fiveGUiLogEntity1 == fiveGUiLogEntity2)
        assert(fiveGUiLogEntity1.hashCode() == fiveGUiLogEntity2.hashCode())
        assert(fiveGUiLogEntity1.toString() == fiveGUiLogEntity2.toString())
    }

    @Test
    fun testGetActiveNetworkEntity() {
        val getActiveNetworkEntity1 = Nr5gActiveNetworkEntity(
            3
        )
        val getActiveNetworkEntity2 = getActiveNetworkEntity1.copy()
        assert(getActiveNetworkEntity1 == getActiveNetworkEntity2)
        assert(getActiveNetworkEntity1.hashCode() == getActiveNetworkEntity2.hashCode())
        assert(getActiveNetworkEntity1.toString() == getActiveNetworkEntity2.toString())
    }

    @Test
    fun testGetDataNetworkTypeEntity() {
        val getDataNetworkTypeEntity1 = Nr5gDataNetworkTypeEntity(
            "timeStamp",
            3
        )
        val getDataNetworkTypeEntity2 = getDataNetworkTypeEntity1.copy()
        assert(getDataNetworkTypeEntity1 == getDataNetworkTypeEntity2)
        assert(getDataNetworkTypeEntity1.hashCode() == getDataNetworkTypeEntity2.hashCode())
        assert(getDataNetworkTypeEntity1.toString() == getDataNetworkTypeEntity2.toString())
    }

    @Test
    fun testGetNetworkIdentityEntity() {
        val getNetworkIdentityEntity1 = Nr5gNetworkIdentityEntity(
            "timeStamp",
            1,
            "310",
            "160",
            2222,
            21,
            22,
            23
        )
        val getNetworkIdentityEntity2 = getNetworkIdentityEntity1.copy()
        assert(getNetworkIdentityEntity1 == getNetworkIdentityEntity2)
        assert(getNetworkIdentityEntity1.hashCode() == getNetworkIdentityEntity2.hashCode())
        assert(getNetworkIdentityEntity1.toString() == getNetworkIdentityEntity2.toString())
    }

    @Test
    fun testGetNrStatusEntity() {
        val getNrStatusEntity1 = Nr5gStatusEntity(
            3
        )
        val getNrStatusEntity2 = getNrStatusEntity1.copy()
        assert(getNrStatusEntity1 == getNrStatusEntity2)
        assert(getNrStatusEntity1.hashCode() == getNrStatusEntity2.hashCode())
        assert(getNrStatusEntity1.toString() == getNrStatusEntity2.toString())
    }

    @Test
    fun testGetWifiStateEntity() {
        val getWifiStateEntity1 = Nr5gWifiStateEntity(
            3
        )
        val getWifiStateEntity2 = getWifiStateEntity1.copy()
        assert(getWifiStateEntity1 == getWifiStateEntity2)
        assert(getWifiStateEntity1.hashCode() == getWifiStateEntity2.hashCode())
        assert(getWifiStateEntity1.toString() == getWifiStateEntity2.toString())
    }

    @Test
    fun testNr5GDeviceInfoEntity() {
        val nr5GDeviceInfoEntity1 = Nr5gDeviceInfoEntity(
            "3",
            "1",
            "2",
            "4",
            "5",
        "modelCode",
            "oem"
        )
        val nr5GDeviceInfoEntity2 = nr5GDeviceInfoEntity1.copy()
        assert(nr5GDeviceInfoEntity1 == nr5GDeviceInfoEntity2)
        assert(nr5GDeviceInfoEntity1.hashCode() == nr5GDeviceInfoEntity2.hashCode())
        assert(nr5GDeviceInfoEntity1.toString() == nr5GDeviceInfoEntity2.toString())
    }

    @Test
    fun testNr5GLocationEntity() {
        val nr5GLocationEntity1 = Nr5gLocationEntity(
            10.0,
            10f,
            10.0,
            10.0,
            10f,
            "",
            1
        )
        val nr5GLocationEntity2 = nr5GLocationEntity1.copy()
        assert(nr5GLocationEntity1 == nr5GLocationEntity2)
        assert(nr5GLocationEntity1.hashCode() == nr5GLocationEntity2.hashCode())
        assert(nr5GLocationEntity1.toString() == nr5GLocationEntity2.toString())
    }

    @Test
    fun testNr5GMmwCellLogEntity() {
        val nr5GMmwCellLogEntity1 = Nr5gMmwCellLogEntity(
            "timeStamp",
            10,
            0,
            10,
            10f,
            0f,
            1f,
            1,
            2f,
            3f,
            4f,
            "nrBandName",
            5,
            7
        )
        val nr5GMmwCellLogEntity2 = nr5GMmwCellLogEntity1.copy()
        assert(nr5GMmwCellLogEntity1 == nr5GMmwCellLogEntity2)
        assert(nr5GMmwCellLogEntity1.hashCode() == nr5GMmwCellLogEntity2.hashCode())
        assert(nr5GMmwCellLogEntity1.toString() == nr5GMmwCellLogEntity2.toString())
    }

    @Test
    fun testNr5GOEMSVEntity() {
        val nr5GOEMSVEntity1 = Nr5gOEMSVEntity(
            "softwareVersoin",
            "10",
            "0",
            "10",
            "2"

        )
        val nr5GOEMSVEntity2 = nr5GOEMSVEntity1.copy()
        assert(nr5GOEMSVEntity1 == nr5GOEMSVEntity2)
        assert(nr5GOEMSVEntity1.hashCode() == nr5GOEMSVEntity2.hashCode())
        assert(nr5GOEMSVEntity1.toString() == nr5GOEMSVEntity2.toString())
    }

    @Test
    fun testTriggerEntity() {
        val triggerEntity1 = Nr5gTriggerEntity(
            "timeStamp",
            10,
            "triggerApp",
            10
        )
        val triggerEntity2 = triggerEntity1.copy()
        assert(triggerEntity2 == triggerEntity1)
        assert(triggerEntity2.hashCode() == triggerEntity1.hashCode())
        assert(triggerEntity2.toString() == triggerEntity1.toString())
    }

    @Test
    fun testBaseFiveGData() {
        val baseNr5GData1 = BaseNr5gData("sessionID", "uniqueID")
        val baseNr5GData2 = BaseNr5gData(baseNr5GData1.sessionId, baseNr5GData1.uniqueId)
        assert(baseNr5GData1 == baseNr5GData2)
        baseNr5GData2.sessionId = "sessionId2"
        baseNr5GData2.uniqueId = "uniqueID2"
        assert(baseNr5GData1 != baseNr5GData2)
    }

    @Test
    fun testConnectedWifiStatus() {
        val connectedWifiStatus1 = ConnectedWifiStatus(
            "bssId",
            "bssLoad",
            "ssId",
            0,
            "capabilities",
            1,
            2,
            "channelMode",
            3,
            4,
            "operatorFriendlyName",
            5,
            6
        )
        val connectedWifiStatus2 = connectedWifiStatus1.copy()
        assert(connectedWifiStatus1 == connectedWifiStatus2)
        assert(connectedWifiStatus1.hashCode() == connectedWifiStatus2.hashCode())
        assert(connectedWifiStatus1.toString() == connectedWifiStatus2.toString())
    }

    @Test
    fun testEndcLteLogData() {
        val endcLteLog1 = EndcLteLog(
            "timeStamp",
            1,
            2,
            0,
            3,
            1
        )
        val endcLteLog2 = endcLteLog1.copy()
        assert(endcLteLog1 == endcLteLog2)
        assert(endcLteLog1.hashCode() == endcLteLog2.hashCode())
        assert(endcLteLog1.toString() == endcLteLog2.toString())
    }

    @Test
    fun testEndcUpLinkLogData() {
        val endcUplinkLog1 = EndcUplinkLog(
            "timeStamp",
            1,
            2
        )
        val endcUplinkLog2 = endcUplinkLog1.copy()
        assert(endcUplinkLog1 == endcUplinkLog2)
        assert(endcUplinkLog1.hashCode() == endcUplinkLog2.hashCode())
        assert(endcUplinkLog1.toString() == endcUplinkLog2.toString())
    }

    @Test
    fun testFiveGUiLogData() {
        val fiveGUiLog1 = Nr5gUiLog(
            "timeStamp",
            1,
            "uiNetworkType",
            "uiDataTransmission",
            2,
            3
        )
        val fiveGUiLog2 = fiveGUiLog1.copy()
        assert(fiveGUiLog1 == fiveGUiLog2)
        assert(fiveGUiLog1.hashCode() == fiveGUiLog2.hashCode())
        assert(fiveGUiLog1.toString() == fiveGUiLog2.toString())
    }

    @Test
    fun testGetActiveNetworkData() {
        val getActiveNetwork1 = Nr5gActiveNetwork(
            3
        )
        val getActiveNetwork2 = getActiveNetwork1.copy()
        assert(getActiveNetwork1 == getActiveNetwork2)
        assert(getActiveNetwork1.hashCode() == getActiveNetwork2.hashCode())
        assert(getActiveNetwork1.toString() == getActiveNetwork2.toString())
    }

    @Test
    fun testGetDataNetworkTypeData() {
        val getDataNetworkType1 = Nr5gDataNetworkType(
            "timeStamp",
            3
        )
        val getDataNetworkType2 = getDataNetworkType1.copy()
        assert(getDataNetworkType1 == getDataNetworkType2)
        assert(getDataNetworkType1.hashCode() == getDataNetworkType2.hashCode())
        assert(getDataNetworkType1.toString() == getDataNetworkType2.toString())
    }

    @Test
    fun testGetNetworkIdentityData() {
        val getNetworkIdentity1 = Nr5gNetworkIdentity(
            "timeStamp",
            1,
            "310",
            "160",
            2222,
            21,
            22,
            23
        )
        val getNetworkIdentity2 = getNetworkIdentity1.copy()
        assert(getNetworkIdentity1 == getNetworkIdentity2)
        assert(getNetworkIdentity1.hashCode() == getNetworkIdentity2.hashCode())
        assert(getNetworkIdentity1.toString() == getNetworkIdentity2.toString())
    }

    @Test
    fun testGetNrStatusData() {
        val getNrStatus1 = Nr5gStatus(
            3
        )
        val getNrStatus2 = getNrStatus1.copy()
        assert(getNrStatus1 == getNrStatus2)
        assert(getNrStatus1.hashCode() == getNrStatus2.hashCode())
        assert(getNrStatus1.toString() == getNrStatus2.toString())
    }

    @Test
    fun testNr5GDeviceInfoData() {
        val nr5GDeviceInfo1 = Nr5gDeviceInfo(
            "3",
            "3",
            "2",
            "4",
            "5",
            "modelCode",
            "oem"
        )
        val nr5GDeviceInfo2 = nr5GDeviceInfo1.copy()
        assert(nr5GDeviceInfo1 == nr5GDeviceInfo2)
        assert(nr5GDeviceInfo1.hashCode() == nr5GDeviceInfo2.hashCode())
        assert(nr5GDeviceInfo1.toString() == nr5GDeviceInfo2.toString())
    }

    @Test
    fun testNr5GLocationData() {
        val nr5GLocation1 = Nr5gLocation(
            10.0,
            10f,
            10.0,
            10.0,
            10f,
            "",
            1
        )
        val nr5GLocation2 = nr5GLocation1.copy()
        assert(nr5GLocation1 == nr5GLocation2)
        assert(nr5GLocation1.hashCode() == nr5GLocation2.hashCode())
        assert(nr5GLocation1.toString() == nr5GLocation2.toString())
    }

    @Test
    fun testNr5GMmwCellLogData() {
        val nr5GMmwCellLog1 = Nr5gMmwCellLog(
            "timeStamp",
            10,
            0,
            10,
            10f,
            0f,
            1f,
            1,
            2f,
            3f,
            4f,
            "nrBandName",
            5,
            7
        )
        val nr5GMmwCellLog2 = nr5GMmwCellLog1.copy()
        assert(nr5GMmwCellLog1 == nr5GMmwCellLog2)
        assert(nr5GMmwCellLog1.hashCode() == nr5GMmwCellLog2.hashCode())
        assert(nr5GMmwCellLog1.toString() == nr5GMmwCellLog2.toString())
    }

    @Test
    fun testNr5GOEMSVData() {
        val nr5GOEMSV1 = Nr5gOEMSV(
            "softwareVersoin",
            "10",
            "0",
            "10",
            "2"
        )
        val nr5GOEMSV2 = nr5GOEMSV1.copy()
        assert(nr5GOEMSV1 == nr5GOEMSV2)
        assert(nr5GOEMSV1.hashCode() == nr5GOEMSV2.hashCode())
        assert(nr5GOEMSV1.toString() == nr5GOEMSV2.toString())
    }

    @Test
    fun testTriggerData() {
        val trigger1 = Nr5gTrigger(
            "timeStamp",
            10,
            "triggerApp",
            10
        )
        val trigger2 = trigger1.copy()
        assert(trigger2 == trigger1)
        assert(trigger2.hashCode() == trigger1.hashCode())
        assert(trigger2.toString() == trigger1.toString())
    }

    @Test
    fun testGetWifiStateData() {
        val getWiFiState1 = Nr5gWiFiState(
            3
        )
        val getWiFiState2 = getWiFiState1.copy()
        assert(getWiFiState2 == getWiFiState1)
        assert(getWiFiState2.hashCode() == getWiFiState1.hashCode())
        assert(getWiFiState2.toString() == getWiFiState1.toString())
    }

    @Test
    fun testInsertEndcLinkLog() {
        val sessionID = "123245"
        val baseEcholocateNr5GEntity = BaseEchoLocateNr5gEntity(
            200,
            "RAWData",
            "timestamp",
            sessionID
        )
        val endcUplinkLogEntity = EndcUplinkLogEntity(
            "timeStamp",
            1,
            2
        )
        endcUplinkLogEntity.sessionId = sessionID
        endcUplinkLogEntity.uniqueId = "uniqueId"
        nr5GDao.insertBaseEchoLocateNr5gEntity(baseEcholocateNr5GEntity)
        nr5GDao.insertEndcUplinkLogEntity(endcUplinkLogEntity)
        val result = nr5GDao.getEndcUplinkLogEntity(baseEcholocateNr5GEntity.sessionId)
        Assert.assertNotNull(result)
    }
}