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
        val url =
            "https://script.googleusercontent.com/macros/echo?user_content_key=m17C-2O7Y33-T05ZaAtg5NQTW-H6kMQEyJn-WSCNg9ys0YMc2FMYobrhFUr8SKM3gOODLndk1c67P1xeDLvisIwDIcpMORNim5_BxDlH2jW0nuo2oDemN9CCS2h10ox_1xSncGQajx_ryfhECjZEnMkLwWFcKOZPZTQ6A19OYO-qfv_HDvlMqy75g4cvgBOIl7rg4Glfcim8mzDTCqnGIg&lib=MWHiZkIRObrNABHwdCNQqltZcfNXvf530"
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
            "https://script.googleusercontent.com/macros/echo?user_content_key=m17C-2O7Y33-T05ZaAtg5NQTW-H6kMQEyJn-WSCNg9ys0YMc2FMYobrhFUr8SKM3gOODLndk1c67P1xeDLvisIwDIcpMORNim5_BxDlH2jW0nuo2oDemN9CCS2h10ox_1xSncGQajx_ryfhECjZEnMkLwWFcKOZPZTQ6A19OYO-qfv_HDvlMqy75g4cvgBOIl7rg4Glfcim8mzDTCqnGIg&lib=MWHiZkIRObrNABHwdCNQqltZcfNXvf530"
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
