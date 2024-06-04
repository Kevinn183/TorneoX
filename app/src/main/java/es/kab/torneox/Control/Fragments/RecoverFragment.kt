package es.kab.torneox.Control.Fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import es.kab.torneox.Control.AuthManager
import es.kab.torneox.R
import es.kab.torneox.databinding.FragmentRecoverBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RecoverFragment : Fragment() {

    private lateinit var binding: FragmentRecoverBinding
    private lateinit var mListener: RecoverFragmentListener
    private lateinit var authManager: AuthManager


    override fun onAttach(context: Context) {
        super.onAttach(context)

        authManager = AuthManager()
        if(context is RecoverFragmentListener){
            mListener = context
        }else{
            throw Exception("Your fragment or activity must implement the interface ResetPasswordListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRecoverBinding.inflate(inflater,container,false)
        binding.buttonChange.setOnClickListener{
            val email = binding.userText.editText?.text.toString()
            if (!email.isNullOrBlank()){
                lifecycleScope.launch(Dispatchers.IO) {
                    authManager.resetPass(email)
                    withContext(Dispatchers.Main){
                        crearDialog()
                        mListener.onRcoverButtonCLicked()
                    }
                }
            }
        }
        return binding.root
    }

    interface RecoverFragmentListener{
        fun onRcoverButtonCLicked()
    }

    fun crearDialog(){
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_check_email, null)
        builder.setView(dialogView)
        val alertDialog = builder.create()
        val btnOk: Button = dialogView.findViewById(R.id.button_recover)
        btnOk.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.show()

    }


}