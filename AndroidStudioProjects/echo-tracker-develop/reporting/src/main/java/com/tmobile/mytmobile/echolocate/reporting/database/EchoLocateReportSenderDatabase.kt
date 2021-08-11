package com.tmobile.mytmobile.echolocate.reporting.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tmobile.mytmobile.echolocate.reporting.database.dao.ReportSenderDao
import com.tmobile.mytmobile.echolocate.reporting.database.entities.ReportSenderEntity

/**
 * Entities used in the database
 */
@Database(entities = [ReportSenderEntity::class], version = 2, exportSchema = false)
/**
 * Room database class to create/initialize echoLocate ReportSender database
 */
abstract class EchoLocateReportSenderDatabase : RoomDatabase() {
    /**
     * defines [ReportSenderDao] DAO as an abstract function to enable access from the repository
     */
    abstract fun reportSenderDao(): ReportSenderDao

    companion object {

        /**
         * Initializes EchoLocateReportSenderDatabase instance
         */
        @Volatile
        private var INSTANCE: EchoLocateReportSenderDatabase? = null

        /***
         * creates EchoLocateReportSenderDatabase instance
         */
        fun getEchoLocateReportSenderDatabase(context: Context): EchoLocateReportSenderDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EchoLocateReportSenderDatabase::class.java,
                    ReportSenderDatabaseConstants.REPORT_SENDER_DATABASE_NAME
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }


}