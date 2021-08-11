package com.tmobile.mytmobile.echolocate.dsdkHandshake.database.repository

import android.content.Context
import android.database.Cursor
import com.tmobile.mytmobile.echolocate.dsdkHandshake.database.EcholocateDsdkHandshakeDatabase
import com.tmobile.mytmobile.echolocate.dsdkHandshake.database.dao.DsdkHandshakeDao
import com.tmobile.mytmobile.echolocate.dsdkHandshake.database.databasemodel.DsdkHandshakeParametersModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class DsdkHandshakeRepository private constructor(context: Context) {
    companion object {
        /**
         * DsdkHandshakeRepository instance
         */
        @Volatile
        private var INSTANCE: DsdkHandshakeRepository? = null

        /***
         * creates DsdkHandshakeRepository instance
         */
        fun getInstance(context: Context): DsdkHandshakeRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = DsdkHandshakeRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * class DsdkHandshakeRepository:
     *  instance of this class and it's methods will be used for db Create Read Update Destroy operations.
     *  Each method of the class has a direct correlation to db command Room library mappings in
     *  DsdkHandshakeDao.kt
     *
     *  The coroutines was creating a thread for each insert and delete. So each thread was working on their own.
     *  That means each insert and delete was happening without waiting for the first insert to complete.
     *  with async{}.await() we have made sure that it waits for the previous insertion to complete before executing the next one
     **/
    private var dsdkHandshakeDao: DsdkHandshakeDao = EcholocateDsdkHandshakeDatabase.getDatabase(context).dsdkHandshakeDao()

    /**
     *  fun insertDsdkHandshakeResponse
     *  method for insertion of table rows/records to db
     * @param @see DsdkHandshakeResponseEvent
     */
    fun insertDsdkHandshakeParameterToDB(dsdkHandshakeParametersModel: DsdkHandshakeParametersModel) {
        runBlocking(Dispatchers.Default) {
            return@runBlocking async {
                dsdkHandshakeDao.insertDsdkHandshakeFlags(dsdkHandshakeParametersModel)
            }.await()
        }
    }

    /**
     *  fun getDsdkHandshakeEvent :
     *   gets the latest flags for dsdk handshake
     *
     */
    fun getDsdkHandshakeEvent(): Cursor? {
        return runBlocking(Dispatchers.Default) {
            return@runBlocking async {
                dsdkHandshakeDao.getDsdkHandshakeFlags()
            }.await()
        }
    }

}