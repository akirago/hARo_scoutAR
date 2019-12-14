package gotitapi.haro_scoutar.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.ByteArrayOutputStream
import java.io.IOException

object HttpUtil {
    private val client = OkHttpClient()

    fun httpGet(url: String): String? {
        val request = Request.Builder()
            .url(url)
            .build()

        val response: Response
        try {
            response = client.newCall(request).execute()
        } catch (e: Exception) {
            throw IOException()
        }
        return response.body?.string()
    }

    fun getTapiocaImage(url: String, bitmap: Bitmap): Bitmap {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
        val byteArray = stream.toByteArray()
        val encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT)

        val requestBody = encodedImage.toRequestBody("plain/text".toMediaTypeOrNull())

        val request = Request
            .Builder()
            .url(url)
            .post(requestBody)
            .build()


        val response: Response
        try {
            response = client.newCall(request).execute()
            Log.d("httpconnection success", response.body?.string())
        } catch (e: Exception) {
            Log.e("httpconnection error",e.message + e.cause)
            throw IOException()
        }

        val jpgarr = Base64.decode(encodedImage, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(jpgarr, 0, jpgarr.size)
        return bitmap
    }



}