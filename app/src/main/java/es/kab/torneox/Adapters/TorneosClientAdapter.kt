package es.kab.torneox.Adapters

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import es.kab.torneox.Classes.Torneo
import es.kab.torneox.R
import java.text.SimpleDateFormat
import java.util.Locale

class TorneosClientAdapter (
    private var torneos: List<Torneo>,
    private val context: Context,
    private val user: String) : RecyclerView.Adapter<TorneosClientAdapter.TorneosClientViewHolder>() {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TorneosClientViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.torneo_client_recycler, parent, false)
            return TorneosClientViewHolder(view)
        }

        override fun getItemCount(): Int {
            return torneos.size
        }

        override fun onBindViewHolder(holder: TorneosClientViewHolder, position: Int) {
            val torneo = torneos[position]
            holder.bindItem(torneo)
        }

        inner class TorneosClientViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val cardView: CardView = view.findViewById(R.id.torneo_client_recycler_cardView)
            private val foto: ImageView = view.findViewById(R.id.torneo_client_recycler_image)
            private val nom: TextView = view.findViewById(R.id.torneo_client_recycler_name)
            private val fecha: TextView = view.findViewById(R.id.torneo_client_recycler_fecha)
            private val imgQr: ImageView = view.findViewById(R.id.torneo_client_recycler_qr)

            fun bindItem(torneo: Torneo) {
                if (torneo.tipo.lowercase() == "deporte") {
                    cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.deporte))
                    foto.setImageResource(R.drawable.deportes)
                } else {
                    cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.mesa))
                    foto.setImageResource(R.drawable.carta)
                }
                imgQr.setImageResource(R.drawable.qr)
                nom.text = torneo.nombre
                val formattedDate = torneo.fecha_inicio?.toDate()?.let { dateFormat.format(it) }
                fecha.text =formattedDate.toString()
                imgQr.setOnClickListener {
                    try {
                        var barcodeEncoder: BarcodeEncoder = BarcodeEncoder()
                        var bitmap: Bitmap = barcodeEncoder.encodeBitmap(
                            "t: "+torneo.nombre + "  u: "+user,
                            BarcodeFormat.QR_CODE,
                            750,750
                        )

                        val builder = AlertDialog.Builder(context)
                        val inflater = LayoutInflater.from(context)
                        val dialogView = inflater.inflate(R.layout.dialog_qr, null)
                        builder.setView(dialogView)
                        val alertDialog = builder.create()
                        val img :ImageView = dialogView.findViewById(R.id.codigo_img)
                        val btnOk: Button = dialogView.findViewById(R.id.button_ok)
                        img.setImageBitmap(bitmap)
                        btnOk.setOnClickListener {
                            alertDialog.dismiss()
                        }
                        alertDialog.show()


                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }
        }


        fun getTorneos(): List<Torneo> {
            return torneos
        }

}