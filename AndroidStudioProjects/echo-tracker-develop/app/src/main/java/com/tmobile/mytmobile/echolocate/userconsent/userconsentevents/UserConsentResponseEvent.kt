package com.tmobile.mytmobile.echolocate.userconsent.userconsentevents

/**
 *  class UserConsentResponseEvent: This class is used for sending the user consent change event to the other modules.
 */

import com.tmobile.mytmobile.echolocate.communicationbus.events.EchoLocateBaseEvent
import com.tmobile.mytmobile.echolocate.userconsent.database.databasemodel.UserConsentResponseModel

data class UserConsentResponseEvent(
        var id: Int = 0,
        var userConsentFlagsParameters: UserConsentFlagsParameters = UserConsentFlagsParameters()
) : EchoLocateBaseEvent() {
    constructor(userConsentResponseEventModel: UserConsentResponseModel) :
            this(
                    userConsentResponseEventModel.id,
                    UserConsentFlagsParameters(
                            userConsentResponseEventModel.userConsentFlagsParametersModel.isAllowedDeviceDataCollection,
                            userConsentResponseEventModel.userConsentFlagsParametersModel.isAllowedIssueAssist,
                            userConsentResponseEventModel.userConsentFlagsParametersModel.isAllowedPersonalizedOffers
                    )
            ) {
        this.sourceComponent = userConsentResponseEventModel.sourceComponent
        this.timeStamp = userConsentResponseEventModel.timeStamp
    }
}