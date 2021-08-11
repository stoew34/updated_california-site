package com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.dao.Nr5gDao
import com.tmobile.mytmobile.echolocate.nr5g.nsa5g.database.entity.*

/**
 * defines the entities used in the 5G database
 */
@Database(
    entities = [
        BaseEchoLocateNr5gEntity::class,
        ConnectedWifiStatusEntity::class,
        EndcLteLogEntity::class,
        EndcUplinkLogEntity::class,
        Nr5gUiLogEntity::class,
        Nr5gActiveNetworkEntity::class,
        Nr5gDataNetworkTypeEntity::class,
        Nr5gNetworkIdentityEntity::class,
        Nr5gStatusEntity::class,
        Nr5gWifiStateEntity::class,
        Nr5gDeviceInfoEntity::class,
        Nr5gLocationEntity::class,
        Nr5gMmwCellLogEntity::class,
        Nr5gOEMSVEntity::class,
        Nr5gTriggerEntity::class,
        Nr5gSingleSessionReportEntity::class],
    version = 3,
    exportSchema = false
)
abstract class EchoLocateNr5gDatabase : RoomDatabase() {
    /**
     * defines the DAO as an abstract funtion to enable access from the repository
     */
    abstract fun nr5gDao(): Nr5gDao

    companion object {

        /**
         * Initializes echoLocateNr5G Database instance
         */
        @Volatile
        private var INSTANCE: EchoLocateNr5gDatabase? = null

        /***
         * creates database instance
         */

        fun getEchoLocateNr5gDatabase(context: Context): EchoLocateNr5gDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EchoLocateNr5gDatabase::class.java,
                    Nr5gDatabaseConstants.ECHO_LOCATE_NR5G_DB_NAME
                ).addMigrations(MIGRATION_2_3)
                .fallbackToDestructiveMigration()
                .build()

                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE ${Nr5gDatabaseConstants.NR5G_REPORT_TABLE_NAME}" +
                        " ADD COLUMN reportStatus TEXT DEFAULT ''")
            }
        }
    }
}