package es.kab.torneox.Client.Fragments

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import es.kab.torneox.Firebase.FirestoreManager
import es.kab.torneox.R
import es.kab.torneox.databinding.FragmentEncuestaBinding
import kotlinx.coroutines.launch


class EncuestaFragment : Fragment() {
        private lateinit var binding: FragmentEncuestaBinding
        private lateinit var firestoreManager: FirestoreManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEncuestaBinding.inflate(inflater)
        firestoreManager = FirestoreManager()
        comprovarExiste()
        comprovarVotado()

        binding.btnOp1.setOnClickListener {
            lifecycleScope.launch {
                firestoreManager.sumarVoto("v1")
                firestoreManager.meterVotante(firestoreManager.getUserName())
                comprovarVotado()
            }
        }

        binding.btnOp2.setOnClickListener {
            lifecycleScope.launch {
                firestoreManager.sumarVoto("v2")
                firestoreManager.meterVotante(firestoreManager.getUserName())
                comprovarVotado()
            }
        }

        binding.btnOp3.setOnClickListener {
            lifecycleScope.launch {
                firestoreManager.sumarVoto("v3")
                firestoreManager.meterVotante(firestoreManager.getUserName())
                comprovarVotado()
            }
        }

        return binding.root
    }


    fun comprovarExiste(){
        lifecycleScope.launch {
            var encuesta = firestoreManager.getEncuestaData()
            if (encuesta != null){
                binding.titEnc.text = encuesta.titulo
                binding.op1.text = encuesta.opcion1
                binding.op2.text = encuesta.opcion2
                binding.op3.text = encuesta.opcion3

                binding.btnOp1.isEnabled = true
                binding.btnOp2.isEnabled = true
                binding.btnOp3.isEnabled = true


            }else{
                binding.titEnc.text = getString(R.string.tit_enc)
                binding.op1.text = getString(R.string.op1)
                binding.op2.text = getString(R.string.op2)
                binding.op3.text = getString(R.string.op3)

                binding.btnOp1.isEnabled = false
                binding.btnOp2.isEnabled = false
                binding.btnOp3.isEnabled = false
            }
        }
    }
    fun comprovarVotado(){
        lifecycleScope.launch {
            if (firestoreManager.usuarioVotado(firestoreManager.getUserName())){

                binding.btnOp1.isEnabled = false
                binding.btnOp2.isEnabled = false
                binding.btnOp3.isEnabled = false
                binding.textView2.text = getString(R.string.ya_vot)


            }else{


                binding.btnOp1.isEnabled = true
                binding.btnOp2.isEnabled = true
                binding.btnOp3.isEnabled = true
                binding.textView2.text = ""
            }
        }
    }


}