package com.tmobile.mytmobile.echolocate.userconsent.database.dao

import androidx.room.*
import com.tmobile.mytmobile.echolocate.userconsent.database.databasemodel.UserConsentResponseModel
import com.tmobile.mytmobile.echolocate.userconsent.utils.UserConsentStringUtils
import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import io.reactivex.Flowable

/**
 * Dao for user consent job table
 */
@Dao
abstract class UserConsentDao {

    /**
     * query to get most current entry from user consent job table
     */
    @Query("SELECT * from ${UserConsentStringUtils.USER_CONSENT_TABLE_NAME} ORDER BY timeStamp DESC LIMIT 1")
    abstract fun getLatestUserConsent(): UserConsentResponseModel?

    /**
     * query to get oldest entry from user consent job table
     */
    @Query("SELECT * from ${UserConsentStringUtils.USER_CONSENT_TABLE_NAME} ORDER BY timeStamp ASC LIMIT 1")
    abstract fun getOldestUserConsent(): UserConsentResponseModel

    /**
     * query to get entire data from user consent job table
     */
    @Query("SELECT * from " + UserConsentStringUtils.USER_CONSENT_TABLE_NAME)
    abstract fun getUserConsentHistory(): Flowable<List<UserConsentResponseModel>>

    /**
     * Inserts records in user consent job table
     *
     * @param userConsentResponseEvent the user consent response object to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(userConsentResponseEvent: UserConsentResponseModel): Long

    /**
     * deletes record from user consent job table
     */
    @Delete
    abstract fun delete(userConsentResponseEvent: UserConsentResponseModel)

    /**
     * deletes record from user consent job table
     */
    @Query("DELETE FROM ${UserConsentStringUtils.USER_CONSENT_TABLE_NAME}")
    abstract fun deleteAll()

    /**
     *  gets number of records in db table
     */
    @Query("SELECT COUNT() FROM ${UserConsentStringUtils.USER_CONSENT_TABLE_NAME}")
    abstract fun getCount(): Int

    @Transaction
    open fun insertUserConsentResponseEvent(userConsentResponseModel: UserConsentResponseModel): Long {
        val count = getCount()
        EchoLocateLog.eLogD(
                "DB Count before insert = $count",
                System.currentTimeMillis()
        )

        if (count >= 10) {
            val oldUserConsent = getOldestUserConsent()
            delete(oldUserConsent)
        }
        val insertedCount = insert(userConsentResponseModel)
        EchoLocateLog.eLogD(
                "DB insert rows Count = $insertedCount",
                System.currentTimeMillis()
        )
        EchoLocateLog.eLogD(
                "DB Count After insert = " + getCount(),
                System.currentTimeMillis()
        )
        return insertedCount
    }
}