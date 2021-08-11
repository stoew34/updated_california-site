package com.tmobile.mytmobile.echolocate.utils

import android.content.Intent
import java.util.*

/**
 * This class handles the intents from broadcast receiver
 */
class DevLogUtils {

    companion object {

        /**
         * check the intents received from receiver is not null and append all the intents to a string
         * @param intent:
         */

        fun getIntentData(intent: Intent?): String {
            val intentDescriptionStringBuilder = StringBuilder()
            if (intent != null) {

                val calendar : Calendar = Calendar.getInstance()
                intentDescriptionStringBuilder.append("\n" + calendar.time.toString() + "\n")
                intentDescriptionStringBuilder.append("--INTENT--\n")
                intentDescriptionStringBuilder.append("Action: ")
                        .append(intent.action)
                val bundle = intent.extras
                if (bundle != null) {
                    intentDescriptionStringBuilder.append("\nWith extras:\n")
                    for (key in bundle.keySet()) {
                        val value = bundle.get(key)
                        if (value != null) {
                            intentDescriptionStringBuilder.append(
                                    String.format(
                                            "%s %s (%s)", key, value.toString(), value.javaClass
                                            .name
                                    )
                            )
                        } else {
                            intentDescriptionStringBuilder.append(
                                    String.format(
                                            "%s %s (%s)",
                                            key,
                                            "null",
                                            "null"
                                    )
                            )
                        }
                        intentDescriptionStringBuilder.append("\n")
                    }
                } else {
                    intentDescriptionStringBuilder.append("\nWithout extras.")
                }
            }
            return intentDescriptionStringBuilder.toString()
        }
    }
}