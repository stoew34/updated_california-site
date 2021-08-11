package com.tmobile.mytmobile.echolocate.analytics.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tmobile.mytmobile.echolocate.analytics.database.dao.AnalyticsDao
import com.tmobile.mytmobile.echolocate.analytics.database.entity.AnalyticsEventEntity

/**
 * defines the entities used in the Analytics database
 */
@Database(
    entities = [
        AnalyticsEventEntity::class
    ],
    version = 1,
    exportSchema = false
)

/**
 * Room database class to create/initialize echoLocate Analytics database
 */
abstract class EchoLocateAnalyticsDatabase: RoomDatabase() {

    abstract fun analyticsDao(): AnalyticsDao

    companion object {
        /**
         * Initializes echoLocateVoice Database instance
         */
        @Volatile
        private var INSTANCE: EchoLocateAnalyticsDatabase? = null

        /***
         * creates database instance
         */
        fun getEchoLocateAnalyticsDatabase(context: Context): EchoLocateAnalyticsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EchoLocateAnalyticsDatabase::class.java,
                    AnalyticsDatabaseConstants.ECHO_LOCATE_ANALYTICS_DB_NAME
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