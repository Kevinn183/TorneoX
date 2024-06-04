package es.kab.torneox.Client.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener
import es.kab.torneox.Adapters.UsersAdapter
import es.kab.torneox.Firebase.FirestoreManager
import es.kab.torneox.R
import es.kab.torneox.databinding.FragmentRankingBinding
import kotlinx.coroutines.launch

class RankingFragment : Fragment() {
    private lateinit var binding: FragmentRankingBinding
    private lateinit var firestoreManager: FirestoreManager
    private lateinit var usersAdapter: UsersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRankingBinding.inflate(inflater)

        firestoreManager = FirestoreManager()
        setUpSpinner()
        setUpRecycler("victoria")

        binding.spinner2.onItemSelectedListener = object :OnItemSelectedListener,
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                setUpRecycler( binding.spinner2.selectedItem.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                return true
            }
        }


        return binding.root
    }

    fun setUpRecycler(orden:String){
        lifecycleScope.launch {
            val usus = firestoreManager.getUsers(orden, 10)
            Log.i("usuaris", usus?.size.toString())
            usersAdapter = usus?.let { UsersAdapter(it, requireContext()) }!!
            binding.recView.adapter = usersAdapter
            binding.recView.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    fun setUpSpinner(){

        val arrayAdapter = ArrayAdapter<String>(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, resources.getStringArray(R.array.spinner_orden))
        binding.spinner2.adapter = arrayAdapter

    }


}