package es.kab.torneox.Client

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationBarView
import es.kab.torneox.Client.Fragments.EncuestaFragment
import es.kab.torneox.Client.Fragments.InicioFragment
import es.kab.torneox.Client.Fragments.PerfilFragment
import es.kab.torneox.Client.Fragments.RankingFragment
import es.kab.torneox.Client.Fragments.TorneosActivosFragment
import es.kab.torneox.Control.AuthManager
import es.kab.torneox.Control.ControlActivity
import es.kab.torneox.Firebase.FirestoreManager
import es.kab.torneox.R
import es.kab.torneox.databinding.ActivityClientBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class ClientActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {
    private lateinit var binding : ActivityClientBinding
    lateinit var firestoreManager: FirestoreManager
    lateinit var authManager: AuthManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.clientBottomNav.setOnItemSelectedListener(this)
        binding.clientBottomNav.selectedItemId = R.id.item_inicio
        setSupportActionBar(binding.clientToolBar)
        firestoreManager = FirestoreManager()
        authManager = AuthManager()

        if (comprobarNombre()){
            crearDialog()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.actionLogOut ->{
                AuthManager().logOut()
                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
                sharedPreferences.edit().clear().apply()
                val intent = Intent(this, ControlActivity::class.java)
                startActivity(intent)
                true
            }
            else -> {
                false
            }
        }
    }


            override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.item_encuestas ->{
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<EncuestaFragment>(R.id.ClientFragmentContainer)
                }
                true
            }
            R.id.item_torneos ->{
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<TorneosActivosFragment>(R.id.ClientFragmentContainer)
                }
                true
            }
            R.id.item_inicio ->{
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<InicioFragment>(R.id.ClientFragmentContainer)
                }
                true
            }
            R.id.item_ranking ->{
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<RankingFragment>(R.id.ClientFragmentContainer)
                }
                true
            }
            R.id.item_perfil ->{
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<PerfilFragment>(R.id.ClientFragmentContainer)
                }
                true
            }
            else -> false
        }
    }
    fun comprobarNombre():Boolean{
        var username = ""
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                username = firestoreManager.getUserName()
            }catch (e:Exception){
                e.toString()
            }
        }
        return username.equals("")
    }
    fun asignarNombre(name:String){

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                firestoreManager.changeUserName(name)
            }catch (e:Exception){
                e.toString()
            }
        }
    }

    fun crearDialog(){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_new_name, null)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        val editName:EditText = dialogView.findViewById(R.id.newName_editText)
        val btnOk: Button = dialogView.findViewById(R.id.newName_button)
        btnOk.setOnClickListener {
            asignarNombre(editName.text.toString())
            alertDialog.dismiss()
        }
        alertDialog.show()
    }



}