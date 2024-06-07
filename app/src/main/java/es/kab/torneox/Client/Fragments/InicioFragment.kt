package es.kab.torneox.Client.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import es.kab.torneox.Adapters.TorneosInicioAdapter
import es.kab.torneox.Adapters.UsersAdapter
import es.kab.torneox.Firebase.FirestoreManager
import es.kab.torneox.R
import es.kab.torneox.databinding.FragmentInicioBinding
import kotlinx.coroutines.launch


class InicioFragment : Fragment(),TorneosInicioAdapter.OnUnirseClickListener {
    private lateinit var binding: FragmentInicioBinding
    private lateinit var firestoreManager: FirestoreManager
    private lateinit var inicioAdapter: TorneosInicioAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInicioBinding.inflate(inflater)
        firestoreManager = FirestoreManager()
        setUpRecycler()
        return binding.root
    }


    fun setUpRecycler(){
        lifecycleScope.launch {
            val torneos = firestoreManager.getTorneosInicio(firestoreManager.getUserName())
            Log.i("fea",torneos?.size.toString())
            if (torneos.isNullOrEmpty()) {

                binding.recyclerView.visibility = View.GONE
                binding.emptyTextView.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                binding.emptyTextView.visibility = View.GONE
                inicioAdapter = TorneosInicioAdapter(torneos,requireContext(), this@InicioFragment)
                binding.recyclerView.adapter = inicioAdapter
                binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            }

        }
    }

    override fun onUnirseClick(position: Int) {
        lifecycleScope.launch{
            firestoreManager.meterParticipante(inicioAdapter.getTorneos()[position].nombre, firestoreManager.getUserName())
            firestoreManager.sumarParticipante(inicioAdapter.getTorneos()[position].nombre)
            setUpRecycler()
        }
    }

}