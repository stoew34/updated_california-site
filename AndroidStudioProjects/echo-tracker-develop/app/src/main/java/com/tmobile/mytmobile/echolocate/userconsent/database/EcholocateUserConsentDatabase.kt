package com.tmobile.mytmobile.echolocate.userconsent.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tmobile.mytmobile.echolocate.userconsent.database.dao.UserConsentDao
import com.tmobile.mytmobile.echolocate.userconsent.database.databasemodel.UserConsentResponseModel
import com.tmobile.mytmobile.echolocate.userconsent.utils.UserConsentStringUtils


/**
 * Entities used in the database
 */
@Database(entities = [UserConsentResponseModel::class], version = 2,  exportSchema = false)
/**
 * Room database class to create/initialize echolocate user consent database
 */
abstract class EcholocateUserConsentDatabase : RoomDatabase() {

    /**
     * UserConsentDao used in the database
     */
    abstract fun userConsentDao(): UserConsentDao

    companion object {
        /**
         * Database instance
         */
        @Volatile
        private var INSTANCE: EcholocateUserConsentDatabase? = null

        /***
         * creates database instance
         */
        fun getDatabase(context: Context): EcholocateUserConsentDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                        context,
                        EcholocateUserConsentDatabase::class.java,
                        UserConsentStringUtils.ECHOLOCATE_USER_CONSENT_DB_NAME
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