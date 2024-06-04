package es.kab.torneox.Control.Fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import es.kab.torneox.Control.AuthManager
import es.kab.torneox.R
import es.kab.torneox.databinding.FragmentLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : Fragment() {
    private var mListener : LoginFragmentListener? = null
    private lateinit var binding : FragmentLoginBinding
    private lateinit var authManager: AuthManager
    private lateinit var sharedPreferences: SharedPreferences

    override fun onAttach(context: Context) {
        super.onAttach(context)
        authManager = AuthManager()
        if (context is LoginFragmentListener){
            mListener = context
        }
        else{
            throw Exception("The activity must implement the interface LoginFragmentListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.loginBtn.setOnClickListener {


            val email = binding.usernameLog.editText?.text.toString()
            val pass = binding.passwordLog.editText?.text.toString()
            if(compruebaDatos(email,pass)){
                lifecycleScope.launch(Dispatchers.IO) {
                    val userLogged = authManager.login(email,pass)
                    withContext(Dispatchers.Main){
                        if (userLogged != null){
                            sharedPreferences.edit().putString("email", email).apply()
                            sharedPreferences.edit().putString("pass", pass).apply()
                            mListener?.onLgnButtonCLicked()
                        }else{
                            Toast.makeText(context, getString(R.string.no_usuario), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }



        binding.noAccount.setOnClickListener {
            mListener?.onRegisterButtonCLicked()
        }
        binding.noPassword.setOnClickListener{
            mListener?.onTextRecoverCLicked()
        }

        return binding.root
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface LoginFragmentListener{
        fun onLgnButtonCLicked()
        fun onTextRecoverCLicked()
        fun onRegisterButtonCLicked()
        fun<T> replace()
    }

    fun compruebaDatos(email:String, pass:String): Boolean{
        if (email.isNullOrBlank() || pass.isNullOrBlank()){
            Toast.makeText(context, getString(R.string.casilla_vacia), Toast.LENGTH_SHORT).show()

            return false
        }
        return true
    }


}