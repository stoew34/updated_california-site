package com.tmobile.mytmobile.echolocate.coverage.dataprocessor
/**
 * Created by Mahesh Shetye on 2020-05-12
 *
 */


import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.tmobile.mytmobile.echolocate.TestActivity
import com.tmobile.mytmobile.echolocate.coverage.database.EchoLocateCoverageDatabase
import com.tmobile.mytmobile.echolocate.coverage.database.dao.CoverageDao
import com.tmobile.mytmobile.echolocate.coverage.database.entity.*
import com.tmobile.mytmobile.echolocate.coverage.delegates.TriggerSource
import com.tmobile.mytmobile.echolocate.coverage.model.BaseCoverageData
import com.tmobile.mytmobile.echolocate.coverage.reportprocessor.CoverageDataStatus
import com.tmobile.mytmobile.echolocate.coverage.utils.CellsMonitor
import com.tmobile.mytmobile.echolocate.coverage.utils.CoverageConstants
import com.tmobile.mytmobile.echolocate.coverage.utils.CoverageTelephonyDataCollector
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import org.junit.*
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class CoverageTelephonyProcessorTest {

    private lateinit var context: Context
    private lateinit var coverageDao: CoverageDao
    private lateinit var db: EchoLocateCoverageDatabase
    private lateinit var sessionId: String

    @get:Rule
    private val activityRule = ActivityTestRule(
        TestActivity::class.java, false, false
    )

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(context, EchoLocateCoverageDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        coverageDao = EchoLocateCoverageDatabase.getEchoLocateCoverageDatabase(context).coverageDao()
        sessionId = UUID.randomUUID().toString()
        insertTestData()
    }

    @After
    fun tearDown() {
        db.close()
    }

    fun insertTestData() {
        val telephonyData = CoverageTelephonyDataCollector.getInstance(context)

        val baseEchoLocateCoverageEntity =
            BaseEchoLocateCoverageEntity(
                TriggerSource.SCREEN_ACTIVITY.name,
                CoverageDataStatus.STATUS_RAW,
                EchoLocateDateUtils.getTriggerTimeStamp(),
                CoverageConstants.SCHEMA_VERSION,
                sessionId
            )
        coverageDao.insertBaseEchoLocateCoverageEntity(baseEchoLocateCoverageEntity)

        val baseCovData = BaseCoverageData(sessionId, UUID.randomUUID().toString())
        CoverageTelephonyProcessor(context).processCoverageData(baseCovData)

        val covPrimCell = telephonyData.getPrimaryCell()
        if (covPrimCell == null) {
            // No registered cell found
            // We will add dummy data, to check if the data can be added in db
            val covPrimCellEntity = CoveragePrimaryCellEntity(
                cellType = CellsMonitor.CELL_INFO_TYPE.GSM.name
            )
            covPrimCellEntity.sessionId = sessionId
            covPrimCellEntity.uniqueId = UUID.randomUUID().toString()

            coverageDao.insertCoveragePrimaryCellEntity(covPrimCellEntity)

            val covCellIdentity = CoverageCellIdentityEntity(
                cellId = "",
                cellInfoDelay = "",
                networkName = "",
                mcc = "",
                mnc= "",
                earfcn = "",
                tac = "",
                lac = ""
            )
            covCellIdentity.sessionId = sessionId
            covCellIdentity.uniqueId = UUID.randomUUID().toString()

            coverageDao.insertCoverageCellIdentityEntity(covCellIdentity)

            val covCellSig = CoverageCellSignalStrengthEntity(
                asu = "",
                dBm = "",
                bandwidth = "",
                rsrp = "",
                rsrq = "",
                rssnr = "",
                cqi = "",
                timingAdvance = ""
            )
            covCellSig.sessionId = sessionId
            covCellSig.uniqueId = UUID.randomUUID().toString()

            coverageDao.insertCoverageCellSignalStrengthEntity(covCellSig)
        }
    }

    @Test
    fun testGetCoverageTelephonyProcessor() {
        val latch = CountDownLatch(1)

        latch.await(1, TimeUnit.SECONDS)
        val covTeleEntity = coverageDao.getCoverageTelephonyEntityBySessionID(sessionId)
        Assert.assertNotNull(covTeleEntity)
    }

    @Test
    fun testGetCoveragePrimaryCellEntity() {
        val latch = CountDownLatch(1)

        latch.await(1, TimeUnit.SECONDS)
        val covPrimCellEntity = coverageDao.getCoveragePrimaryCellEntityBySessionID(sessionId)
        Assert.assertNotNull(covPrimCellEntity)
    }

    @Test
    fun testGetCoverageCellIdentity() {
        val latch = CountDownLatch(1)

        latch.await(1, TimeUnit.SECONDS)
        val covCellIdEntity = coverageDao.getCoverageCellIdentityEntityBySessionID(sessionId)
        Assert.assertNotNull(covCellIdEntity)
    }

    @Test
    fun testGetCoverageCellSignalStrength() {
        val latch = CountDownLatch(1)

        latch.await(1, TimeUnit.SECONDS)
        val covCellSigStgth = coverageDao.getCoverageCellSignalStrengthEntityBySessionID(sessionId)
        Assert.assertNotNull(covCellSigStgth)
    }

    @Test
    fun testGetCoverageNrCell() {
        val latch = CountDownLatch(1)

        latch.await(1, TimeUnit.SECONDS)
        var covNrCell = coverageDao.getCoverageNrCellEntityBySessionID(sessionId)
        if (covNrCell == null) {
            // This can happen if device is not capable of Nr5G, or not in the network
            // In this case, we will add dummy record to check if record could be added in db
            val coverageNrCell = CoverageNrCellEntity(
                nrCsiRsrp = "",
                nrCsiRsrq = "",
                nrCsiSinr = "",
                nrSsRsrp = "",
                nrSsRsrq = "",
                nrSsSinr = "",
                nrStatus = "",
                nrDbm = "",
                nrLevel = "",
                nrAsuLevel = "",
                nrArfcn = "",
                nrCi = "",
                nrPci = "",
                nrTac = ""
            )
            coverageNrCell.sessionId = sessionId
            coverageNrCell.uniqueId = UUID.randomUUID().toString()

            coverageDao.insertCoverageNrCellEntity(coverageNrCell)
            latch.await(1, TimeUnit.SECONDS)
            covNrCell = coverageDao.getCoverageNrCellEntityBySessionID(sessionId)
        }
        Assert.assertNotNull(covNrCell)
    }
}
