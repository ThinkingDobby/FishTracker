package com.example.thinkingdobby.fishtracker.functions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

// 이미지 회전
fun rotateImage(source: Bitmap, angle: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(source, 0, 0, source.width, source.height,
            matrix, true)
}

// 절대경로 확인
fun createCopyAndReturnRealPath(appContext: Context, uri: Uri) :String? {
    val contentResolver = appContext.contentResolver ?: return null

    // Create file path inside app's data dir
    val filePath = (appContext.applicationInfo.dataDir + File.separator
            + System.currentTimeMillis())
    val file = File(filePath)
    try {
        val inputStream = contentResolver.openInputStream(uri) ?: return null
        val outputStream: OutputStream = FileOutputStream(file)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
        outputStream.close()
        inputStream.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return file.absolutePath
}