package es.kab.torneox.Admin.Fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Timestamp
import es.kab.torneox.Classes.Torneo
import es.kab.torneox.Classes.User
import es.kab.torneox.Firebase.FirestoreManager
import es.kab.torneox.R
import es.kab.torneox.databinding.FragmentCrearTorneosBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.util.Calendar
import java.util.Date
import java.util.Locale


class CrearTorneosFragment : Fragment() {
    private lateinit var binding: FragmentCrearTorneosBinding
    private lateinit var firestoreManager: FirestoreManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var fechaSeleccionada: Date? = null
        var horaSeleccionada: Date? = null
        firestoreManager = FirestoreManager()
        var timestampInicio = Timestamp.now()
        binding = FragmentCrearTorneosBinding.inflate(inflater)
        binding.btnCrearTorneo.setOnClickListener {
            val nombre = binding.editTextNombreTorneo.text.toString()
            val tipo = binding.editTextTipoTorneo.text.toString()
            var limite = binding.editTextLimiteParticipantes.text.toString()
            val fecha = binding.editTextFechaInicio.text.toString()
            if (limite.isNullOrBlank()){
                limite = "999"
            }
            if (compruebaDatos(nombre,tipo,fecha, timestampInicio)){
                lifecycleScope.launch(Dispatchers.IO) {
                    createTorneo(nombre,tipo,timestampInicio,limite.toInt())
                }
            }

        }
        binding.editTextFechaInicio.setOnClickListener {

            val datePicker = DatePickerDialog(requireContext(), { _, year, month, day ->
                val calendar = Calendar.getInstance()
                calendar.set(year, month, day)
                fechaSeleccionada = calendar.time

                val timePicker = TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    horaSeleccionada = calendar.time

                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, day)
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
                    val fechaHoraInicio = calendar.time

                    timestampInicio = Timestamp(fechaHoraInicio)

                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                    val fechaHoraTexto = sdf.format(fechaHoraInicio)
                    binding.editTextFechaInicio.setText(fechaHoraTexto)

                }, 0, 0, true)

                timePicker.show()
            }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH))

            datePicker.show()
        }
        return binding.root
    }




    fun compruebaDatos(nombre:String, tipo:String, fecha:String, fechaSeleccionada: Timestamp): Boolean {
        if (nombre.isNullOrBlank() || tipo.isNullOrBlank() || fecha.isNullOrBlank()){
            Toast.makeText(context, getString(R.string.casilla_vacia), Toast.LENGTH_SHORT).show()
            return false
        }
        if (tipo.lowercase() != "deporte" && tipo.lowercase() != "juegos de mesa"){
            Toast.makeText(context, getString(R.string.tipo_novalido), Toast.LENGTH_SHORT).show()
            return false
        }
        if (fechaSeleccionada.seconds <= Timestamp.now().seconds){
            Toast.makeText(context, getString(R.string.fecha_no), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun createTorneo(nombre:String, tipo:String, fecha: Timestamp, limite:Int){
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val newTorneo = Torneo(estado = "activo", fecha_creacion = Timestamp.now(), fecha_inicio = fecha, limite =limite, nombre =nombre, tipo = tipo)
                firestoreManager.addTorneo(newTorneo)
                binding.editTextNombreTorneo.setText("")
                binding.editTextTipoTorneo.setText("")
                binding.editTextLimiteParticipantes.setText("")
                binding.editTextFechaInicio.setText("")
            }catch (e:Exception){
                Log.e("torneito", "Error al añadir torneo: ${e.message}", e)
            }
        }

    }


}