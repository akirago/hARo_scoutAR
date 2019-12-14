package gotitapi.haro_scoutar.api

import android.graphics.Bitmap
import android.util.Log
import gotitapi.haro_scoutar.data.RequestData
import gotitapi.haro_scoutar.data.ResponseData
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

object HttpUtil {
    private val client = OkHttpClient()

    const val SCHEME = "http://"
    const val url = ""

//    fun httpGet(url: String): String? {
//        val request = Request.Builder()
//            .url(url)
//            .build()
//
//        val response: Response
//        try {
//            response = client.newCall(request).execute()
//        } catch (e: Exception) {
//            throw IOException()
//        }
//        return response.body?.string()
//    }

    fun registerProfile(requestData: RequestData): String {
        val url = "https://c50cd690.ngrok.io/register"
        println(requestData.jsonObject)
        val requestBody =
            requestData.jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
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
            Log.e("httpconnection error", e.message + e.cause)
            throw IOException()
        }
        return response.body!!.string()
    }


    fun getProfile(bitmap: Bitmap): ResponseData {
        val url =
            "https://c50cd690.ngrok.io/getinfo"
        val encodedImage = bitmap.toBase64()
        val requestBody = encodedImage.toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request
            .Builder()
            .url(url)
            .put(requestBody)
            .get()
            .build()


        val response: Response
        try {
            response = client.newCall(request).execute()
            Log.d("httpconnection success", response.body?.string())
        } catch (e: Exception) {
            Log.e("httpconnection error", e.message + e.cause)
            throw IOException()
        }

        return ResponseData(JSONObject(response.body!!.string()))
    }

}
