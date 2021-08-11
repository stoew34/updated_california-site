package com.tmobile.mytmobile.echolocate.voice.utils

import com.tmobile.mytmobile.echolocate.standarddatablocks.OEMSV
import com.tmobile.mytmobile.echolocate.voice.repository.database.entity.*
import com.tmobile.mytmobile.echolocate.voice.model.*

/**
 * Converter class that converts the model to its respective Entities inorder to save the entities in the database
 */
class VoiceEntityConverter {

    companion object {

        /**
         * Converts [DetailedCallStateData] to [DetailedCallStateEntity]
         * @param detailedCallStateData: [DetailedCallStateData]
         * @param baseVoiceData: [BaseVoiceData]
         * @return [DetailedCallStateEntity] converted entity
         */
        fun convertDetailedCallStateDataToEntity(
            detailedCallStateData: DetailedCallStateData,
            baseVoiceData: BaseVoiceData
        ): DetailedCallStateEntity {
            val detailedCallStateEntity = DetailedCallStateEntity(
                detailedCallStateData.callCode,
                detailedCallStateData.callState,
                detailedCallStateData.oemTimestamp,
                detailedCallStateData.eventTimestamp
            )
            detailedCallStateEntity.callId = baseVoiceData.callId
            detailedCallStateEntity.uniqueId = baseVoiceData.uniqueId
            return detailedCallStateEntity
        }

        /**
         * Converts [CallSettingData] to [CallSettingDataEntity]
         * @param callSettingData: [CallSettingData]
         * @param baseVoiceData: [BaseVoiceData]
         * @return [CallSettingDataEntity] converted entity
         */
        fun convertCallSettingDataToEntity(
            callSettingData: CallSettingData,
            baseVoiceData: BaseVoiceData
        ): CallSettingDataEntity {
            val callSettingEntity = CallSettingDataEntity(
                callSettingData.volteStatus,
                callSettingData.wfcStatus,
                callSettingData.wfcPreference,
                callSettingData.oemTimestamp,
                callSettingData.eventTimestamp
            )
            callSettingEntity.callId = baseVoiceData.callId
            callSettingEntity.uniqueId = baseVoiceData.uniqueId
            return callSettingEntity
        }

        /**
         * Converts [CellInfo] to [CellInfoEntity]
         * @param cellInfo: [CellInfo]
         * @param baseVoiceData: [BaseVoiceData]
         * @return [CellInfoEntity] converted entity
         */
        fun convertCellInfoDataToEntity(
            cellInfo: CellInfo,
            baseVoiceData: BaseVoiceData
        ): CellInfoEntity {
            val cellInfoEntity = CellInfoEntity(
                cellInfo.ecio,
                cellInfo.rscp,
                cellInfo.rsrp,
                cellInfo.rsrq,
                cellInfo.rssi,
                cellInfo.sinr,
                cellInfo.snr,
                cellInfo.lac,
                cellInfo.networkBand,
                cellInfo.cellId,
                cellInfo.networkType
            )
            cellInfoEntity.callId = baseVoiceData.callId
            cellInfoEntity.uniqueId = baseVoiceData.uniqueId
            return cellInfoEntity
        }

        /**
         * Converts [LocationData] to [VoiceLocationEntity]
         * @param location: [VoiceLocationEntity]
         * @param baseVoiceData: [BaseVoiceData]
         * @return [VoiceLocationEntity] converted entity
         */
        fun convertLocationDataToEntity(
            location: LocationData?,
            baseVoiceData: BaseVoiceData
        ): VoiceLocationEntity? {

            if (location == null) {
                return null
            }

            val locationEntity = VoiceLocationEntity(
                location.altitude,
                location.altitudePrecision,
                location.latitude,
                location.longitude,
                location.precision,
                location.locationAge
            )
            locationEntity.callId = baseVoiceData.callId
            locationEntity.uniqueId = baseVoiceData.uniqueId
            return locationEntity
        }

        /**
         * Converts [UiCallStateData] to [UiCallStateEntity]
         * @param uiCallStateData: [UiCallStateData]
         * @param baseVoiceData: [BaseVoiceData]
         * @return [UiCallStateEntity] converted entity
         */
        fun convertUICallStateDataToEntity(
            uiCallStateData: UiCallStateData,
            baseVoiceData: BaseVoiceData
        ): UiCallStateEntity {
            val uiCallStateEntity = UiCallStateEntity(
                uiCallStateData.uICallState,
                uiCallStateData.oemTimestamp,
                uiCallStateData.eventTimestamp
            )
            uiCallStateEntity.callId = baseVoiceData.callId
            uiCallStateEntity.uniqueId = baseVoiceData.uniqueId
            return uiCallStateEntity
        }

        /**
         * Converts [radioHandoverData] to [RadioHandoverData]
         * @param radioHandoverData: [RadioHandoverData]
         * @param baseVoiceData: [BaseVoiceData]
         * @return [RadioHandoverEntity] converted entity
         */
        fun convertRadioHandOverDataToEntity(
            radioHandoverData: RadioHandoverData,
            baseVoiceData: BaseVoiceData
        ): RadioHandoverEntity {
            val radioHandoverEntity = RadioHandoverEntity(
                radioHandoverData.handoverState,
                radioHandoverData.oemTimestamp,
                radioHandoverData.eventTimestamp
            )
            radioHandoverEntity.callId = baseVoiceData.callId
            radioHandoverEntity.uniqueId = baseVoiceData.uniqueId
            return radioHandoverEntity
        }

        /**
         * Converts [ImsSignallingData] to [ImsSignallingEntity]
         * @param imsSignallingData: [ImsSignallingData]
         * @param baseVoiceData: [BaseVoiceData]
         * @return [ImsSignallingEntity] converted entity
         */
        fun convertImsSignallingDataToEntity(
            imsSignallingData: ImsSignallingData,
            baseVoiceData: BaseVoiceData
        ): ImsSignallingEntity {
            val imsSignallingEntity = ImsSignallingEntity(
                imsSignallingData.sipCallId,
                imsSignallingData.sipCseq,
                imsSignallingData.sipLine1,
                imsSignallingData.sipOrigin,
                imsSignallingData.sipReason,
                imsSignallingData.sipSDP,
                imsSignallingData.oemTimestamp,
                imsSignallingData.eventTimestamp
            )
            imsSignallingEntity.callId = baseVoiceData.callId
            imsSignallingEntity.uniqueId = baseVoiceData.uniqueId
            return imsSignallingEntity
        }

        /**
         * Converts [AppTriggeredCallData] to [AppTriggeredCallDataEntity]
         * @param appTriggeredCallData: [DetailedCallStateData]
         * @param baseVoiceData: [BaseVoiceData]
         * @return [AppTriggeredCallDataEntity] converted entity
         */
        fun convertAppTriggeredCallDataToEntity(
            appTriggeredCallData: AppTriggeredCallData,
            baseVoiceData: BaseVoiceData
        ): AppTriggeredCallDataEntity {
            val appTriggeredCallDataEntity = AppTriggeredCallDataEntity(
                appTriggeredCallData.appName,
                appTriggeredCallData.appPackageId,
                appTriggeredCallData.appVersionCode,
                appTriggeredCallData.appVersionName,
                appTriggeredCallData.oemTimestamp,
                appTriggeredCallData.eventTimestamp
            )
            appTriggeredCallDataEntity.callId = baseVoiceData.callId
            appTriggeredCallDataEntity.uniqueId = baseVoiceData.uniqueId
            return appTriggeredCallDataEntity
        }

        /***
         *  Converts [RtpdlStateData] to [RtpdlStateEntity]
         *  @param rtpdlStateData: [RtpdlStateData]
         *  @param baseVoiceData: [BaseVoiceData]
         *  @return [RtpdlStateEntity] converted entity
         */
        fun convertRtpdlStateDatatoEntity(
            rtpdlStateData: RtpdlStateData,
            baseVoiceData: BaseVoiceData
        ): RtpdlStateEntity {
            val rtpdlStateEntity = RtpdlStateEntity(
                rtpdlStateData.delay,
                rtpdlStateData.sequence,
                rtpdlStateData.jitter,
                rtpdlStateData.lossRate,
                rtpdlStateData.measuredPeriod,
                rtpdlStateData.oemTimestamp,
                rtpdlStateData.eventTimestamp
            )
            rtpdlStateEntity.callId = baseVoiceData.callId
            rtpdlStateEntity.uniqueId = baseVoiceData.uniqueId

            return rtpdlStateEntity

        }

        /**
         * Converts [OEMSV] to [OEMSoftwareVersionEntity]
         * @param oemSoftwareVersion : [OEMSV]
         * @param baseVoiceData : [BaseVoiceData]
         * @return [OEMSoftwareVersionEntity]
         */
        fun convertOEMSoftwareVersiontoEntity(
            oemSoftwareVersion: OEMSV,
            baseVoiceData: BaseVoiceData
        ): OEMSoftwareVersionEntity {
            val oemSoftwareVersionEntity = OEMSoftwareVersionEntity(
                oemSoftwareVersion.softwareVersion?: "",
                oemSoftwareVersion.customVersion?: "",
                oemSoftwareVersion.radioVersion?: "",
                oemSoftwareVersion.buildName,
                oemSoftwareVersion.androidVersion
            )
            oemSoftwareVersionEntity.callId = baseVoiceData.callId
            oemSoftwareVersionEntity.uniqueId = baseVoiceData.uniqueId

            return oemSoftwareVersionEntity
        }

        /**
         * Converts [EmergencyCallTimerStateData] to [EmergencyCallTimerStateEntity]
         * @param emergencyCallTimerStateData: [EmergencyCallTimerStateData]
         * @param baseVoiceData: [BaseVoiceData]
         * @return [EmergencyCallTimerStateEntity] converted entity
         */
        fun convertEmergencyCallTimerStateEntity(
            emergencyCallTimerStateData: EmergencyCallTimerStateData,
            baseVoiceData: BaseVoiceData
        ): EmergencyCallTimerStateEntity {
            val emergencyCallTimerStateEntity = EmergencyCallTimerStateEntity(
                emergencyCallTimerStateData.timerName,
                emergencyCallTimerStateData.timerState,
                emergencyCallTimerStateData.eventTimestamp,
                emergencyCallTimerStateData.oemTimestamp
            )
            emergencyCallTimerStateEntity.callId = baseVoiceData.callId
            emergencyCallTimerStateEntity.uniqueId = baseVoiceData.uniqueId
            return emergencyCallTimerStateEntity
        }

        /***
         *  Converts [CarrierConfigData] to [CarrierConfigDataEntity]
         *  @param carrierConfigData: [CarrierConfigData]
         *  @param baseVoiceData: [BaseVoiceData]
         *  @return [CarrierConfigDataEntity] converted entity
         */
        fun convertCarrierConfigDataToEntity(
            carrierConfigData: CarrierConfigData,
            baseVoiceData: BaseVoiceData,
            keys: String,
            values: String
        ): CarrierConfigDataEntity {
            val carrierConfigDataEntity = CarrierConfigDataEntity(
                carrierConfigData.carrierConfigVersion,
                carrierConfigData.carrierVoWiFiConfig,
                keys,
                values,
                carrierConfigData.carrierVoiceConfig,
                carrierConfigData.eventTimestamp
            )
            carrierConfigDataEntity.callId = baseVoiceData.callId
            carrierConfigDataEntity.uniqueId = baseVoiceData.uniqueId
            return carrierConfigDataEntity
        }

    }
}