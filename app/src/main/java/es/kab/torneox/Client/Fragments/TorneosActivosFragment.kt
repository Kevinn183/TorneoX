package es.kab.torneox.Client.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import es.kab.torneox.Adapters.TorneosClientAdapter
import es.kab.torneox.Adapters.TorneosInicioAdapter
import es.kab.torneox.Firebase.FirestoreManager
import es.kab.torneox.databinding.FragmentTorneosActivosBinding
import kotlinx.coroutines.launch

class TorneosActivosFragment : Fragment() {

    private lateinit var binding: FragmentTorneosActivosBinding
    private lateinit var firestoreManager: FirestoreManager
    private lateinit var clientAdapter: TorneosClientAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTorneosActivosBinding.inflate(inflater)
        firestoreManager = FirestoreManager()
        setUpRecycler()
        return binding.root
    }


    fun setUpRecycler(){
        lifecycleScope.launch {
            val nomUser = firestoreManager.getUserName()
            val torneos = firestoreManager.getTorneosClient(nomUser)
            if (torneos.isNullOrEmpty()) {

                binding.recyclerViewClient.visibility = View.GONE
                binding.emptyTextView.visibility = View.VISIBLE
            } else {
                binding.recyclerViewClient.visibility = View.VISIBLE
                binding.emptyTextView.visibility = View.GONE
                clientAdapter = TorneosClientAdapter(torneos,requireContext(),nomUser)
                binding.recyclerViewClient.adapter = clientAdapter
                binding.recyclerViewClient.layoutManager = LinearLayoutManager(requireContext())
            }

        }
    }
}