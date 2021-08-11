package com.tmobile.mytmobile.echolocate.voice.repository.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.tmobile.echolocate.CallSessionProto
import java.io.InputStream
import java.io.OutputStream

object CallSessionSerializer : Serializer<CallSessionProto> {
    override val defaultValue: CallSessionProto
        get() = CallSessionProto.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): CallSessionProto {
        try {
            return CallSessionProto.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read CallSessionProto.", exception)
        }
    }

    override suspend fun writeTo(t: CallSessionProto, output: OutputStream) {
        t.writeTo(output)
    }

}
