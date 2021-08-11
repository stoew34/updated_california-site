package com.tmobile.mytmobile.echolocate.coverage.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tmobile.mytmobile.echolocate.coverage.database.dao.CoverageDao
import com.tmobile.mytmobile.echolocate.coverage.database.entity.*

/**
 * Defines the entities used in the coverage database
 */
@Database(
    entities = [
        BaseEchoLocateCoverageEntity::class,
        CoverageCellIdentityEntity::class,
        CoverageCellSignalStrengthEntity::class,
        CoverageSettingsEntity::class,
        CoverageTelephonyEntity::class,
        CoverageConnectedWifiStatusEntity::class,
        CoverageSingleSessionReportEntity::class,
        CoverageLocationEntity::class,
        CoverageNetEntity::class,
        CoverageNrCellEntity::class,
        CoveragePrimaryCellEntity::class,
        CoverageOEMSVEntity::class],
    version = 2,
    exportSchema = false
)
abstract class EchoLocateCoverageDatabase : RoomDatabase() {

    /**
     * defines the DAO as an abstract function to enable access from the repository
     */
    abstract fun coverageDao(): CoverageDao

    companion object {

        /**
         * Initializes Coverage Database instance
         */
        @Volatile
        private var INSTANCE: EchoLocateCoverageDatabase? = null

        /***
         * creates database instance
         */

        fun getEchoLocateCoverageDatabase(context: Context): EchoLocateCoverageDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EchoLocateCoverageDatabase::class.java,
                    CoverageDatabaseConstants.ECHO_LOCATE_COVERAGE_DB_NAME
                ).addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .build()

                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE ${CoverageDatabaseConstants.COVERAGE_REPORT_TABLE_NAME}" +
                        " ADD COLUMN reportStatus TEXT DEFAULT ''")
            }
        }
    }
}