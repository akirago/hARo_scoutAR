package gotitapi.haro_scoutar.data

import android.graphics.Bitmap
import gotitapi.haro_scoutar.api.fromBase64toBitmap
import gotitapi.haro_scoutar.api.toBase64
import org.json.JSONObject

val KEY_NAME = "name"
val KEY_FACE = "face_image"
val KEY_TWITTER = "twitter_id"
val KEY_GITHUB = "github_id"
val KEY_IMAGE = "image"
val KEY_INTRODUCTION = "introduction"

data class RequestData(
    val name: String,
    val bitmap: Bitmap,
    val twitterId: String,
    val githubId: String
) {
    val jsonObject = JSONObject("data")
        .put(KEY_NAME, name)
        .put(KEY_FACE, bitmap.toBase64())
        .put(KEY_TWITTER, twitterId)
        .put(KEY_GITHUB, githubId)
}

data class ResponseData(val jsonObject: JSONObject) {
    val name = jsonObject.optString(KEY_NAME, "")!!
    val twitterData = TwitterData(jsonObject.getJSONObject("twitter_info"))
    val githubData = GithubData(jsonObject.getJSONObject("github_info"))
}

data class TwitterData(val jsonObject: JSONObject) {
    val name = jsonObject.optString(KEY_NAME, "")!!
    val image = jsonObject.optString(KEY_IMAGE, "").fromBase64toBitmap()
    val introduction = jsonObject.optString(KEY_INTRODUCTION, "")
}

data class GithubData(val jsonObject: JSONObject) {
    val name = jsonObject.optString(KEY_NAME, "")!!
    val image = jsonObject.optString(KEY_IMAGE, "").fromBase64toBitmap()
    val introduction = jsonObject.optString(KEY_INTRODUCTION, "")
    val languageList = jsonObject.getJSONArray("language").let {
        val list = ArrayList<String>()
        for (i in 0 until it.length()) {
            list.add(it[i] as String)
        }
        list
    }
}
