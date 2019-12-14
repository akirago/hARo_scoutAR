package gotitapi.haro_scoutar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import gotitapi.haro_scoutar.ar.ArActivity
import gotitapi.haro_scoutar.register.RegisterActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        move_haro_button.setOnClickListener {
            startActivity(Intent(this, ArActivity::class.java))
        }

        move_register_button.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
