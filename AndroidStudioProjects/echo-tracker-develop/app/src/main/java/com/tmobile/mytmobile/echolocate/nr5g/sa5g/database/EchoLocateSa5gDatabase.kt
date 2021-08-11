package com.tmobile.mytmobile.echolocate.nr5g.sa5g.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.dao.Sa5gDao
import com.tmobile.mytmobile.echolocate.nr5g.sa5g.database.entity.*

/**
 * defines the entities used in the SA 5G database
 */
@Database(
    entities = [
        BaseEchoLocateSa5gEntity::class,
        Sa5gActiveNetworkEntity::class,
        Sa5gCarrierConfigEntity::class,
        Sa5gConnectedWifiStatusEntity::class,
        Sa5gDeviceInfoEntity::class,
        Sa5gDownlinkCarrierLogsEntity::class,
        Sa5gLocationEntity::class,
        Sa5gNetworkLogEntity::class,
        Sa5gOEMSVEntity::class,
        Sa5gRrcLogEntity::class,
        Sa5gSettingsLogEntity::class,
        Sa5gSingleSessionReportEntity::class,
        Sa5gTriggerEntity::class,
        Sa5gUiLogEntity::class,
        Sa5gUplinkCarrierLogsEntity::class,
        Sa5gWiFiStateEntity::class],
    version = 3,
    exportSchema = false
)
abstract class EchoLocateSa5gDatabase : RoomDatabase() {

    /*** Defines the DAO as an abstract function to enable access from the repository*/
    abstract fun sa5gDao(): Sa5gDao

    companion object {
        /*** Initializes EchoLocate SA 5G Database instance */
        @Volatile
        private var INSTANCE: EchoLocateSa5gDatabase? = null

        /**** Creates database instance */
        fun getEchoLocateSa5gDatabase(context: Context): EchoLocateSa5gDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EchoLocateSa5gDatabase::class.java,
                    Sa5gDatabaseConstants.ECHO_LOCATE_SA5G_DB_NAME
                ).addMigrations(MIGRATION_2_3)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE ${Sa5gDatabaseConstants.SA5G_REPORT_TABLE_NAME}" +
                        " ADD COLUMN reportStatus TEXT DEFAULT ''")
            }
        }
    }
}