package com.challenge.profileviewer.domain.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import io.reactivex.Observable
import java.io.ByteArrayOutputStream

class ImageToBase64Encoder(
    private val compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
    private val quality: Int = 50
) {
    fun encode(pathToImage: String): Observable<String> =
        Observable.fromCallable {
            val image: Bitmap = BitmapFactory.decodeFile(pathToImage)
            val byteArrayOS = ByteArrayOutputStream()
            image.compress(compressFormat, quality, byteArrayOS)
            Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT)
        }
}