package com.tmobile.mytmobile.echolocate.dsdkHandshake

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import com.tmobile.mytmobile.echolocate.dsdkHandshake.database.repository.DsdkHandshakeRepository

class DsdkHandshakeContentProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        return true
    }

    override fun getType(uri: Uri): String? {
        // getType won't be supported with this content provider
        throw UnsupportedOperationException("getType won't be supported with this content provider")
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?,
                       selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        if (context != null) {
            return DsdkHandshakeRepository.getInstance(context!!).getDsdkHandshakeEvent()
        }

        throw IllegalArgumentException("Failed to retrieve data for dsdk handshake")
    }

    override fun delete(uri: Uri, selection: String?,
                        selectionArgs: Array<String>?): Int {
        // delete won't be supported with this content provider
        throw UnsupportedOperationException("delete won't be supported with this content provider")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        // insert won't be supported with this content provider
        throw UnsupportedOperationException("insert won't be supported with this content provider")
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                        selectionArgs: Array<String>?): Int {
        // update won't be supported with this content provider
        throw UnsupportedOperationException("update won't be supported with this content provider")
    }

}