package com.tmobile.mytmobile.echolocate.userconsent.database.databasemodel

/**
 *  class UserConsentResponseModel used for storing the user consent flags in to the database.
 */

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tmobile.mytmobile.echolocate.communicationbus.events.EchoLocateBaseEvent
import com.tmobile.mytmobile.echolocate.userconsent.userconsentevents.UserConsentResponseEvent
import com.tmobile.mytmobile.echolocate.userconsent.utils.UserConsentStringUtils

@Entity(tableName = UserConsentStringUtils.USER_CONSENT_TABLE_NAME)
data class UserConsentResponseModel(

        @PrimaryKey(autoGenerate = true)
        var id: Int = 0,

        @Embedded
        var userConsentFlagsParametersModel: UserConsentFlagsParametersModel = UserConsentFlagsParametersModel()

) : EchoLocateBaseEvent() {
    constructor(userConsentResponseEvent: UserConsentResponseEvent) :
            this(
                    userConsentResponseEvent.id,
                    UserConsentFlagsParametersModel(
                            userConsentResponseEvent.userConsentFlagsParameters.isAllowedDeviceDataCollection,
                            userConsentResponseEvent.userConsentFlagsParameters.isAllowedIssueAssist,
                            userConsentResponseEvent.userConsentFlagsParameters.isAllowedPersonalizedOffers
                    )
            ) {
        this.sourceComponent = userConsentResponseEvent.sourceComponent
        this.timeStamp = userConsentResponseEvent.timeStamp
    }
}