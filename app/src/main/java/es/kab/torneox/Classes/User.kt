package es.kab.torneox.Classes

import com.google.firebase.Timestamp

data class User(
    var correo: String = "",
    var contraseña: String = "",
    var nombre: String? = "",
    var participaciones: Int = 0,
    var victorias: Int = 0
)
