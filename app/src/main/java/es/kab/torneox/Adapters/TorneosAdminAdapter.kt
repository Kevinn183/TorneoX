package es.kab.torneox.Adapters

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import es.kab.torneox.Classes.Torneo
import es.kab.torneox.Firebase.FirestoreManager
import es.kab.torneox.R
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class TorneosAdminAdapter (
    private var torneos: MutableList<Torneo>,
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<TorneosAdminAdapter.TorneosAdminViewHolder>() {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    private lateinit var firestoreManager: FirestoreManager
    private lateinit var adapter: TorneosAdminAdapter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TorneosAdminViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.torneo_admin_recycler, parent, false)
        return TorneosAdminViewHolder(view)
    }

    override fun getItemCount(): Int {
        return torneos.size
    }

    override fun onBindViewHolder(holder: TorneosAdminViewHolder, position: Int) {
        val torneo = torneos[position]
        holder.bindItem(torneo)
    }

    inner class TorneosAdminViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val cardView: CardView = view.findViewById(R.id.torneo_admin_recycler_cardView)
        private val foto: ImageView = view.findViewById(R.id.torneo_admin_recycler_image)
        private val nom: TextView = view.findViewById(R.id.torneo_admin_recycler_name)
        private val fecha: TextView = view.findViewById(R.id.torneo_admin_recycler_fecha)
        private val estado: TextView = view.findViewById(R.id.torneo_admin_recycler_estado)
        private val parti: TextView = view.findViewById(R.id.torneo_admin_recycler_participantes)

        fun bindItem(torneo: Torneo) {
            firestoreManager = FirestoreManager()
            if (torneo.tipo.lowercase() == "deporte") {
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.deporte))
                foto.setImageResource(R.drawable.deportes)
            } else {
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.mesa))
                foto.setImageResource(R.drawable.carta)
            }
            nom.text = torneo.nombre
            val formattedDate = torneo.fecha_inicio?.toDate()?.let { dateFormat.format(it) }
            fecha.text =formattedDate.toString()
            val str =torneo.numero_participantes
            parti.text = str.toString()
            estado.text = torneo.estado.uppercase()

            cardView.setOnClickListener {
                try {
                    if(torneo.ganador.equals("")){
                        val builder = AlertDialog.Builder(context)
                        val inflater = LayoutInflater.from(context)
                        val dialogView = inflater.inflate(R.layout.dialog_cerrar_torneo, null)
                        builder.setView(dialogView)
                        val alertDialog = builder.create()
                        val spinner : Spinner= dialogView.findViewById(R.id.spinner_winner)
                        val arrayAdapter = ArrayAdapter<String>(context, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, torneo.participantes)
                        spinner.adapter = arrayAdapter
                        var usu = spinner.selectedItem.toString()

                        val btnOk: Button = dialogView.findViewById(R.id.button_close)
                        btnOk.setOnClickListener {
                            if (!usu.isNullOrBlank()){
                                lifecycleOwner.lifecycleScope.launch {
                                    firestoreManager.sumarVictoria(usu)
                                    firestoreManager.cambiarEstado(torneo.nombre, "cerrado")
                                    firestoreManager.addGanador(torneo.nombre, usu)
                                    torneos.clear()
                                    firestoreManager.getTorneos()
                                        ?.let { it1 -> torneos.addAll(it1) }
                                    notifyDataSetChanged()

                                    alertDialog.dismiss()
                                }

                            }
                        }
                        alertDialog.show()
                    }else{
                        val builder = AlertDialog.Builder(context)
                        val inflater = LayoutInflater.from(context)
                        val dialogView = inflater.inflate(R.layout.dialog_ver_ganador, null)
                        builder.setView(dialogView)
                        val alertDialog = builder.create()
                        val nom : TextView = dialogView.findViewById(R.id.winner_name)
                        nom.text = torneo.ganador
                        val btnOk: Button = dialogView.findViewById(R.id.button_winner)
                        btnOk.setOnClickListener {
                            alertDialog.dismiss()
                        }
                        alertDialog.show()
                    }





                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }


    }
}