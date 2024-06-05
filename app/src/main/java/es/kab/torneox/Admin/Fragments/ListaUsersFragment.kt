package es.kab.torneox.Admin.Fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import es.kab.torneox.Adapters.UsersAdapter
import es.kab.torneox.Firebase.FirestoreManager
import android.text.TextWatcher

import es.kab.torneox.R
import es.kab.torneox.databinding.FragmentListaUsersBinding
import kotlinx.coroutines.launch

class ListaUsersFragment : Fragment() {
    private lateinit var binding: FragmentListaUsersBinding
    private lateinit var firestoreManager: FirestoreManager
    private lateinit var usersAdapter: UsersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListaUsersBinding.inflate(inflater)


        firestoreManager = FirestoreManager()
        setUpRecycler("nombre")

        binding.editTextText.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                usersAdapter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        return binding.root
    }

    fun setUpRecycler(orden:String){
        lifecycleScope.launch {
            val usus = firestoreManager.getUsers(orden, null)
            usersAdapter = usus?.let { UsersAdapter(it, requireContext()) }!!
            usersAdapter.setOnItemClickListener(object : UsersAdapter.OnItemClickListener{
                override fun onItemClick(email: String) {
                    val correu = Intent(Intent.ACTION_SENDTO)
                    correu.data = Uri.parse("mailto:")
                    correu.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(email))
                    startActivity(Intent.createChooser(correu, "Seleccionar cliente de correo"))
                }
            })

            binding.recViewLista.adapter = usersAdapter
            binding.recViewLista.layoutManager = LinearLayoutManager(requireContext())
        }
    }
}