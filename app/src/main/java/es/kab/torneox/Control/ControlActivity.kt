package es.kab.torneox.Control

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.lifecycleScope
import es.kab.torneox.Admin.AdminActivity
import es.kab.torneox.Client.ClientActivity

import es.kab.torneox.Control.Fragments.LoginFragment
import es.kab.torneox.Control.Fragments.RecoverFragment
import es.kab.torneox.Control.Fragments.RegisterFragment
import es.kab.torneox.Firebase.FirestoreManager
import es.kab.torneox.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ControlActivity : AppCompatActivity(), LoginFragment.LoginFragmentListener, RegisterFragment.RegisterFragmentListener, RecoverFragment.RecoverFragmentListener {
    lateinit var authManager:AuthManager
    lateinit var firestoreManager: FirestoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authManager = AuthManager()
        firestoreManager = FirestoreManager()
        autoLogin()
        setContentView(R.layout.activity_control)



    }

    override fun onLgnButtonCLicked(email: String, pass: String) {
        lifecycleScope.launch {
            if (firestoreManager.checkAdmin(email,pass)){
                val intent = Intent(applicationContext, AdminActivity::class.java)
                startActivity(intent)
            }else{
                val intent = Intent(applicationContext, ClientActivity::class.java)
                startActivity(intent)
            }
        }

    }

    override fun onTextRecoverCLicked() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<RecoverFragment>(R.id.controlFragmentContainer)
        }
    }

    override fun onRegisterButtonCLicked() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<RegisterFragment>(R.id.controlFragmentContainer)
        }

    }

    override fun onRegButtonCLicked() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<LoginFragment>(R.id.controlFragmentContainer)
        }
    }

    override fun onRcoverButtonCLicked() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<LoginFragment>(R.id.controlFragmentContainer)
        }
    }



    override fun <T> replace() {
    }

    fun autoLogin(){
        var sharedPreferences = getSharedPreferences("es.kab.torneox_preferences",Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email", "")
        if (!email.isNullOrBlank()){
            lifecycleScope.launch(Dispatchers.IO) {
                val pass = firestoreManager.getUserPass()
                if (!pass.isNullOrBlank()){
                    val userLogged = authManager.login(email,pass)
                    withContext(Dispatchers.Main){
                        if (userLogged != null){
                            onLgnButtonCLicked(email, pass)
                        }
                    }
                }

            }
        }
    }
}