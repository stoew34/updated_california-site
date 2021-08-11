package com.tmobile.mytmobile.echolocate.dsdkHandshake.database.dao

import android.database.Cursor
import androidx.room.*
import com.tmobile.mytmobile.echolocate.dsdkHandshake.database.databasemodel.DsdkHandshakeParametersModel
import com.tmobile.mytmobile.echolocate.dsdkHandshake.utils.DsdkHandshakeUtils

@Dao
interface DsdkHandshakeDao {
    /**
     * Inserts [DsdkHandshakeParametersModel] parameters into the database in a single transaction.
     * @param dsdkHandshakeParametersModel :DsdkHandshakeResponseModel object
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertToDB(dsdkHandshakeParametersModel: DsdkHandshakeParametersModel)

    /**
     * query to get most current entry from user consent job table
     */
    @Query("SELECT * from ${DsdkHandshakeUtils.DSDK_HANDSHAKE_TABLE_NAME} LIMIT 1")
    fun getDsdkHandshakeFlags(): Cursor?

    /**
     * Gets count of [DsdkHandshakeResponseModel] parameters from the database in a single transaction.
     */
    @Query("SELECT COUNT(*) FROM ${DsdkHandshakeUtils.DSDK_HANDSHAKE_TABLE_NAME}")
    fun getCount(): Int

    /**
     * Gets voice flag value from the database in a single transaction.
     */
    @Query("SELECT voiceStopDataCollection FROM " + DsdkHandshakeUtils.DSDK_HANDSHAKE_TABLE_NAME )
    fun getVoiceStopDataCollectionFlag(): Boolean

    /**
     * Gets lte flag value from the database in a single transaction.
     */
    @Query("SELECT lteStopDataCollection FROM " + DsdkHandshakeUtils.DSDK_HANDSHAKE_TABLE_NAME )
    fun getLteStopDataCollectionFlag(): Boolean

    /**
     * Gets nr5g flag value from the database in a single transaction.
     */
    @Query("SELECT nr5gStopDataCollection FROM " + DsdkHandshakeUtils.DSDK_HANDSHAKE_TABLE_NAME )
    fun getNr5gtopDataCollectionFlag(): Boolean

    /**
     * Deletes all the data from dsdk handshake table
     */
    @Query("DELETE FROM ${DsdkHandshakeUtils.DSDK_HANDSHAKE_TABLE_NAME}")
    fun deleteAll() : Int

//    @Transaction
    suspend fun insertDsdkHandshakeFlags(dsdkHandshakeParametersModel: DsdkHandshakeParametersModel): Boolean {
        val count = getCount()

        if (count >= 1) {
            deleteAll()
        }

        insertToDB(dsdkHandshakeParametersModel)

        return true
    }

}