package gotitapi.haro_scoutar.register

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import gotitapi.haro_scoutar.R
import gotitapi.haro_scoutar.api.HttpUtil
import gotitapi.haro_scoutar.data.RequestData
import kotlinx.android.synthetic.main.register_view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.register_view)

        send_button.setOnClickListener {
            val data = RequestData(
                name_edit_text.text.toString(),
                photo_image_view.drawable.toBitmap(),
                twitter_edit_text.text.toString(),
                github_edit_text.text.toString()
            )
            launch {
                runCatching {
                    HttpUtil.registerProfile(data)
                }.onSuccess {
                    if (it.isBlank()) {
                        finish()
                    } else {
                        Toast.makeText(this@RegisterActivity, it, Toast.LENGTH_LONG).show()
                    }
                }.onFailure {

                }
            }
        }
    }
}