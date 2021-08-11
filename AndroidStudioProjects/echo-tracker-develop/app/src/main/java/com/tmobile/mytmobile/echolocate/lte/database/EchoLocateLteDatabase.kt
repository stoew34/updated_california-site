package com.tmobile.mytmobile.echolocate.lte.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tmobile.mytmobile.echolocate.lte.database.dao.LteDao
import com.tmobile.mytmobile.echolocate.lte.database.entity.*

/**
 * defines the entities used in the voice database
 */
@Database(
    entities = [BaseEchoLocateLteEntity::class, BearerEntity::class,
        BearerConfigurationEntity::class, CAEntity::class, CommonRFConfigurationEntity::class,
        DownLinkCarrierInfoEntity::class, DownlinkRFConfigurationEntity::class,
        LteLocationEntity::class, LteOEMSVEntity::class, LteSettingsEntity::class,
        NetworkIdentityEntity::class, SecondCarrierEntity::class,
        SignalConditionEntity::class, ThirdCarrierEntity::class, UplinkCarrierInfoEntity::class,
        UpLinkRFConfigurationEntity::class, LteSingleSessionReportEntity::class], version = 2, exportSchema = false
)

/**
 * Room database class to create/initialize echoLocate Voice database
 */
abstract class EchoLocateLteDatabase : RoomDatabase() {


    /**
     * defines the DAO as an abstract funtion to enable access from the repository
     */
    abstract fun lteDao(): LteDao

    companion object {
        /**
         * Initializes echoLocateVoice Database instance
         */
        @Volatile
        private var INSTANCE: EchoLocateLteDatabase? = null

        /***
         * creates database instance
         */
        fun getEchoLocateLteDatabase(context: Context): EchoLocateLteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EchoLocateLteDatabase::class.java,
                    LteDatabaseConstants.ECHO_LOCATE_LTE_DB_NAME
                ).addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .build()

                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE ${LteDatabaseConstants.LTE_REPORT_TABLE_NAME}" +
                        " ADD COLUMN reportStatus TEXT DEFAULT ''")
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