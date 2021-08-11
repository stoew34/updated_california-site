package com.tmobile.mytmobile.echolocate.voice.repository.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.tmobile.echolocate.CallStatusProto
import java.io.InputStream
import java.io.OutputStream

object CallStatusSerializer : Serializer<CallStatusProto> {
    override val defaultValue: CallStatusProto
        get() = CallStatusProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): CallStatusProto {
        try {
            return CallStatusProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read CallStatusProto.", exception)
        }
    }

    override suspend fun writeTo(t: CallStatusProto, output: OutputStream) {
        t.writeTo(output)
    }
}