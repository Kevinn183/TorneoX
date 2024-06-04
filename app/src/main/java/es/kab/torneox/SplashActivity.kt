package es.kab.torneox

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import es.kab.torneox.Control.ControlActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lifecycleScope.launch(Dispatchers.IO){
            delay(2000)
            withContext(Dispatchers.Main){
                val intent = Intent(this@SplashActivity,ControlActivity::class.java)
                startActivity(intent)
                finish()
            }

        }

    }
}