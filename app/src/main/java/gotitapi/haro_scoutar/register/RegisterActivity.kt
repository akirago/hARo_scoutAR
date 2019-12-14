package gotitapi.haro_scoutar.register

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import gotitapi.haro_scoutar.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class RegisterActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.register_view)
    }
}