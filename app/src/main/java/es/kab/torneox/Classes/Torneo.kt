package es.kab.torneox.Classes

import com.google.type.DateTime
import com.google.firebase.Timestamp

data class Torneo(
    var nombre: String = "",
    var estado: String = "",
    var tipo: String = "",
    var fecha_creacion: Timestamp? = null,
    var fecha_inicio: Timestamp? = null,
    var numero_participantes: Int = 0,
    var participantes: List<String> = emptyList(),
    var limite: Int = 0
)
