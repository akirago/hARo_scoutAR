package gotitapi.haro_scoutar.register

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import gotitapi.haro_scoutar.R
import gotitapi.haro_scoutar.api.HttpUtil
import gotitapi.haro_scoutar.data.RequestData
import kotlinx.android.synthetic.main.register_view.*
import kotlinx.coroutines.*

class RegisterActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    val takeIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    val RESULT_CAMERA = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.register_view)

        take_photo.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startActivityForResult(takeIntent, RESULT_CAMERA)
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        android.Manifest.permission.CAMERA
                    )
                ) {
                    AlertDialog.Builder(this)
                        .setMessage("許可してくれないんで画面を閉じます。")
                        .setPositiveButton("閉じる") { _, _ -> finish() }
                        .show()
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.CAMERA),
                        0
                    )
                }
            }
        }

        send_button.setOnClickListener {
//            val data = RequestData(
//                name_edit_text.text.toString(),
//                photo_image_view.drawable.toBitmap(),
//                twitter_edit_text.text.toString(),
//                github_edit_text.text.toString()
//            )
            launch(Dispatchers.IO) {
                runCatching {
                    HttpUtil.getMock(photo_image_view.drawable.toBitmap())
//                    HttpUtil.getProfile(photo_image_view.drawable.toBitmap())
//                    HttpUtil.registerProfile(RequestData("name", photo_image_view.drawable.toBitmap(), "twitter", "github"))

                }.onSuccess {
                    withContext(Dispatchers.Main) {
//                        if (it.isBlank()) {
//                            finish()
//                        } else {
//                            Toast.makeText(this@RegisterActivity, it, Toast.LENGTH_LONG).show()
//                        }
                    }
                }.onFailure {
                    withContext(Dispatchers.Main) {
                        println(it.message)
                        Toast.makeText(this@RegisterActivity, it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RESULT_CAMERA) {
            data?.extras?.get("data").let {
                if (it is Bitmap) {
                    photo_image_view.setImageBitmap(it)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            0 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(takeIntent, RESULT_CAMERA)
                } else {
                    AlertDialog.Builder(this)
                        .setMessage("許可してくれないんで画面を閉じます。")
                        .setPositiveButton("閉じる") { _, _ -> finish() }
                        .show()
                }
            }
            else -> {

            }
        }
    }
}