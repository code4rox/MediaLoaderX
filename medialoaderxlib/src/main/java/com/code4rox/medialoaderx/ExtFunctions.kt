package com.code4rox.medialoaderx

import android.database.Cursor
import android.util.Log


fun Cursor.getColumnString(mediaColumn: String): String = getString(getColumnIndexOrThrow(mediaColumn)) ?: ""
fun Cursor.getColumnLong(mediaColumn: String): Long = getLong(getColumnIndexOrThrow(mediaColumn))


fun String.printIt() {
    Log.d("MediaLoaderX-->", this)
}


