package com.tmobile.mytmobile.echolocate.voice.repository.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.tmobile.echolocate.HistoryProto
import java.io.InputStream
import java.io.OutputStream

object HistorySerializer : Serializer<HistoryProto> {
    override val defaultValue: HistoryProto
        get() = HistoryProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): HistoryProto {
        try {
            return HistoryProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read HistoryProto.", exception)
        }
    }

    override suspend fun writeTo(t: HistoryProto, output: OutputStream) {
        t.writeTo(output)
    }

}