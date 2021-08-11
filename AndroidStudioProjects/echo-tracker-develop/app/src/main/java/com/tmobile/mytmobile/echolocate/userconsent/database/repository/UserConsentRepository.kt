package com.tmobile.mytmobile.echolocate.userconsent.database.repository

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.tmobile.mytmobile.echolocate.userconsent.database.EcholocateUserConsentDatabase
import com.tmobile.mytmobile.echolocate.userconsent.database.dao.UserConsentDao
import com.tmobile.mytmobile.echolocate.userconsent.database.databasemodel.UserConsentResponseModel
import com.tmobile.mytmobile.echolocate.userconsent.userconsentevents.UserConsentResponseEvent
import io.reactivex.Flowable
import kotlinx.coroutines.*

class UserConsentRepository private constructor(context: Context) {

    companion object {
        /**
         * UserConsentRepository instance
         */
        @Volatile
        private var INSTANCE: UserConsentRepository? = null

        /***
         * creates UserConsentRepository instance
         */
        fun getInstance(context: Context): UserConsentRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = UserConsentRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * class UserConsentRepository:
     *  instance of this class and it's methods will be used for db Create Read Update Destroy operations.
     *  Each method of the class has a direct correlation to db command Room library mappings in
     *  UserConsentDao.kt
     *
     *  The coroutines was creating a thread for each insert and delete. So each thread was working on their own.
     *  That means each insert and delete was happening without waiting for the first insert to complete.
     *  with async{}.await() we have made sure that it waits for the previous insertion to complete before executing the next one
     **/
    private var userConsentDao: UserConsentDao = EcholocateUserConsentDatabase.getDatabase(context).userConsentDao()

    /**
     * For Unit test only @see [com.tmobile.mytmobile.echolocate.userconsent.UserConsentDataFlowTests]
     */
    @VisibleForTesting
    fun setUserConsentDaoForTesting(userConsentDao: UserConsentDao) {
        this.userConsentDao = userConsentDao
    }

    /**
     *  fun insertUserConsentResponse
     *  method for insertion of table rows/records to db
     * @param @see UserConsentResponseEvent
     */
    fun insertUserConsentResponse(event: UserConsentResponseEvent) = runBlocking(Dispatchers.Default) {
        val result = async {
            val userConsentResponseModel =
                    UserConsentResponseModel(event)
            userConsentDao.insertUserConsentResponseEvent(userConsentResponseModel)
        }.await()
        return@runBlocking result
    }

    /**
     *  fun getLatestUserConsentResponse :
     *   gets must current table member based on row/record's timestamp
     *
     */
    fun getLatestUserConsentResponse() = runBlocking(Dispatchers.Default) {
        val result = async {
            val userConsentResponseModel = userConsentDao.getLatestUserConsent()
            if (userConsentResponseModel != null)
                UserConsentResponseEvent(userConsentResponseModel)
            else null
        }.await()

        return@runBlocking result
    }

    /**
     * Get oldest recod base on timestamp
     */
    fun getOldestUserConsentResponse() = runBlocking(Dispatchers.Default) {
        val result = async {
            val userConsentResponseModel = userConsentDao.getLatestUserConsent()
            if (userConsentResponseModel != null)
            UserConsentResponseEvent(userConsentResponseModel)
            else null
        }.await()
        return@runBlocking result
    }

    /**
     *  fun getUserConsentResponseHistory
     *
     *  @return Flowable<List<UserConsentResponseEvent>>
     *
     */
    fun getUserConsentResponseHistory(): Flowable<List<UserConsentResponseModel>> {
        return userConsentDao.getUserConsentHistory()
    }

    /**
     *  fun deleteUserConsentResponse
     *
     *  deletes a row/record from db table
     *
     *  @param event: UserConsentResponseEvent
     */
    fun deleteUserConsentResponse(event: UserConsentResponseEvent) {
        runBlocking {
            GlobalScope.launch(Dispatchers.IO) {
                val userConsentResponseModel =
                        UserConsentResponseModel(event)
                userConsentDao.delete(userConsentResponseModel)
            }
        }
    }

    /**
     *   fun deleteAllUserConsentResponse
     *
     *   deletes all row/record(s) from db table
     *
     *   @param event: UserConsentResponseEvent
     */
    fun deleteAllUserConsentResponse() {
        runBlocking {
            GlobalScope.launch(Dispatchers.IO) {
                userConsentDao.deleteAll()
            }
        }
    }

    /**
     * fun getUserConsentResponseCount
     *
     *  returns number of row/record(s) in db table
     *  (maximum number of records in db is 10)
     *
     *  @return count
     */
    fun getUserConsentResponseCount() = runBlocking(Dispatchers.Default) {
        val result = async { userConsentDao.getCount() }.await()
        return@runBlocking result
    }
}