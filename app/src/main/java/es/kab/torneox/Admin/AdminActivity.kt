package es.kab.torneox.Admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationBarView
import es.kab.torneox.Admin.Fragments.CrearEncuestaFragment
import es.kab.torneox.Admin.Fragments.CrearTorneosFragment
import es.kab.torneox.Admin.Fragments.HistorialFragment
import es.kab.torneox.Admin.Fragments.ListaUsersFragment
import es.kab.torneox.Client.Fragments.EncuestaFragment
import es.kab.torneox.Client.Fragments.InicioFragment
import es.kab.torneox.Client.Fragments.PerfilFragment
import es.kab.torneox.Client.Fragments.RankingFragment
import es.kab.torneox.Comun.TorneosActivosFragment
import es.kab.torneox.Control.AuthManager
import es.kab.torneox.Control.ControlActivity
import es.kab.torneox.R
import es.kab.torneox.databinding.ActivityAdminBinding

class AdminActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {
    private lateinit var binding : ActivityAdminBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.adminBottomNav.setOnItemSelectedListener(this)
        binding.adminBottomNav.selectedItemId = R.id.item_crear
        binding.adminBottomNav.setOnItemSelectedListener(this)
        setSupportActionBar(binding.adminToolBar)
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
            R.id.actionLenguage->{
                TODO()
            }
            else -> {
                false
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.item_crear_encuestas ->{
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<CrearEncuestaFragment>(R.id.AdminFragmentContainer)
                }
                true
            }
            R.id.item_torneos ->{
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<TorneosActivosFragment>(R.id.AdminFragmentContainer)
                }
                true
            }
            R.id.item_crear ->{
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<CrearTorneosFragment>(R.id.AdminFragmentContainer)
                }
                true
            }
            R.id.item_lista ->{
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<ListaUsersFragment>(R.id.AdminFragmentContainer)
                }
                true
            }
            R.id.item_historial ->{
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    replace<HistorialFragment>(R.id.AdminFragmentContainer)
                }
                true
            }
            else -> false
        }
    }
}