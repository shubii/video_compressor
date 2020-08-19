package com.example.videocompressor.utils

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import java.io.*
import java.lang.Math.log10
import java.text.DecimalFormat
import kotlin.math.pow

fun getMediaPath(context: Context, uri: Uri): String {

    val resolver = context.applicationContext.contentResolver
    val projection = arrayOf(MediaStore.Video.Media.DATA)
    var cursor: Cursor? = null
    try {
        cursor = resolver.query(uri, projection, null, null, null)
        return if (cursor != null) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
            cursor.moveToFirst()
            cursor.getString(columnIndex)

        } else ""

    } catch (e: Exception) {
        resolver.let {
            val filePath = (context.applicationInfo.dataDir + File.separator
                    + System.currentTimeMillis())
            val file = File(filePath)

            resolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    val buf = ByteArray(4096)
                    var len: Int
                    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(
                        buf,
                        0,
                        len
                    )
                }
            }
            return file.absolutePath
        }
    } finally {
        cursor?.close()
    }
}

fun getFileSize(size: Long): String {
    if (size <= 0)
        return "0"

    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()

    return DecimalFormat("#,##0.#").format(
        size / 1024.0.pow(digitGroups.toDouble())
    ) + " " + units[digitGroups]
}
