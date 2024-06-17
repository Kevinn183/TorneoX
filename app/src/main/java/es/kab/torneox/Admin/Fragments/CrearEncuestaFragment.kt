package es.kab.torneox.Admin.Fragments

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import es.kab.torneox.Classes.Encuesta
import es.kab.torneox.Firebase.FirestoreManager
import es.kab.torneox.R
import es.kab.torneox.databinding.FragmentCrearEncuestaBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CrearEncuestaFragment : Fragment() {
    private lateinit var firestoreManager: FirestoreManager
    private lateinit var binding:FragmentCrearEncuestaBinding
    var existe:Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        firestoreManager = FirestoreManager()
        binding = FragmentCrearEncuestaBinding.inflate(inflater)
        comprovarExiste()

        binding.btnCrearEncuesta.setOnClickListener {
            if (condiciones()){
                crearEncuesta()
                comprovarExiste()
            }
        }
        binding.btnCerrarEncuesta.setOnClickListener {
            cerrarEncuesta()
            comprovarExiste()
        }
        return binding.root
    }

    fun comprovarExiste(){
        lifecycleScope.launch {
            var encuesta = firestoreManager.getEncuestaData()
            if (encuesta != null){
                binding.tituloEnc.text = Editable.Factory.getInstance().newEditable(encuesta.titulo)
                binding.etOpcion1.text = Editable.Factory.getInstance().newEditable(encuesta.opcion1)

                binding.etOpcion2.text = Editable.Factory.getInstance().newEditable(encuesta.opcion2)
                binding.etOpcion3.text = Editable.Factory.getInstance().newEditable(encuesta.opcion3)

                binding.tvVotos1.text = getString(R.string.voto) + " " + encuesta.votos1
                binding.tvVotos2.text = getString(R.string.voto) + " " + encuesta.votos2
                binding.tvVotos3.text = getString(R.string.voto) + " " + encuesta.votos3

                binding.btnCerrarEncuesta.isEnabled = true

                binding.btnCrearEncuesta.isEnabled = false
                binding.tituloEnc.isFocusable = false
                binding.etOpcion1.isFocusable = false
                binding.etOpcion2.isFocusable = false
                binding.etOpcion3.isFocusable = false

            }else{

                binding.btnCerrarEncuesta.isEnabled = false
                binding.btnCrearEncuesta.isEnabled = true

                binding.tituloEnc.text = Editable.Factory.getInstance().newEditable("")
                binding.etOpcion1.text = Editable.Factory.getInstance().newEditable("")
                binding.etOpcion2.text = Editable.Factory.getInstance().newEditable("")
                binding.etOpcion3.text =Editable.Factory.getInstance().newEditable("")

                binding.tituloEnc.isFocusable = true
                binding.etOpcion1.isFocusable = true
                binding.etOpcion2.isFocusable = true
                binding.etOpcion3.isFocusable = true
            }
        }
    }

    fun condiciones():Boolean{
        var titulo = binding.tituloEnc.text.toString()
        var op1 = binding.etOpcion1.text.toString()
        var op2 = binding.etOpcion2.text.toString()
        var op3 = binding.etOpcion3.text.toString()

        if (titulo.isNullOrBlank() || op1.isNullOrBlank() || op2.isNullOrBlank() || op3.isNullOrBlank()||
            titulo.length < 3 || op1.length < 3 || op2.length < 3 || op3.length < 3)
            return false
        if (op1.equals(op2) || op1.equals(op3) || op2.equals(op3))
            return false

        return true

    }

    fun crearEncuesta(){
        var encuesta = Encuesta("activo", "",binding.etOpcion1.text.toString(),
            binding.etOpcion2.text.toString(), binding.etOpcion3.text.toString(),
            binding.tituloEnc.text.toString())
        lifecycleScope.launch {
            firestoreManager.addEncuesta(encuesta)
            Toast.makeText(requireContext(), "Creada con exito", Toast.LENGTH_SHORT).show()
        }
    }
    fun cerrarEncuesta(){
        lifecycleScope.launch {
            firestoreManager.cambiarEstadoEncuesta()
            Toast.makeText(requireContext(), "Cerrada con exito", Toast.LENGTH_SHORT).show()
        }
    }

}