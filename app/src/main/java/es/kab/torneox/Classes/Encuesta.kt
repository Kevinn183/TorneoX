package es.kab.torneox.Classes

import com.google.firebase.Timestamp

data class Encuesta (
    var estado: String = "",
    var ganador: String? = "",
    var opcion1: String = "",
    var opcion2: String = "",
    var opcion3: String = "",
    var titulo: String = "",
    var usuarios_voto: List<String> = emptyList(),
    var votos1: Int = 0,
    var votos2: Int = 0,
    var votos3: Int = 0
    )