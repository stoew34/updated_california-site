package com.tmobile.mytmobile.echolocate.voice.repository.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tmobile.mytmobile.echolocate.voice.repository.database.dao.VoiceDao
import com.tmobile.mytmobile.echolocate.voice.repository.database.entity.*

/**
 * Defines the entities used in the voice database
 */
@Database(
    entities = [
        VoiceLocationEntity::class,
        OEMSoftwareVersionEntity::class,
        CellInfoEntity::class,
        AppTriggeredCallDataEntity::class,
        RadioHandoverEntity::class,
        ImsSignallingEntity::class,
        RtpdlStateEntity::class,
        UiCallStateEntity::class,
        DeviceInfoEntity::class,
        CallSettingDataEntity::class,
        DetailedCallStateEntity::class,
        BaseEchoLocateVoiceEntity::class,
        VoiceReportEntity::class,
        EmergencyCallTimerStateEntity::class,
        CarrierConfigDataEntity::class
    ], version = 4, exportSchema = false
)

/**
 * Room database class to create/initialize echoLocate Voice database
 */
abstract class EchoLocateVoiceDatabase : RoomDatabase() {


    /**
     * defines the DAO as an abstract funtion to enable access from the repository
     */
    abstract fun voiceDao(): VoiceDao

    companion object {

        /**
         * Initializes echoLocateVoice Database instance
         */
        @Volatile
        private var INSTANCE: EchoLocateVoiceDatabase? = null

        /***
         * Creates database instance
         */
        fun getEchoLocateVoiceDatabase(context: Context): EchoLocateVoiceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EchoLocateVoiceDatabase::class.java,
                    VoiceDatabaseConstants.ECHO_LOCATE_VOICE_DB_NAME
                ).addMigrations(MIGRATION_3_4)
                .fallbackToDestructiveMigration()
                .build()

                INSTANCE = instance
                instance
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE ${VoiceDatabaseConstants.VOICE_REPORT_TABLE_NAME}" +
                        " ADD COLUMN reportStatus TEXT DEFAULT ''")
            }
        }

    }
}