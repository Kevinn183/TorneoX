package es.kab.torneox.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.kab.torneox.Classes.User
import es.kab.torneox.R

class UsersAdapter(private var usuarios: List<User>,private val context: Context) : RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(email: String)
    }
    private var clickListener: OnItemClickListener? = null
    private var listaFiltrada: List<User> = usuarios
    fun setOnItemClickListener(listener: OnItemClickListener) {
        clickListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.user_recycler, parent, false)
        return UsersViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listaFiltrada.size
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val usu = listaFiltrada[position]
        holder.bindItem(usu, context)
        holder.itemView.setOnClickListener {
            clickListener?.onItemClick(usu.correo)
        }
    }
    class UsersViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val foto: ImageView = view.findViewById(R.id.user_recycler_image)
        private val nom: TextView = view.findViewById(R.id.user_recycler_name)
        private val num_vic: TextView = view.findViewById(R.id.user_recycler_wins)
        private val num_par: TextView = view.findViewById(R.id.user_recycler_part)
        fun bindItem(user: User, context: Context){
            val img = context.resources.getIdentifier(user.imagen, "drawable", context.packageName)
            foto.setImageResource(img)
            nom.text = user.nombre
            val str = context.getString(R.string.num_vic_rank) + " " + user.victorias
            num_vic.text = str
            val str2 = context.getString(R.string.num_part_rank) + " " + user.participaciones
            num_par.text = str2


        }
    }
    fun filter(query: String) {
        listaFiltrada = if (query.isEmpty()) {
            usuarios
        } else {
            usuarios.filter { it.nombre?.startsWith(query, ignoreCase = true) == true }
        }
        notifyDataSetChanged()
    }
}