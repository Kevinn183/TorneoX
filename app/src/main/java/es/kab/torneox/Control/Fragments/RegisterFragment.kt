package es.kab.torneox.Control.Fragments

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import es.kab.torneox.Classes.User
import es.kab.torneox.Control.AuthManager
import es.kab.torneox.Firebase.FirestoreManager
import es.kab.torneox.R
import es.kab.torneox.databinding.FragmentRegisterBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Calendar

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private var mListener : RegisterFragmentListener? = null
    private lateinit var authManager:AuthManager


    private val firestoreManager: FirestoreManager by lazy { FirestoreManager() }

    @RequiresApi(Build.VERSION_CODES.O)
    val formatter = DateTimeFormatter.ofPattern("[d/M/yyyy][dd/MM/yyyy]")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        authManager = AuthManager()
        if (context is RegisterFragmentListener){
            mListener = context
        }
        else{
            throw Exception("The activity must implement the interface RegisterFragmentListener")
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater,container,false)
        binding.registerBtn.setOnClickListener {
            val email = binding.regName.editText?.text.toString()
            val pass = binding.regPass.editText?.text.toString()
            val repass = binding.regPassConfirm.editText?.text.toString()
            val fecha_nac = binding.fechaNac.editText?.text.toString()


            if (compruebaDatos(email,pass,repass,fecha_nac)){
                lifecycleScope.launch(Dispatchers.IO){
                    val userLogged = authManager.singin(email,pass)
                    withContext(Dispatchers.Main){
                        if (userLogged != null){
                            Toast.makeText(requireContext(), userLogged.email, Toast.LENGTH_SHORT).show()
                            //val dialogFragment = ControlDialog.newInstance(email,pass)
                            //dialogFragment.show(requireActivity().supportFragmentManager, "LOGIN")
                            createUser(email, pass)
                            mListener?.onRegButtonCLicked()
                        }else{
                            Toast.makeText(requireContext(),getString(R.string.cuenta_existente), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        //binding.fechaNac.isEnabled = false
        binding.fechaNac.editText?.setOnClickListener {
            val calendario = Calendar.getInstance()
            val dia = calendario.get(Calendar.DAY_OF_MONTH)
            val mes = calendario.get(Calendar.MONTH)
            val año = calendario.get(Calendar.YEAR)

            val datePickerDialog = DatePickerDialog(this.requireContext(), { _, i, i1, i2 ->
                val m = i1 + 1
                val fechaaux = "$i2/$m/$i"

                binding.fechaNac.editText?.setText(fechaaux)
            }, año, mes, dia)

            datePickerDialog.show()
            //binding.fechaNac.isEnabled = false
        }


        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun compruebaDatos(email:String, pass:String, repass:String, fecha:String): Boolean {
        if (email.isNullOrBlank() || pass.isNullOrBlank() || repass.isNullOrBlank() || fecha.isNullOrBlank()){
            Toast.makeText(context, getString(R.string.casilla_vacia), Toast.LENGTH_SHORT).show()
            return false
        }
        val fechaActual = LocalDate.parse(fecha, formatter)
        if (!email.matches(Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"))){
            Toast.makeText(context, getString(R.string.correo_invalido), Toast.LENGTH_SHORT).show()
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
        if (pass != repass){
            Toast.makeText(context, getString(R.string.contrasenya_diferente), Toast.LENGTH_SHORT).show()
            return false
        }
        if (Period.between(fechaActual, LocalDate.now()).years < 14){
            Toast.makeText(context, getString(R.string.edad_insuficiente), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
    private fun createUser(correo:String, pass:String){
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val newUser = User(correo = correo, contraseña = pass, imagen = "avatar_prede")
                firestoreManager.addUser(newUser)
            }catch (e:Exception){
                e.toString()
            }
        }

    }

    interface RegisterFragmentListener{
        fun onRegButtonCLicked()
        fun<T> replace()
    }
}