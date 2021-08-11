package com.tmobile.mytmobile.echolocate.lte.utils

import com.tmobile.mytmobile.echolocate.utils.EchoLocateLog
import com.tmobile.mytmobile.echolocate.lte.utils.logcat.LogcatListener
import java.util.regex.Pattern

class LogcatlistenerItem(
    val id: String,
    internal val resultAction: String,
    regexList: List<String>,
    internal val type: LogcatListener.Type) {


    var regexToSearch: Pattern = try {
        Pattern.compile(regexList.joinToString(separator = "|"))
    } catch (e: Exception) {
        EchoLocateLog.eLogE("Diagnostic : Regex pattern is not valid, returning empty string")
        Pattern.compile("")
    }

    override fun toString(): String {
        return "id: $id, result action: $resultAction"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as LogcatlistenerItem

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}