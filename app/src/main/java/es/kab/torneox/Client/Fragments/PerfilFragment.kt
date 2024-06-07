package es.kab.torneox.Client.Fragments

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import es.kab.torneox.Classes.User
import es.kab.torneox.Control.AuthManager
import es.kab.torneox.Control.ControlActivity
import es.kab.torneox.Firebase.FirestoreManager
import es.kab.torneox.R
import es.kab.torneox.databinding.FragmentPerfilBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.Period


class PerfilFragment : Fragment() {
    private lateinit var binding: FragmentPerfilBinding
    private lateinit var firestoreManager: FirestoreManager
    private lateinit var authManager: AuthManager
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPerfilBinding.inflate(inflater)
        firestoreManager = FirestoreManager()
        authManager = AuthManager()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        montarPerfil()

        binding.fotoPerfil.setOnClickListener {
            cambiarFoto()
        }
        binding.nomPerfil.setOnClickListener {
            cambiarNombre()
        }
        binding.fotoCambioPass.setOnClickListener {
            cambiarContraseña()
        }
        binding.botonLogout.setOnClickListener {
            AuthManager().logOut()
            sharedPreferences.edit().clear().apply()
            val intent = Intent(requireActivity(), ControlActivity::class.java)
            startActivity(intent)

        }

        return binding.root
    }

    fun montarPerfil(){
        var nom = ""
        var foto = ""
        var part = getString(R.string.num_part)
        var pass = ""
        var corr = getString(R.string.corr_per)
        var vict = getString(R.string.num_vic)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val user = firestoreManager.getUserData()
                if (user != null){
                    nom = user["nombre"] as? String ?: ""
                    part += user["participaciones"] as? Number?: 0
                    vict += (" " + user["victorias"] as? Number) ?: 0
                    corr += (" " + user["correo"] as? String) ?: ""
                    pass = (" " + user["contraseña"] as? String) ?: ""
                    foto = user["imagen"] as? String ?: ""

                }

            }catch (e:Exception){
                e.toString()
            }
            withContext(Dispatchers.Main){
                binding.nomPerfil.text = nom
                binding.numPart.text = part
                val img = resources.getIdentifier(foto, "drawable", requireContext().packageName)
                binding.fotoPerfil.setImageResource(img)
                binding.numVic.text = vict
                binding.TextCorreo.text = corr
                binding.editTextPassword.setText(pass)
                binding.editTextPassword.isEnabled = false
            }
        }

    }



    fun cambiarFoto() {
        val builder = AlertDialog.Builder(requireContext())
        val dialogView = layoutInflater.inflate(R.layout.dialog_image_profile, null)
        builder.setView(dialogView)
        val dialog = builder.create()
        dialog.show()

        dialogView.findViewById<View>(R.id.dialog_image1).setOnClickListener {
            onImageClicked(it)
            dialog.dismiss()
        }
        dialogView.findViewById<View>(R.id.dialog_image2).setOnClickListener {
            onImageClicked(it)
            dialog.dismiss()
        }
        dialogView.findViewById<View>(R.id.dialog_image3).setOnClickListener {
            onImageClicked(it)
            dialog.dismiss()
        }
        dialogView.findViewById<View>(R.id.dialog_image4).setOnClickListener {
            onImageClicked(it)
            dialog.dismiss()
        }
        dialogView.findViewById<View>(R.id.dialog_image5).setOnClickListener {
            onImageClicked(it)
            dialog.dismiss()
        }
        dialogView.findViewById<View>(R.id.dialog_image6).setOnClickListener {
            onImageClicked(it)
            dialog.dismiss()
        }



    }

    private fun onImageClicked(view: View) {
        val imageName = view.tag.toString()
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                firestoreManager.changeUserImage(imageName)
                montarPerfil()
            }catch (e:Exception){
                e.toString()
            }
        }
    }

    fun cambiarNombre(){
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_new_name, null)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        val editName: EditText = dialogView.findViewById(R.id.newName_editText)
        val btnOk: Button = dialogView.findViewById(R.id.newName_button)
        btnOk.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    firestoreManager.changeUserName(editName.text.toString())
                    alertDialog.dismiss()
                    montarPerfil()
                }catch (e:Exception){
                    e.toString()
                }
            }
        }
        alertDialog.show()
    }

    fun cambiarContraseña(){
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_new_pass, null)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        val editPass: EditText = dialogView.findViewById(R.id.newPass_editText)
        val btnOk: Button = dialogView.findViewById(R.id.newPass_button)
        btnOk.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    if (compruebaContrasenya(editPass.text.toString())){
                        firestoreManager.changeUserPassword(editPass.text.toString())
                        authManager.changePass(editPass.text.toString())
                        alertDialog.dismiss()
                        montarPerfil()
                        withContext(Dispatchers.Main){
                            sharedPreferences.edit().putString("pass", editPass.text.toString()).apply()
                        }
                    }
                }catch (e:Exception){
                    e.toString()
                }
            }
        }
        alertDialog.show()
    }


    fun compruebaContrasenya(pass:String): Boolean {
        if (pass.isNullOrBlank()){
            Toast.makeText(context, getString(R.string.casilla_vacia), Toast.LENGTH_SHORT).show()
            return false
        }
        if (pass.length < 8){
            Toast.makeText(context, getString(R.string.contrasenya_corta), Toast.LENGTH_SHORT).show()
            return false
        }
        if (!pass.contains(Regex("[0-9]")) || !pass.contains(Regex("[a-zA-Z]"))){
            Toast.makeText(context, getString(R.string.contrasenya_insegura), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }


}