package es.kab.torneox.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import es.kab.torneox.Classes.Torneo
import es.kab.torneox.R

class TorneosInicioAdapter(
    private var torneos: List<Torneo>,
    private val context: Context,
    private val listener: OnUnirseClickListener
) : RecyclerView.Adapter<TorneosInicioAdapter.TorneosInicioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TorneosInicioViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.torneo_inicio_recycler, parent, false)
        return TorneosInicioViewHolder(view)
    }

    override fun getItemCount(): Int {
        return torneos.size
    }

    override fun onBindViewHolder(holder: TorneosInicioViewHolder, position: Int) {
        val torneo = torneos[position]
        holder.bindItem(torneo)
    }

    inner class TorneosInicioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val cardView: CardView = view.findViewById(R.id.inicio_recycler_card)
        private val foto: ImageView = view.findViewById(R.id.inicio_recycler_imagen)
        private val nom: TextView = view.findViewById(R.id.inicio_recycler_nombre)
        private val tipo: TextView = view.findViewById(R.id.inicio_recycler_tipo)
        private val fecha: TextView = view.findViewById(R.id.inicio_recycler_fecha)
        private val parti: TextView = view.findViewById(R.id.inicio_recycler_participantes)
        private val lim: TextView = view.findViewById(R.id.inicio_recycler_limite)
        private val unirseButton: Button = view.findViewById(R.id.inicio_recycler_boton)

        fun bindItem(torneo: Torneo) {
            if (torneo.tipo.lowercase() == "deporte") {
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.deporte))
                foto.setImageResource(R.drawable.deportes)
            } else {
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.mesa))
                foto.setImageResource(R.drawable.carta)
            }
            nom.text = torneo.nombre
            tipo.text = torneo.tipo
            fecha.text = torneo.fecha_inicio?.toDate().toString()
            val str = context.getString(R.string.inicio_parti) + " " + torneo.numero_participantes
            parti.text = str
            val str2 = context.getString(R.string.inicio_limite) + " " + torneo.limite
            lim.text = str2

            unirseButton.setOnClickListener {
                listener.onUnirseClick(adapterPosition)
            }
        }
    }

    interface OnUnirseClickListener {
        fun onUnirseClick(position: Int)
    }
    fun getTorneos(): List<Torneo> {
        return torneos
    }
}
