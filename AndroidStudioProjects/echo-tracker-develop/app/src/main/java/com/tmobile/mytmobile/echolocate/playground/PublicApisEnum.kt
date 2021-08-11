package com.tmobile.mytmobile.echolocate.playground

enum class PublicApisEnum(val key: String) {
    CELL_INFO_5G("5gCellInfo"),
    CELL_SIGNAL_STRENGTH_5G("5gCellSignalStrength"),
    CELL_IDENTITY_5G("5gCellIdentity"),
    NETWORK_SCAN_P("NetworkScan"),
    NETWORK_TYPE_5G("NetworkType5G"),
    DATA_NETWORK_TYPE("DataNetworkType"),
    DISPLAY_STATE_REGISTER("Register Display Listener"),
    DISPLAY_STATE_UNREGISTER("Unregister Display Listener"),
    NETWORK_BANDWIDTH("NetworkBandwidth"),
    CLEAR("Clear")
}