package gotitapi.haro_scoutar.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import java.util.concurrent.TimeUnit


object HttpUtil {
    private val client = OkHttpClient.Builder()
        .callTimeout(10000, TimeUnit.MINUTES)
        .connectTimeout(10000, TimeUnit.MINUTES)
        .readTimeout(10000, TimeUnit.MINUTES)
        .writeTimeout(10000, TimeUnit.MINUTES).build()


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

    // get twitter icon
    fun getIcon(url: String): Bitmap {
        val request = Request.Builder().url(url).get().build()

        val response: Response
        try {
            response = client.newCall(request).execute()
//            Log.d("httpconnection success", response.body!!.byteStream().toString())
        } catch (e: Exception) {
            Log.e("httpconnection error", e.message + e.cause)
            throw IOException()
        }

        return BitmapFactory.decodeStream(response.body!!.byteStream())
    }


    fun registerProfile(requestData: RequestData): String {
        val url = "https://e5a17d44.ngrok.io/register"
//        val url = "https://c50cd690.ngrok.io/register"
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
            "https://41dcf92b.ngrok.io/getinfo"
        // ↓モック
//            "https://script.googleusercontent.com/macros/echo?user_content_key=m17C-2O7Y33-T05ZaAtg5NQTW-H6kMQEyJn-WSCNg9ys0YMc2FMYobrhFUr8SKM3gOODLndk1c67P1xeDLvisIwDIcpMORNim5_BxDlH2jW0nuo2oDemN9CCS2h10ox_1xSncGQajx_ryfhECjZEnMkLwWFcKOZPZTQ6A19OYO-qfv_HDvlMqy75g4cvgBOIl7rg4Glfcim8mzDTCqnGIg&lib=MWHiZkIRObrNABHwdCNQqltZcfNXvf530"
        val encodedImage = bitmap.toBase64()
        val requestBody = "".toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request
            .Builder()
            .url(url)
            .post(requestBody)
            .build()


        val response: Response
        try {
            response = client.newCall(request).execute()
//            Log.d("httpconnection success", response.body?.string())
        } catch (e: Exception) {
            Log.e("httpconnection error", e.message + e.cause)
            throw IOException()
        }
        println("testteststart ${response.code}")
//        println("testteststart ${JSONObject(response.body?.string()).optString("data")}")
        var data_json = response.body!!.string().replace("\\", "")
        println("test $data_json")
        val json = JSONObject(data_json)
        println("test json ${json.optJSONObject("data").optString("name")}")
//        println("test $string")
//        val json = JSONObject("{" +
//                "\"data\": {" +
//                "\"name\": \"inuneko\"," +
//                "\"github_info\": {" +
//                "\"name\": \"inu\"," +
//                "\"image\": \"ASFSADA..\"," +
//                "\"introduction\": \"qwerty..\"," +
//                "\"language\": [{" +
//                "\"name\": \"java\"," +
//                "\"amount\": 10000" +
//                "}, {" +
//                "\"name\": \"go\"," +
//                "\"amount\": 200" +
//                "}]" +
//                "}," +
//                "\"twitter_info\": {" +
//                "\"name\": \"neko\"," +
//                "\"image\": \"ASDFASDFA..\"," +
//                "\"introduction\": \"qwerty..\"" +
//                "}" +
//                "}" +
//                "}")
//        println("test ${json.optJSONObject("data").optString("name")}")
        val data = ResponseData(json)
        println(data.twitterData.name)
//        val data = ResponseData(JSONObject())

        println(data.name)
        println(data.githubData.languageList)
//        println(data.twitterData.name)
        return data
    }

    fun getMock(bitmap: Bitmap): ResponseData {
        val url =
//            "https://c50cd690.ngrok.io/getinfo"
            "https://41dcf92b.ngrok.io/getinfo"
        // ↓モック
//            "https://script.googleusercontent.com/macros/echo?user_content_key=m17C-2O7Y33-T05ZaAtg5NQTW-H6kMQEyJn-WSCNg9ys0YMc2FMYobrhFUr8SKM3gOODLndk1c67P1xeDLvisIwDIcpMORNim5_BxDlH2jW0nuo2oDemN9CCS2h10ox_1xSncGQajx_ryfhECjZEnMkLwWFcKOZPZTQ6A19OYO-qfv_HDvlMqy75g4cvgBOIl7rg4Glfcim8mzDTCqnGIg&lib=MWHiZkIRObrNABHwdCNQqltZcfNXvf530"
        val encodedImage = bitmap.toBase64()
        val requestBody =
            RequestData("", bitmap, "", "").jsonObject.toString()
                .toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request
            .Builder()
            .url(url)
            .post(requestBody)
            .build()


        val response: Response
        try {
            response = client.newCall(request).execute()
//            Log.d("httpconnection success", response.body?.string())
        } catch (e: Exception) {
            Log.e("httpconnection error", e.message + e.cause)
            throw IOException()
        }
//        println("testteststart")
//        println("testteststart ${JSONObject(response.body?.string()).optString("data")}")
        var data_json = response.body!!.string().replace("\\", "")
        println("test $data_json")
        val json = JSONObject(data_json)
        println("test $json")
//        val json = JSONObject("{" +
//                "\"data\": {" +
//                "\"name\": \"inuneko\"," +
//                "\"github_info\": {" +
//                "\"name\": \"inu\"," +
//                "\"image\": \"ASFSADA..\"," +
//                "\"introduction\": \"qwerty..\"," +
//                "\"language\": [{" +
//                "\"name\": \"java\"," +
//                "\"amount\": 10000" +
//                "}, {" +
//                "\"name\": \"go\"," +
//                "\"amount\": 200" +
//                "}]" +
//                "}," +
//                "\"twitter_info\": {" +
//                "\"name\": \"neko\"," +
//                "\"image\": \"ASDFASDFA..\"," +
//                "\"introduction\": \"qwerty..\"" +
//                "}" +
//                "}" +
//                "}")
//        println("test ${json.optJSONObject("data").optString("name")}")
        val data = ResponseData(json)
//        println(data.twitterData.name)
//        val data = ResponseData(JSONObject())

        println("ここ" + data.name)
        println(data.githubData.languageList)
//        println(data.twitterData.name)
        return data
    }

}
