package com.tmobile.mytmobile.echolocate.userconsent

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.userconsent.database.EcholocateUserConsentDatabase
import com.tmobile.mytmobile.echolocate.userconsent.database.repository.UserConsentRepository
import com.tmobile.mytmobile.echolocate.userconsent.userconsentevents.UserConsentFlagsParameters
import com.tmobile.mytmobile.echolocate.userconsent.userconsentevents.UserConsentResponseEvent
import org.junit.*
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class UserConsentDataFlowTests {
    lateinit var db: EcholocateUserConsentDatabase
    lateinit var userConsentRepository: UserConsentRepository

    companion object {

        @get:Rule
        val consentManager: ConsentManager =
                ConsentManager.getInstance(InstrumentationRegistry.getInstrumentation().targetContext)
        @get:Rule
        val consentRequestProvider: ConsentRequestProvider =
                ConsentRequestProvider.getInstance(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        db = Room.inMemoryDatabaseBuilder(context, EcholocateUserConsentDatabase::class.java)
                .build()
        val userConsentDao = db.userConsentDao()
        consentManager.setUserConsentDaoForTesting(userConsentDao)
        userConsentRepository = UserConsentRepository.getInstance(context)
        userConsentRepository.setUserConsentDaoForTesting(userConsentDao)
    }

    @After
    fun tearDown() {
//        db.close()
    }

    /**
     * Testing dataflow from broadcast receiver DiagnosticConsentChangedReceiver to ConsentRequestProvider
     */
//    @Test
//    fun testBroadcast() {
//        val latch = CountDownLatch(1)
//        val intent = Intent()
//
//        intent.putExtra(UserConsentStringUtils.EXTRA_SOURCE, "New Source")
//        intent.putExtra(UserConsentStringUtils.EXTRA_IS_CONSENTED, true)
//        intent.putExtra(UserConsentStringUtils.EXTRA_NAME, "Device Data Collection")
//
//        val diagnosticConsentChangedReceiver = DiagnosticConsentChangedReceiver()
//
//        var isAllowedDeviceDataCollection = false
//        var isAllowedIssueAssist = false
//        var isAllowedPersonalizedOffers = false
//
//        consentRequestProvider.getUserConsentUpdates().subscribe {
//            isAllowedDeviceDataCollection =
//                    it.userConsentFlagsParameters.isAllowedDeviceDataCollection
//            isAllowedIssueAssist = it.userConsentFlagsParameters.isAllowedIssueAssist
//            isAllowedPersonalizedOffers = it.userConsentFlagsParameters.isAllowedPersonalizedOffers
//            latch.countDown()
//        }
//        diagnosticConsentChangedReceiver.processReceiverData(intent)
//        latch.await(2, TimeUnit.SECONDS)
//        Assert.assertEquals(true, isAllowedDeviceDataCollection)
//    }

    /**
     * Testing dataflow from DiagnosticFlagsResolver to ConsentRequestProvider
     */
    @Test
    fun testResolver() {
        val userConsentResponseEvent = UserConsentResponseEvent(
                userConsentFlagsParameters =
                UserConsentFlagsParameters(
                        false,
                        true,
                        true
                )
        )
        userConsentResponseEvent.timeStamp = Calendar.getInstance().timeInMillis
        userConsentResponseEvent.sourceComponent = "DiagnosticFlagsResolver"
        val userConsetResponseEventFromDB = consentManager.getConsentFlags(userConsentResponseEvent)

        Assert.assertNotNull(userConsetResponseEventFromDB)
        Assert.assertNotNull(userConsetResponseEventFromDB?.userConsentFlagsParameters)

        Assert.assertEquals(
                userConsentResponseEvent.userConsentFlagsParameters.isAllowedPersonalizedOffers,
                userConsetResponseEventFromDB?.userConsentFlagsParameters?.isAllowedPersonalizedOffers
        )
        Assert.assertEquals(
                userConsentResponseEvent.userConsentFlagsParameters.isAllowedIssueAssist,
                userConsetResponseEventFromDB?.userConsentFlagsParameters?.isAllowedIssueAssist
        )
        Assert.assertEquals(
                userConsentResponseEvent.userConsentFlagsParameters.isAllowedDeviceDataCollection,
                userConsetResponseEventFromDB?.userConsentFlagsParameters?.isAllowedDeviceDataCollection
        )

    }

    @Test
    fun testInsertAndDelete() {
        val userConsentFlagsParameters = UserConsentFlagsParameters(true, false, true)
        val userConsentResponseEvent = UserConsentResponseEvent(1, userConsentFlagsParameters)
        userConsentResponseEvent.sourceComponent = "sourceComponent"
        userConsentResponseEvent.timeStamp = Calendar.getInstance().timeInMillis
        userConsentRepository.insertUserConsentResponse(userConsentResponseEvent)
        val latestResponse = userConsentRepository.getLatestUserConsentResponse()
        val oldestResponse = userConsentRepository.getOldestUserConsentResponse()
        assert(userConsentResponseEvent.sourceComponent == latestResponse?.sourceComponent)
        assert(latestResponse == oldestResponse)
        val count = userConsentRepository.getUserConsentResponseCount()
        assert(count == 1)

        userConsentRepository.deleteUserConsentResponse(latestResponse!!)
        assert(count == 0)
        userConsentRepository.insertUserConsentResponse(userConsentResponseEvent)
        userConsentRepository.deleteAllUserConsentResponse()
        assert(count == 0)
    }
}