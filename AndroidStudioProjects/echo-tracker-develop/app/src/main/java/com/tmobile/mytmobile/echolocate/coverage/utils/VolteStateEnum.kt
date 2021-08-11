package com.tmobile.mytmobile.echolocate.coverage.utils

/**
 *
 * Enum class for volteState in CoverageSettings
 */

enum class VolteStateEnum(val key: Int) {

    /**
     * Used for define volteState for CoverageSettings
     */
    ENABLED(0),
    DISABLED(1),
    UNSUPPORTED(-1);

    companion object {
        fun valueOf(value: Int): VolteStateEnum? = values().find { it.key == value }
    }
}