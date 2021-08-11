package com.tmobile.mytmobile.echolocate.userconsent

import android.content.Intent
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.test.ProviderTestCase2
import android.test.mock.MockContentProvider
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.tmobile.mytmobile.echolocate.userconsent.consentreader.DiagnosticConsentChangedReceiver
import com.tmobile.mytmobile.echolocate.userconsent.database.EcholocateUserConsentDatabase
import com.tmobile.mytmobile.echolocate.userconsent.database.databasemodel.UserConsentFlagsParametersModel
import com.tmobile.mytmobile.echolocate.userconsent.database.databasemodel.UserConsentResponseModel
import com.tmobile.mytmobile.echolocate.userconsent.model.UserConsentUpdateParameters
import com.tmobile.mytmobile.echolocate.userconsent.userconsentevents.UserConsentFlagsParameters
import com.tmobile.mytmobile.echolocate.userconsent.userconsentevents.UserConsentResponseEvent
import com.tmobile.mytmobile.echolocate.userconsent.utils.UserConsentStringUtils
import com.tmobile.mytmobile.echolocate.userconsent.utils.UserConsentStringUtils.AUTHORITY
import com.tmobile.mytmobile.echolocate.userconsent.utils.UserConsentStringUtils.COLUMN1
import com.tmobile.mytmobile.echolocate.userconsent.utils.UserConsentStringUtils.COLUMN2
import com.tmobile.mytmobile.echolocate.userconsent.utils.UserConsentStringUtils.COLUMN3
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.junit.Assert
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


class UserConsentTest
    : ProviderTestCase2<UserConsentTest.OneQueryMockContentProvider>(
        OneQueryMockContentProvider::class.java,
        AUTHORITY
) {
    private lateinit var db: EcholocateUserConsentDatabase
    private lateinit var consentManager: ConsentManager
    private lateinit var consentRequestProvider: ConsentRequestProvider

    override fun setUp() {
        super.setUp()
        consentManager = ConsentManager.getInstance(this.mockContext)
        consentRequestProvider = ConsentRequestProvider.getInstance(this.mockContext)
        val context = InstrumentationRegistry.getInstrumentation().targetContext.applicationContext
        db = Room.inMemoryDatabaseBuilder(context, EcholocateUserConsentDatabase::class.java)
                .build()
        consentManager.setUserConsentDaoForTesting(db.userConsentDao())

    }

    override fun tearDown() {
        super.tearDown()

        // TODO Need to find a way to make tests run sequentially. If not, any db transaction attempt after db close will throw IllegalStateException
        //db.close()
        consentManager.resetInstance()
        consentManager.unregisterConsentUpdate()
        consentRequestProvider.resetInstance()
    }

    @Test
    fun testBroadcast() {

        val latch = CountDownLatch(1)
        val intent = Intent()

        intent.putExtra(UserConsentStringUtils.EXTRA_SOURCE, "New Source")
        intent.putExtra(UserConsentStringUtils.EXTRA_IS_CONSENTED, true)
        intent.putExtra(UserConsentStringUtils.EXTRA_NAME, "Device Data Collection")

        val diagnosticConsentChangedReceiver = DiagnosticConsentChangedReceiver()

        var isAllowedDeviceDataCollection = false
        var isAllowedIssueAssist = false
        var isAllowedPersonalizedOffers = false

        consentRequestProvider.getUserConsentUpdates().subscribe {
            isAllowedDeviceDataCollection =
                    it.userConsentFlagsParameters.isAllowedDeviceDataCollection
            isAllowedIssueAssist = it.userConsentFlagsParameters.isAllowedIssueAssist
            isAllowedPersonalizedOffers = it.userConsentFlagsParameters.isAllowedPersonalizedOffers
        }
        diagnosticConsentChangedReceiver.processReceiverData(intent)

        latch.await(1, TimeUnit.SECONDS)
        Assert.assertEquals(false, isAllowedDeviceDataCollection)
        Assert.assertEquals(false, isAllowedIssueAssist)
        Assert.assertEquals(false, isAllowedPersonalizedOffers)


    }

    @Test
    fun testResolver() {


        //Prepare the sample columns for the content provider
        val exampleProjection = arrayOf(COLUMN1, COLUMN2, COLUMN3)
        val matrixCursor = MatrixCursor(exampleProjection)


        //prepare the sample data
        val userConsentResponseEvent = UserConsentResponseEvent(
                userConsentFlagsParameters = UserConsentFlagsParameters(
                        false,
                        true,
                        true
                )
        )
        val exampleData = arrayOf(
                userConsentResponseEvent.userConsentFlagsParameters.isAllowedDeviceDataCollection,
                userConsentResponseEvent.userConsentFlagsParameters.isAllowedIssueAssist,
                userConsentResponseEvent.userConsentFlagsParameters.isAllowedPersonalizedOffers
        )

        //add the sample data to the provider
        matrixCursor.addRow(exampleData)
        this.provider.addQueryResult(matrixCursor)

        val userConsentResponseEventFromDB = consentRequestProvider.getUserConsentFlags()

        Assert.assertNotNull(userConsentResponseEventFromDB)
        Assert.assertNotNull(userConsentResponseEventFromDB?.userConsentFlagsParameters)

        Assert.assertEquals(
                userConsentResponseEvent.userConsentFlagsParameters.isAllowedPersonalizedOffers,
                userConsentResponseEventFromDB?.userConsentFlagsParameters?.isAllowedPersonalizedOffers
        )
        Assert.assertEquals(
                userConsentResponseEvent.userConsentFlagsParameters.isAllowedIssueAssist,
                userConsentResponseEventFromDB?.userConsentFlagsParameters?.isAllowedIssueAssist
        )
        Assert.assertEquals(
                userConsentResponseEvent.userConsentFlagsParameters.isAllowedDeviceDataCollection,
                userConsentResponseEventFromDB?.userConsentFlagsParameters?.isAllowedDeviceDataCollection
        )


    }


    /**
     * This class is used to mock the provider
     */
    class OneQueryMockContentProvider : MockContentProvider() {
        private var queryResult: Cursor? = null

        // add the mock result
        fun addQueryResult(expectedResult: Cursor) {
            this.queryResult = expectedResult
        }

        //  returns the mock result when query is invoked on the resolver instance
        override fun query(
            uri: Uri,
            projection: Array<out String>?,
            selection: String?,
            selectionArgs: Array<out String>?,
            sortOrder: String?
        ): Cursor? {
            return this.queryResult
        }
    }

    @Test
    fun testUserConsentUpdateParameters() {
        val userConsentUpdateParameters1 = UserConsentUpdateParameters("source", true, "name")
        val userConsentUpdateParameters2 = userConsentUpdateParameters1.copy()
        val userConsentUpdateParameters3 = UserConsentUpdateParameters(
                userConsentUpdateParameters2.source,
                userConsentUpdateParameters2.isConsented,
                userConsentUpdateParameters2.name
        )

        val json = Json(JsonConfiguration.Stable)
        val jsonString1 =
                json.stringify(UserConsentUpdateParameters.serializer(), userConsentUpdateParameters1)
        val jsonString2 =
                json.stringify(UserConsentUpdateParameters.serializer(), userConsentUpdateParameters2)

        val obj1 = json.parse(UserConsentUpdateParameters.serializer(), jsonString1)

        assert(jsonString1 == jsonString2)
        assert(userConsentUpdateParameters1.source == obj1.source)
        assert(userConsentUpdateParameters1.isConsented == obj1.isConsented)
        assert(userConsentUpdateParameters1.name == obj1.name)
        assert(userConsentUpdateParameters1 === obj1)
        assert(userConsentUpdateParameters1.equals(userConsentUpdateParameters2))
        assert(userConsentUpdateParameters1.hashCode() == userConsentUpdateParameters2.hashCode())
        assert(userConsentUpdateParameters1.toString() == userConsentUpdateParameters2.toString())
        assert(userConsentUpdateParameters2.equals(userConsentUpdateParameters3))
    }

    @Test
    fun testUserConsentFlagsParameters() {
        val userConsentFlagsParametersModel1 = UserConsentFlagsParametersModel(true, true, true)
        val userConsentFlagsParametersModel2 = userConsentFlagsParametersModel1.copy()


        val json = Json(JsonConfiguration.Stable)
        val jsonString1 =
                json.stringify(UserConsentFlagsParametersModel.serializer(), userConsentFlagsParametersModel1)
        val jsonString2 =
                json.stringify(UserConsentFlagsParametersModel.serializer(), userConsentFlagsParametersModel2)

        val obj1 = json.parse(UserConsentFlagsParametersModel.serializer(), jsonString1)

        assert(jsonString1 == jsonString2)
        assert(userConsentFlagsParametersModel1.isAllowedDeviceDataCollection == obj1.isAllowedDeviceDataCollection)
        assert(userConsentFlagsParametersModel1.isAllowedIssueAssist == obj1.isAllowedIssueAssist)
        assert(userConsentFlagsParametersModel1.isAllowedPersonalizedOffers == obj1.isAllowedPersonalizedOffers)
        assert(userConsentFlagsParametersModel1 === obj1)
        assert(userConsentFlagsParametersModel1.equals(userConsentFlagsParametersModel2))
        assert(userConsentFlagsParametersModel1.hashCode() == userConsentFlagsParametersModel2.hashCode())
        assert(userConsentFlagsParametersModel1.toString() == userConsentFlagsParametersModel2.toString())
    }

    @Test
    fun testUserConsentResponseModel() {
        val userConsentFlagsParametersModel1 = UserConsentFlagsParametersModel(true, true, true)
        val userConsentResponseModel1 = UserConsentResponseModel(1, userConsentFlagsParametersModel1)
        val userConsentResponseModel2 = userConsentResponseModel1.copy()

        assert(userConsentResponseModel1.equals(userConsentResponseModel2))
        assert(userConsentResponseModel1.hashCode() == userConsentResponseModel2.hashCode())
        assert(userConsentResponseModel1.toString() == userConsentResponseModel2.toString())

    }
}