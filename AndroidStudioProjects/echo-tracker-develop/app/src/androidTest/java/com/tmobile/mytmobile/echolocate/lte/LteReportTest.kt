package com.tmobile.mytmobile.echolocate.lte

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.lte.database.EchoLocateLteDatabase
import com.tmobile.mytmobile.echolocate.lte.database.dao.LteDao
import com.tmobile.mytmobile.echolocate.lte.database.entity.BaseEchoLocateLteEntity
import com.tmobile.mytmobile.echolocate.lte.database.entity.LteSettingsEntity
import com.tmobile.mytmobile.echolocate.lte.database.entity.LteSingleSessionReportEntity
import com.tmobile.mytmobile.echolocate.lte.database.entity.UpLinkRFConfigurationEntity
import com.tmobile.mytmobile.echolocate.lte.manager.LteDataManager
import com.tmobile.mytmobile.echolocate.lte.reportprocessor.LteDataStatus
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*

/**
 * This class is responsible create and assert test case for LteReports
 */
@RunWith(AndroidJUnit4::class)
class LteReportTest {


    private lateinit var lteDao: LteDao
    private lateinit var instrumentationContext: Context
    private var lteDataManager: LteDataManager? = null


    @Before
    fun setUp() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().targetContext
        lteDao = EchoLocateLteDatabase.getEchoLocateLteDatabase(instrumentationContext).lteDao()
        lteDataManager = LteDataManager(instrumentationContext)

    }

    /**
     * Inserts and delete the report data from db and asserts the conditions
     */
    @Test
    fun insertLteReportData() {
        val ctx = InstrumentationRegistry.getInstrumentation().context
        val inputStream = ctx.resources.assets.open("lteData.json")
        val json = readDataStream(inputStream)
        val lteSingleSessionReportEntity = LteSingleSessionReportEntity(
            "test",
            json,
            "2019-12-08T08:54:52.277-0700"
        )
        val lteSingleSessionReportEntity1 = LteSingleSessionReportEntity(
            "test1",
            json,
            "2019-12-07T08:54:52.277-0700"
        )
        val lteSingleSessionReportEntity2 = LteSingleSessionReportEntity(
            "test2",
            json,
            "2019-12-06T08:54:52.277-0700"
        )
        lteDao.insertLteSingleSessionReportEntity(lteSingleSessionReportEntity)
        lteDao.insertLteSingleSessionReportEntity(lteSingleSessionReportEntity1)
        lteDao.insertLteSingleSessionReportEntity(lteSingleSessionReportEntity2)
        val sessionId1  = UUID.randomUUID().toString()
        val sessionId2  = UUID.randomUUID().toString()
        val baseEchoLocateLteEntity =
            BaseEchoLocateLteEntity(
                1,
                LteDataStatus.STATUS_PROCESSED,
                "",
                "1",
                "1",
                sessionId1
            )
        lteDao.insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity)
        val baseEchoLocateLteEntity2 =
            BaseEchoLocateLteEntity(
                1,
                LteDataStatus.STATUS_PROCESSED,
                "",
                "1",
                "1",
                sessionId1
            )
        lteDao.insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity2)
        val baseEchoLocateLteEntity3 =
            BaseEchoLocateLteEntity(
                1,
                LteDataStatus.STATUS_PROCESSED,
                "",
                "1",
                "1",
                sessionId2
            )
        lteDao.insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity3)
        val upLinkRFConfigurationEntity1 = UpLinkRFConfigurationEntity(
            "1",
            "timeStamp"
        )
        upLinkRFConfigurationEntity1.sessionId = sessionId1
        upLinkRFConfigurationEntity1.uniqueId = sessionId1

        lteDao.insertUpLinkRFConfigurationEntity(upLinkRFConfigurationEntity1)

        val upLinkRFConfigurationEntity2 = UpLinkRFConfigurationEntity(
            "1",
            "timeStamp"
        )
        upLinkRFConfigurationEntity2.sessionId = sessionId1
        upLinkRFConfigurationEntity2.uniqueId = sessionId1

        lteDao.insertUpLinkRFConfigurationEntity(upLinkRFConfigurationEntity2)
        val upLinkRFConfigurationEntity3 = UpLinkRFConfigurationEntity(
            "1",
            "timeStamp"
        )
        upLinkRFConfigurationEntity3.sessionId = sessionId2
        upLinkRFConfigurationEntity3.uniqueId = sessionId2

        lteDao.insertUpLinkRFConfigurationEntity(upLinkRFConfigurationEntity3)

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
        lteSettingsEntity1.sessionId = sessionId1

        lteDao.insertLteSettingsEntity(lteSettingsEntity1)

        assert(lteDao.getProcessedReports().isNotEmpty())
//        lteDao.deleteProcessedReportsWithTimeRange(
//            "2019-05-13T08:54:52.277-0700",
//            "2019-05-15T08:54:52.277-0700"
//        )
        assert(lteDao.getProcessedReports().isEmpty())
    }

    @Throws(Exception::class)
    fun readDataStream(inputStream: InputStream): String {
        val result = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var length: Int = inputStream.read(buffer)
        while (length != -1) {
            result.write(buffer, 0, length)
            length = inputStream.read(buffer)
        }
        return result.toString("UTF-8")
    }

}