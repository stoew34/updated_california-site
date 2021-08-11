package com.tmobile.mytmobile.echolocate.dsdkHandshake.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tmobile.mytmobile.echolocate.dsdkHandshake.database.dao.DsdkHandshakeDao
import com.tmobile.mytmobile.echolocate.dsdkHandshake.database.databasemodel.DsdkHandshakeParametersModel
import com.tmobile.mytmobile.echolocate.dsdkHandshake.utils.DsdkHandshakeUtils

/**
 * Entities used in the database
 */
@Database(entities = [DsdkHandshakeParametersModel::class], version = 1,  exportSchema = false)
/**
 * Room database class to create/initialize echolocate user consent database
 */
abstract class EcholocateDsdkHandshakeDatabase : RoomDatabase() {


    /**
     * UserConsentDao used in the database
     */
    abstract fun dsdkHandshakeDao(): DsdkHandshakeDao

    companion object {
        /**
         * Database instance
         */
        @Volatile
        private var INSTANCE: EcholocateDsdkHandshakeDatabase? = null

        /***
         * creates database instance
         */
        fun getDatabase(context: Context): EcholocateDsdkHandshakeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    EcholocateDsdkHandshakeDatabase::class.java,
                    DsdkHandshakeUtils.ECHOLOCATE_DSDK_HANDSHAKE_DB_NAME
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }

        /**
         * deletes the created database instance
         */
        fun destroyDataBase() {
            INSTANCE = null
        }
    }
}
