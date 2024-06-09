package es.kab.torneox.Admin.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import es.kab.torneox.Adapters.TorneosAdminAdapter
import es.kab.torneox.Firebase.FirestoreManager
import es.kab.torneox.databinding.FragmentHistorialBinding
import kotlinx.coroutines.launch


class HistorialFragment : Fragment() {


    private lateinit var binding: FragmentHistorialBinding
    private lateinit var firestoreManager: FirestoreManager
    private lateinit var adminAdapter: TorneosAdminAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistorialBinding.inflate(inflater)
        firestoreManager = FirestoreManager()
        setUpRecycler()
        return binding.root
    }


    fun setUpRecycler(){
        lifecycleScope.launch {
            val torneos = firestoreManager.getTorneos()
                adminAdapter = torneos?.let { TorneosAdminAdapter(it.toMutableList(),requireContext(), this@HistorialFragment) }!!
                binding.recyclerViewHistorial.adapter = adminAdapter
                binding.recyclerViewHistorial.layoutManager = LinearLayoutManager(requireContext())

        }
    }

}