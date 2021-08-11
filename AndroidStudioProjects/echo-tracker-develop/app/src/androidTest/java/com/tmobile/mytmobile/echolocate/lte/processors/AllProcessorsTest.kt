package com.tmobile.mytmobile.echolocate.lte.processors

import android.content.Context
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.lte.oemdata.LteBaseDataMetricsWrapper
import com.tmobile.mytmobile.echolocate.lte.database.EchoLocateLteDatabase
import com.tmobile.mytmobile.echolocate.lte.database.dao.LteDao
import com.tmobile.mytmobile.echolocate.lte.database.entity.BaseEchoLocateLteEntity
import com.tmobile.mytmobile.echolocate.lte.dataprocessor.*
import com.tmobile.mytmobile.echolocate.lte.model.LteMetricsData
import com.tmobile.mytmobile.echolocate.lte.reportprocessor.LteDataStatus
import com.tmobile.mytmobile.echolocate.utils.EchoLocateDateUtils
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class AllProcessorsTest {

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
    fun testInsertBearerConfigurationEntity() {
        val latch = CountDownLatch(1)
        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateLteEntity =
            BaseEchoLocateLteEntity(
                201,
                LteDataStatus.STATUS_RAW,
                EchoLocateDateUtils.getTriggerTimeStamp(),
                "1",
                "2.0",
                sessionId
            )

        lteDao.insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity)
        val sourceList = mutableListOf<String>()
        sourceList.add(0, "")
        sourceList.add(1, "1")
        sourceList.add(2, "4")
        sourceList.add(3, "4")
        sourceList.add(4, "fast.t-mobile.com")
        sourceList.add(5, "5")
        sourceList.add(6, "ims")
        sourceList.add(7, "-999")
        sourceList.add(8, "-999")
        sourceList.add(9, "-999")
        sourceList.add(10, "-999")


        BearerConfigurationProcessor(context).execute(
            LteMetricsData(
                sourceList,
                baseEchoLocateLteEntity.triggerTimestamp,
                LteBaseDataMetricsWrapper.ApiVersion.VERSION_1,
                sessionId
            )
        )
        latch.await(1, TimeUnit.SECONDS)
        val bearerConfigurationEntityList = lteDao.getBearerConfigurationEntity()
        assert(bearerConfigurationEntityList.isNotEmpty())

        val bearerEntityList = lteDao.getBearerEntity()
        assert(bearerEntityList.isNotEmpty())
    }

    @Test
    fun testInsertUpLinkRFConfigurationEntity() {
        val latch = CountDownLatch(1)
        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateLteEntity =
            BaseEchoLocateLteEntity(
                201,
                LteDataStatus.STATUS_RAW,
                EchoLocateDateUtils.getTriggerTimeStamp(),
                "1",
                "2.0",
                sessionId
            )

        lteDao.insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity)
        val sourceList = mutableListOf<String>()
        sourceList.add(0, "1")
        sourceList.add(1, "1")
        sourceList.add(2, "4")
        sourceList.add(3, "4")
        sourceList.add(4, "4")
        sourceList.add(5, "1")
        sourceList.add(6, "1")
        sourceList.add(7, "1")
        sourceList.add(8, "4")
        sourceList.add(9, "1")
        sourceList.add(10, "1")
        sourceList.add(11, "1")
        sourceList.add(12, "1")
        sourceList.add(13, "1")

        UpLinkRFConfigurationProcessor(context).execute(
            LteMetricsData(
                sourceList,
                baseEchoLocateLteEntity.triggerTimestamp,
                LteBaseDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION,
                sessionId
            )
        )
        latch.await(1, TimeUnit.SECONDS)
        val upLinkRFConfigurationEntityList = lteDao.getUpLinkRFConfigurationEntityList()
        assert(upLinkRFConfigurationEntityList.isNotEmpty())

        val caEntityList = lteDao.getCAEntity()
        assert(caEntityList.isNotEmpty())
    }

    @Test
    fun testInsertUpLinkRFConfigurationEntityV1() {
        val latch = CountDownLatch(1)
        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateLteEntity =
            BaseEchoLocateLteEntity(
                201,
                LteDataStatus.STATUS_RAW,
                EchoLocateDateUtils.getTriggerTimeStamp(),
                "1",
                "2.0",
                sessionId
            )

        lteDao.insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity)
        val sourceList = mutableListOf<String>()
        sourceList.add(0, "1")
        sourceList.add(1, "1")
        sourceList.add(2, "4")
        sourceList.add(3, "4")


        UpLinkRFConfigurationProcessor(context).execute(
            LteMetricsData(
                sourceList,
                baseEchoLocateLteEntity.triggerTimestamp,
                LteBaseDataMetricsWrapper.ApiVersion.VERSION_1,
                sessionId
            )
        )
        latch.await(1, TimeUnit.SECONDS)
        val upLinkRFConfigurationEntityList = lteDao.getUpLinkRFConfigurationEntityList()
        assert(upLinkRFConfigurationEntityList.isNotEmpty())

        val caEntityList = lteDao.getCAEntity()
        assert(caEntityList.isNotEmpty())
    }

    @Test
    fun testInsertDownLinkRFConfigurationEntity() {
        val latch = CountDownLatch(1)
        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateLteEntity =
            BaseEchoLocateLteEntity(
                201,
                EchoLocateDateUtils.getTriggerTimeStamp(),
                "1",
                "2",
                "1",
                sessionId
            )

        lteDao.insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity)
        val sourceList = mutableListOf<String>()
        sourceList.add(0, "")
        sourceList.add(1, "1")
        sourceList.add(2, "64QAM")
        sourceList.add(3, "64QAM")

        DownLinkRFConfigurationProcessor(context).execute(
            LteMetricsData(
                sourceList,
                baseEchoLocateLteEntity.triggerTimestamp,
                LteBaseDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION,
                sessionId
            )
        )
        latch.await(1, TimeUnit.SECONDS)
        val downLinkRFConfigurationEntityList = lteDao.getDownLinkRFConfigurationInfoEntity()
        assert(downLinkRFConfigurationEntityList.isNotEmpty())
        val caEntityList = lteDao.getCAEntity()
        assert(caEntityList.isNotEmpty())
    }

    @Test
    fun testInsertDownLinkRFConfigurationEntityV1() {
        val latch = CountDownLatch(1)
        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateLteEntity =
            BaseEchoLocateLteEntity(
                201,
                EchoLocateDateUtils.getTriggerTimeStamp(),
                "1",
                "2",
                "1",
                sessionId
            )

        lteDao.insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity)
        val sourceList = mutableListOf<String>()
        sourceList.add(0, "")
        sourceList.add(1, "1")
        sourceList.add(2, "64QAM")
        sourceList.add(3, "64QAM")

        DownLinkRFConfigurationProcessor(context).execute(
            LteMetricsData(
                sourceList,
                baseEchoLocateLteEntity.triggerTimestamp,
                LteBaseDataMetricsWrapper.ApiVersion.VERSION_1,
                sessionId
            )
        )
        latch.await(1, TimeUnit.SECONDS)
        val downLinkRFConfigurationEntityList = lteDao.getDownLinkRFConfigurationInfoEntity()
        assert(downLinkRFConfigurationEntityList.isNotEmpty())
        val caEntityList = lteDao.getCAEntity()
        assert(caEntityList.isNotEmpty())
    }

    @Test
    fun testInsertDownLinkRFConfigurationEntityV3() {
        val latch = CountDownLatch(1)
        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateLteEntity =
            BaseEchoLocateLteEntity(
                201,
                EchoLocateDateUtils.getTriggerTimeStamp(),
                "1",
                "2",
                "1",
                sessionId
            )

        lteDao.insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity)
        val sourceList = mutableListOf<String>()
        sourceList.add(0, "")
        sourceList.add(1, "1")
        sourceList.add(2, "64QAM")
        sourceList.add(3, "64QAM")

        DownLinkRFConfigurationProcessor(context).execute(
            LteMetricsData(
                sourceList,
                baseEchoLocateLteEntity.triggerTimestamp,
                LteBaseDataMetricsWrapper.ApiVersion.VERSION_3,
                sessionId
            )
        )
        latch.await(1, TimeUnit.SECONDS)
        val downLinkRFConfigurationEntityList = lteDao.getDownLinkRFConfigurationInfoEntity()
        assert(downLinkRFConfigurationEntityList.isNotEmpty())
        val caEntityList = lteDao.getCAEntity()
        assert(caEntityList.isNotEmpty())
    }

    @Test
    fun testInsertNetworkIdentityEntity(){
        val latch = CountDownLatch(1)
        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateLteEntity =
            BaseEchoLocateLteEntity(
                201,
                "",
                "",
                EchoLocateDateUtils.getTriggerTimeStamp(),
                "2.0",
                sessionId

            )

        lteDao.insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity)
        val sourceList = mutableListOf<String>()
        sourceList.add(0, "1")
        sourceList.add(1,"310")
        sourceList.add(2, "260")
        sourceList.add(3,"11334")
        sourceList.add(4,"1")
        sourceList.add(5,"0")
        sourceList.add(6,"0")
        sourceList.add(7, "0")
        sourceList.add(8, "0")
        sourceList.add(9,"310")
        sourceList.add(10, "260")
        sourceList.add(11,"11334")
        sourceList.add(12,"1")
        sourceList.add(13,"0")

        NetworkIdentityProcessor(context).execute(
            LteMetricsData(
                sourceList,
                baseEchoLocateLteEntity.triggerTimestamp,
                LteBaseDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION,
                sessionId
            )
        )
        latch.await(1, TimeUnit.SECONDS)
        val caEntityList = lteDao.getCAEntity()
        assert(caEntityList.isNotEmpty())
    }

    @Test
    fun testInsertNetworkIdentityEntityV1(){
        val latch = CountDownLatch(1)
        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateLteEntity =
            BaseEchoLocateLteEntity(
                201,
                "",
                "",
                EchoLocateDateUtils.getTriggerTimeStamp(),
                "2.0",
                sessionId

            )

        lteDao.insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity)
        val sourceList = mutableListOf<String>()
        sourceList.add(0, "1")
        sourceList.add(1,"310")
        sourceList.add(2, "260")
        sourceList.add(3,"11334")
        sourceList.add(4,"1")
        sourceList.add(5,"0")
        sourceList.add(6,"0")
        sourceList.add(7, "0")
        sourceList.add(8, "0")
        sourceList.add(9,"310")
        sourceList.add(10, "260")

        NetworkIdentityProcessor(context).execute(
            LteMetricsData(
                sourceList,
                baseEchoLocateLteEntity.triggerTimestamp,
                LteBaseDataMetricsWrapper.ApiVersion.VERSION_1,
                sessionId
            )
        )
        latch.await(1, TimeUnit.SECONDS)
        val caEntityList = lteDao.getCAEntity()
        assert(caEntityList.isNotEmpty())
    }

    @Test
    fun testInsertNetworkIdentityEntityV3(){
        val latch = CountDownLatch(1)
        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateLteEntity =
            BaseEchoLocateLteEntity(
                201,
                "",
                "",
                EchoLocateDateUtils.getTriggerTimeStamp(),
                "2.0",
                sessionId

            )

        lteDao.insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity)
        val sourceList = mutableListOf<String>()
        sourceList.add(0, "1")
        sourceList.add(1,"310")
        sourceList.add(2, "260")
        sourceList.add(3,"11334")
        sourceList.add(4,"1")
        sourceList.add(5,"0")
        sourceList.add(6,"0")
        sourceList.add(7, "0")
        sourceList.add(8, "0")
        sourceList.add(9,"310")
        sourceList.add(10, "260")

        NetworkIdentityProcessor(context).execute(
            LteMetricsData(
                sourceList,
                baseEchoLocateLteEntity.triggerTimestamp,
                LteBaseDataMetricsWrapper.ApiVersion.VERSION_3,
                sessionId
            )
        )
        latch.await(1, TimeUnit.SECONDS)
        val caEntityList = lteDao.getCAEntity()
        assert(caEntityList.isNotEmpty())
    }

    @Test
    fun testInsertUpLinkCarrierInfoEntity() {
        val latch = CountDownLatch(1)
        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateLteEntity =
            BaseEchoLocateLteEntity(
                201,
                LteDataStatus.STATUS_RAW,
                EchoLocateDateUtils.getTriggerTimeStamp(),
                "1",
                "2.0",
                sessionId
            )

        lteDao.insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity)
        val sourceList = mutableListOf<String>()
        sourceList.add(0, "")
        sourceList.add(1, "66786")
        sourceList.add(2, "66")
        sourceList.add(3, "20")
        sourceList.add(4, "1")
        sourceList.add(5, "66786")
        sourceList.add(6, "66")
        sourceList.add(7, "20")
        sourceList.add(8, "1")

        UpLinkCarrierInfoProcessor(context).execute(
            LteMetricsData(
                sourceList,
                baseEchoLocateLteEntity.triggerTimestamp,
                LteBaseDataMetricsWrapper.ApiVersion.VERSION_1,
                sessionId
            )
        )
        latch.await(1, TimeUnit.SECONDS)
        val upLinkCarrierInfoEntity = lteDao.getUpLinkCarrierInfoEntity()
        assert(upLinkCarrierInfoEntity.isNotEmpty())

        val caEntityList = lteDao.getCAEntity()
        assert(caEntityList.isNotEmpty())
    }

    /**
     * Test case for the RF configuration entity
     */
    @Test
    fun testInsertRFConfigurationEntity() {
        val latch = CountDownLatch(1)
        val yTLink =
            "https://s.youtube.com/api/stats/playback?cl=279650211&ei=YvfMXfmHBIe03LUPloiO6Ac&fexp=1714247%2C38810003%2C23839597%2C23842986%2C23848795%2C23817286%2C23856906%2C38810002%2C23853235%2C23851051%2C23837993%2C23837860%2C23844961%2C11217119%2C23856651%2C11217209%2C23857560%2C23803853%2C23857364%2C11216358%2C23850473%2C11215956&ns=yt&plid=AAWXSMnEZ2aovQXd&adformat=1_8&autoplay=1&delay=10&el=adunit&len=76&mos=1&of=ucLW9a-KDp2D-BGoUuvc6w&uga=m29&vm=CAEQABgEKixxd3dmQ0xIRjJEWVRqYWQ5R2MyTjhqa2g0T29UNGNFNWg2ckt6SkJvVVh3PQ&cpn=ga_RPp8eFfiuxcEJ&ver=2&cplatform=mobile&cbr=com.google.android.youtube&c=android&cmodel=Android%20SDK%20built%20for%20x86&cos=Android&csdk=28&cbrver=14.45.52&cver=14.45.52&cosver=9&cbrand=Google&cplayer=ANDROID_EXOPLAYER&rt=0.4&lact=3101&fmt=135&afmt=140&cmt=0.0&conn=3&vis=5&uao=0&muted=1&volume=33&rtn=10.0&docid="
        val yTContentId = "(scrubbed)"
        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateLteEntity =
            BaseEchoLocateLteEntity(
                1,
                LteDataStatus.STATUS_RAW,
                EchoLocateDateUtils.getTriggerTimeStamp(),
                "1",
                "2.0",
                sessionId
            )
        lteDao.insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity)
        val sourceList = mutableListOf<String>()
        sourceList.add(0, "1")
        sourceList.add(1, "1")
        sourceList.add(2, "4")
        sourceList.add(3, "4")
        sourceList.add(4, "4")
        sourceList.add(5, "1")
        sourceList.add(6, "1")
        sourceList.add(7, "1")

        CommonRFConfigurationProcessor(context).execute(
            LteMetricsData(
                sourceList,
                baseEchoLocateLteEntity.triggerTimestamp,
                LteBaseDataMetricsWrapper.ApiVersion.VERSION_1,
                sessionId, yTLink, yTContentId
            )
        )
        latch.await(1, TimeUnit.SECONDS)
        val commonRFConfigurationEntityList = lteDao.getCommonRFConfigurationEntity(sessionId)
        assert(commonRFConfigurationEntityList?.sessionId == sessionId)
    }

    /**
     * Test case for the RF configuration entity
     */
    @Test
    fun testInsertRFConfigurationEntityNA() {
        val latch = CountDownLatch(1)
        val yTLink =
            "https://s.youtube.com/api/stats/playback?cl=279650211&ei=YvfMXfmHBIe03LUPloiO6Ac&fexp=1714247%2C38810003%2C23839597%2C23842986%2C23848795%2C23817286%2C23856906%2C38810002%2C23853235%2C23851051%2C23837993%2C23837860%2C23844961%2C11217119%2C23856651%2C11217209%2C23857560%2C23803853%2C23857364%2C11216358%2C23850473%2C11215956&ns=yt&plid=AAWXSMnEZ2aovQXd&adformat=1_8&autoplay=1&delay=10&el=adunit&len=76&mos=1&of=ucLW9a-KDp2D-BGoUuvc6w&uga=m29&vm=CAEQABgEKixxd3dmQ0xIRjJEWVRqYWQ5R2MyTjhqa2g0T29UNGNFNWg2ckt6SkJvVVh3PQ&cpn=ga_RPp8eFfiuxcEJ&ver=2&cplatform=mobile&cbr=com.google.android.youtube&c=android&cmodel=Android%20SDK%20built%20for%20x86&cos=Android&csdk=28&cbrver=14.45.52&cver=14.45.52&cosver=9&cbrand=Google&cplayer=ANDROID_EXOPLAYER&rt=0.4&lact=3101&fmt=135&afmt=140&cmt=0.0&conn=3&vis=5&uao=0&muted=1&volume=33&rtn=10.0&docid="
        val yTContentId = "(scrubbed)"
        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateLteEntity =
            BaseEchoLocateLteEntity(
                1,
                LteDataStatus.STATUS_RAW,
                EchoLocateDateUtils.getTriggerTimeStamp(),
                "1",
                "2.0",
                sessionId
            )
        lteDao.insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity)
        val sourceList = mutableListOf<String>()
        sourceList.add(0, "1")
        sourceList.add(1, "1")
        sourceList.add(2, "4")
        sourceList.add(3, "4")
        sourceList.add(4, "4")
        sourceList.add(5, "1")
        sourceList.add(6, "1")
        sourceList.add(7, "1")
        sourceList.add(8, "4")
        sourceList.add(9, "1")
        sourceList.add(10, "1")
        sourceList.add(11, "1")

        CommonRFConfigurationProcessor(context).execute(
            LteMetricsData(
                sourceList,
                baseEchoLocateLteEntity.triggerTimestamp,
                LteBaseDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION,
                sessionId, yTLink, yTContentId
            )
        )
        latch.await(1, TimeUnit.SECONDS)
        val commonRFConfigurationEntityList = lteDao.getCommonRFConfigurationEntity(sessionId)
        assert(commonRFConfigurationEntityList?.sessionId == sessionId)
    }

    @Test
    fun testInsertSignalConditionEntity() {
        val latch = CountDownLatch(1)
        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateLteEntity =
            BaseEchoLocateLteEntity(
                201,
                LteDataStatus.STATUS_RAW,
                EchoLocateDateUtils.getTriggerTimeStamp(),
                "1",
                "2.0",
                sessionId
            )

        lteDao.insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity)
        val sourceList = mutableListOf<String>()
        sourceList.add(0, "1")
        sourceList.add(1, "66786")
        sourceList.add(2, "66")
        sourceList.add(3, "20")
        sourceList.add(4, "1")
        sourceList.add(5, "66786")

        SignalConditionProcessor(context).execute(
            LteMetricsData(
                sourceList,
                baseEchoLocateLteEntity.triggerTimestamp,
                LteBaseDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION,
                sessionId
            )
        )
        latch.await(1, TimeUnit.SECONDS)
        val signalConditionEntity = lteDao.getSignalConditionEntityList()
        assert(signalConditionEntity.isNotEmpty())
    }

    @Test
    fun testInsertSignalConditionEntityV1() {
        val latch = CountDownLatch(1)
        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateLteEntity =
            BaseEchoLocateLteEntity(
                201,
                LteDataStatus.STATUS_RAW,
                EchoLocateDateUtils.getTriggerTimeStamp(),
                "1",
                "2.0",
                sessionId
            )

        lteDao.insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity)
        val sourceList = mutableListOf<String>()
        sourceList.add(0, "")
        sourceList.add(1, "66786")
        sourceList.add(2, "66")
        sourceList.add(3, "20")
        sourceList.add(4, "1")
        sourceList.add(5, "66786")
        sourceList.add(6, "66")
        sourceList.add(7, "20")
        sourceList.add(8, "1")
        sourceList.add(9, "66786")
        sourceList.add(10, "66")
        sourceList.add(11, "20")
        sourceList.add(12, "1")
        sourceList.add(13, "66786")
        sourceList.add(14, "66")
        sourceList.add(15, "20")

        SignalConditionProcessor(context).execute(
            LteMetricsData(
                sourceList,
                baseEchoLocateLteEntity.triggerTimestamp,
                LteBaseDataMetricsWrapper.ApiVersion.VERSION_1,
                sessionId
            )
        )
        latch.await(1, TimeUnit.SECONDS)
        val signalConditionEntity = lteDao.getSignalConditionEntityList()
        assert(signalConditionEntity.isNotEmpty())
    }


    @Test
    fun testInsertDownLinkCarrierInfoEntity() {
        val latch = CountDownLatch(1)
        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateLteEntity =
            BaseEchoLocateLteEntity(
                201,
                EchoLocateDateUtils.getTriggerTimeStamp(),
                "1",
                "2",
                "1",
                sessionId
            )

        lteDao.insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity)
        val sourceList = mutableListOf<String>()
        sourceList.add(0, "")
        sourceList.add(1, "66786")
        sourceList.add(2, "66")
        sourceList.add(3, "20")
        sourceList.add(4, "1")
        sourceList.add(5, "66786")
        sourceList.add(6, "66")
        sourceList.add(7, "20")
        sourceList.add(8, "2")
        sourceList.add(9, "66786")
        sourceList.add(10, "66")
        sourceList.add(11, "20")

        DownLinkCarrierInfoProcessor(context).execute(
            LteMetricsData(
                sourceList,
                baseEchoLocateLteEntity.triggerTimestamp,
                LteBaseDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION,
                sessionId
            )
        )
        latch.await(1, TimeUnit.SECONDS)
        val downLinkCarrierInfoEntity = lteDao.getDownLinkCarrierInfoEntity()
        assert(downLinkCarrierInfoEntity.isNotEmpty())

        val caEntityList = lteDao.getCAEntity()
        assert(caEntityList.isNotEmpty())
    }

    /**
     * Test case for the Data settings entity
     */
    @Test
    fun testInsertDataSettingsEntity() {
        val latch = CountDownLatch(1)
        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateLteEntity =
            BaseEchoLocateLteEntity(
                1,
                EchoLocateDateUtils.getTriggerTimeStamp(),
                "1",
                "1",
                "1",
                sessionId
            )
        lteDao.insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity)
        val sourceList = mutableListOf<String>()
        sourceList.add(0, "1")
        sourceList.add(1, "1")
        sourceList.add(2, "4")
        sourceList.add(3, "4")
        sourceList.add(4, "4")
        sourceList.add(5, "1")
        sourceList.add(6, "1")
        sourceList.add(7, "1")
        sourceList.add(8, "1")

        DataSettingsProcessor(context).execute(
            LteMetricsData(
                sourceList,
                baseEchoLocateLteEntity.triggerTimestamp,
                LteBaseDataMetricsWrapper.ApiVersion.VERSION_1,
                sessionId, "", ""
            )
        )
        latch.await(1, TimeUnit.SECONDS)
        val lteSettingsEntityList = lteDao.getLteSettingsEntity(sessionId)
        assert(lteSettingsEntityList?.sessionId == sessionId)
    }

    /**
     * Test case for the Data settings entity
     */
    @Test
    fun testInsertDataSettingsEntityNA() {
        val latch = CountDownLatch(1)
        val sessionId = UUID.randomUUID().toString()
        val baseEchoLocateLteEntity =
            BaseEchoLocateLteEntity(
                1,
                EchoLocateDateUtils.getTriggerTimeStamp(),
                "1",
                "1",
                "1",
                sessionId
            )
        lteDao.insertBaseEchoLocateLteEntity(baseEchoLocateLteEntity)
        val sourceList = mutableListOf<String>()
        sourceList.add(0, "1")
        sourceList.add(1, "1")
        sourceList.add(2, "4")
        sourceList.add(3, "4")
        sourceList.add(4, "4")
        sourceList.add(5, "1")
        sourceList.add(6, "1")

        DataSettingsProcessor(context).execute(
            LteMetricsData(
                sourceList,
                baseEchoLocateLteEntity.triggerTimestamp,
                LteBaseDataMetricsWrapper.ApiVersion.UNKNOWN_VERSION,
                sessionId, "", ""
            )
        )
        latch.await(1, TimeUnit.SECONDS)
        val lteSettingsEntityList = lteDao.getLteSettingsEntity(sessionId)
        assert(lteSettingsEntityList?.sessionId == sessionId)
    }
}