package com.tmobile.mytmobile.echolocate.lte.utils

import com.tmobile.mytmobile.echolocate.lte.database.entity.LteOEMSVEntity
import com.tmobile.mytmobile.echolocate.standarddatablocks.OEMSV


class LteEntityConverter {
    companion object {

        /**
         * Converts [OEMSV] to [LteOEMSVEntity]
         * @param oemSoftwareVersion : [OEMSV]
         * @return [LteOEMSVEntity]
         */
        fun convertLteOEMSVEntity(
            oemSoftwareVersion: OEMSV
        ): LteOEMSVEntity {
            return LteOEMSVEntity(
                oemSoftwareVersion.softwareVersion,
                oemSoftwareVersion.customVersion,
                oemSoftwareVersion.radioVersion,
                oemSoftwareVersion.buildName,
                oemSoftwareVersion.androidVersion
            )
        }

    }
}