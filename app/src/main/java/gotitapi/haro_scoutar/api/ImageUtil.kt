package gotitapi.haro_scoutar.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

fun Bitmap.toBase64(): String {
    val stream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 0, stream)
    val byteArray = stream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

fun String.fromBase64toBitmap(): Bitmap {
    val jpgarr = Base64.decode(this, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(jpgarr, 0, jpgarr.size)
}