package es.kab.torneox.Firebase
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import es.kab.torneox.Classes.Encuesta
import es.kab.torneox.Classes.Torneo
import es.kab.torneox.Classes.User
import es.kab.torneox.Control.AuthManager
import kotlinx.coroutines.tasks.await

class FirestoreManager {
    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private lateinit var authManager: AuthManager


    suspend fun addUser(user: User): Boolean {

        return try {
            firestore.collection("usuarios").add(user).await()
            true
        }catch(e: Exception){
            false
        }
    }
    suspend fun changeUserName(name: String): Boolean {
            authManager = AuthManager()
            var email = authManager.getUserEmail()
            val userId = getUserIdFromEmail(email).await()
            if (userId != null) {
                return try {
                    firestore.collection("usuarios").document(userId).update("nombre", name).await()
                    true
                } catch (e: Exception) {
                    println(e.message)
                    false
                }
            }
        return false
    }
    suspend fun changeUserImage(name: String): Boolean {
        authManager = AuthManager()
        var email = authManager.getUserEmail()
        val userId = getUserIdFromEmail(email).await()
        if (userId != null) {
            return try {
                firestore.collection("usuarios").document(userId).update("imagen", name).await()
                true
            } catch (e: Exception) {
                println(e.message)
                false
            }
        }
        return false
    }
    suspend fun changeUserPassword(name: String): Boolean {
        val authManager = AuthManager()
        var email = authManager.getUserEmail()
        val userId = getUserIdFromEmail(email).await()
        if (userId != null) {
            return try {
                firestore.collection("usuarios").document(userId).update("contraseña", name).await()
                true
            } catch (e: Exception) {
                println(e.message)
                false
            }
        }
        return false
    }

    suspend fun getUserData(): Map<String, Any?>? {
        val authManager = AuthManager()
        val email = authManager.getUserEmail()
        val userId = getUserIdFromEmail(email).await()
        return if (userId != null) {
            try {
                val usu = firestore.collection("usuarios").document(userId).get().await()
                usu.data
            } catch (e: Exception) {
                println(e.message)
                null
            }
        } else {
            null
        }
    }

    suspend fun getUserName(): String {
        val authManager = AuthManager()
        var email = authManager.getUserEmail()
        val userId = getUserIdFromEmail(email).await()
        if (userId != null) {
            try {
                val usu = firestore.collection("usuarios").document(userId).get().await()
                val nom = usu.getString("nombre")
                if (nom != null)
                    return nom
                return ""
            } catch (e: Exception) {
                println(e.message)
                return ""
            }
        }
        return ""
    }

    suspend fun getUserPass(): String {
        val authManager = AuthManager()
        var email = authManager.getUserEmail()
        val userId = getUserIdFromEmail(email).await()
        if (userId != null) {
            try {
                val usu = firestore.collection("usuarios").document(userId).get().await()
                val pass = usu.getString("contraseña")
                if (pass != null)
                    return pass
                return ""
            } catch (e: Exception) {
                println(e.message)
                return ""
            }
        }
        return ""
    }

    suspend fun getUsers(orden: String, limite:Long?): List<User>? {
        return try {
            var query = firestore.collection("usuarios")
                .orderBy(orden,Query.Direction.DESCENDING)
            if (limite != null) {
                query = query.limit(limite)
            }

            val snapshot = query.get().await()

            val usuariosList = mutableListOf<User>()
            for (document in snapshot.documents) {
                val usuario = document.toObject(User::class.java)
                if (usuario != null) {
                    usuariosList.add(usuario)
                }
            }
            usuariosList
        } catch (e: Exception) {
            println(e.message)
            null
        }
    }

    fun getUserIdFromEmail(email: String): Task<String?> {
        val query = firestore.collection("usuarios").whereEqualTo("correo", email)

        return query.get().continueWith { task ->
            if (task.isSuccessful) {
                val querySnapshot = task.result
                if (querySnapshot != null && !querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    document.id
                } else {
                    null
                }
            } else {
                throw task.exception ?: Exception("Error")
            }
        }
    }

    suspend fun addTorneo(torneo: Torneo): Boolean {

        return try {
            firestore.collection("torneos").add(torneo).await()
            true
        }catch(e: Exception){
            false
        }
    }

    suspend fun getTorneosInicio(nombreUsuario: String): List<Torneo>? {
        return try {
            var query = firestore.collection("torneos")
                .whereEqualTo("estado", "activo")
                .whereGreaterThan("limite", 0)

            val snapshot = query.get().await()

            val torneosList = mutableListOf<Torneo>()
            for (document in snapshot.documents) {
                Log.i("llego","llego")
                val torneo = document.toObject(Torneo::class.java)
                if (torneo != null && nombreUsuario !in torneo.participantes) {
                    Log.i("llego","llego")
                    torneosList.add(torneo)
                }
            }
            torneosList
        } catch (e: Exception) {
            Log.i("llegomal","")
            println(e.message)
            null
        }
    }

    suspend fun meterParticipante(nombreTorneo: String, nombreCliente: String) {
        try {
            val querySnapshot = firestore.collection("torneos").whereEqualTo("nombre", nombreTorneo).get().await()

            if (!querySnapshot.isEmpty) {
                val torneoRef = querySnapshot.documents[0].reference
                torneoRef.update("participantes", FieldValue.arrayUnion(nombreCliente))
            }
        } catch (e: Exception) {
            Log.i( "Error", e.toString())
        }
    }

    suspend fun sumarParticipante(nombreTorneo: String) {
        try {
            val querySnapshot = firestore.collection("torneos").whereEqualTo("nombre", nombreTorneo).get().await()
            Log.i( "Error", "e.toString(")
            if (!querySnapshot.isEmpty) {
                Log.i( "Error", "e.toString(")
                val torneo = querySnapshot.documents[0].reference
                torneo.update("numero_participantes", FieldValue.increment(1))
            }
        } catch (e: Exception) {
            Log.i( "Error", e.toString())
        }
    }
    suspend fun restarLimite(nombreTorneo: String) {
        try {
            val querySnapshot = firestore.collection("torneos").whereEqualTo("nombre", nombreTorneo).get().await()
            if (!querySnapshot.isEmpty) {
                val torneo = querySnapshot.documents[0].reference
                torneo.update("limite", FieldValue.increment(-1))
            }
        } catch (e: Exception) {
            Log.e("Error", "Error al restar participante: ${e.message}")
        }
    }

    suspend fun getTorneosClient(nombreUsuario: String): List<Torneo>? {
        return try {
            var query = firestore.collection("torneos")
                .whereEqualTo("estado", "activo")

            val snapshot = query.get().await()

            val torneosList = mutableListOf<Torneo>()
            for (document in snapshot.documents) {
                Log.i("llego","llego")
                val torneo = document.toObject(Torneo::class.java)
                if (torneo != null && nombreUsuario in torneo.participantes) {
                    Log.i("llego","llego")
                    torneosList.add(torneo)
                }
            }
            torneosList
        } catch (e: Exception) {
            Log.i("llegomal","")
            println(e.message)
            null
        }
    }

    suspend fun getTorneos(): List<Torneo>? {
        return try {
            var query = firestore.collection("torneos")
            val snapshot = query.get().await()

            val torneosList = mutableListOf<Torneo>()
            for (document in snapshot.documents) {
                val torneo = document.toObject(Torneo::class.java)
                if (torneo != null) {
                    torneosList.add(torneo)
                }

            }
            torneosList
        } catch (e: Exception) {
            println(e.message)
            null
        }
    }
    suspend fun sumarVictoria(nombreUsuario: String) {
        try {
            val querySnapshot = firestore.collection("usuarios").whereEqualTo("nombre", nombreUsuario).get().await()
            if (!querySnapshot.isEmpty) {
                val usuario = querySnapshot.documents[0].reference
                usuario.update("victorias", FieldValue.increment(1))
            }
        } catch (e: Exception) {
            Log.i( "Error", e.toString())
        }
    }

    suspend fun cambiarEstado(nombreTorneo: String,estado: String) {
        try {
            val querySnapshot = firestore.collection("torneos").whereEqualTo("nombre", nombreTorneo).get().await()

            if (!querySnapshot.isEmpty) {
                val torneoRef = querySnapshot.documents[0].reference
                torneoRef.update("estado", estado)
            }
        } catch (e: Exception) {
            Log.i( "Error", e.toString())
        }
    }

    suspend fun addGanador(nombreTorneo: String,nombreUsuario: String) {
        try {
            val querySnapshot = firestore.collection("torneos").whereEqualTo("nombre", nombreTorneo).get().await()

            if (!querySnapshot.isEmpty) {
                val torneoRef = querySnapshot.documents[0].reference
                torneoRef.update("ganador", nombreUsuario)
            }
        } catch (e: Exception) {
            Log.i( "Error", e.toString())
        }
    }
    suspend fun checkAdmin(email: String, password: String): Boolean {
        return try {
            val querySnapshot = firestore.collection("admins")
                .whereEqualTo("correo", email)
                .whereEqualTo("contraseña", password)
                .get().await()

            !querySnapshot.isEmpty
        } catch (e: Exception) {
            Log.i("Error", e.toString())
            false
        }
    }
    suspend fun checkUserEamil(email: String): Boolean {
        return try {
            val querySnapshot = firestore.collection("usuarios")
                .whereEqualTo("correo", email)
                .get().await()

            !querySnapshot.isEmpty
        } catch (e: Exception) {
            Log.i("Error", e.toString())
            false
        }
    }

    suspend fun getEncuestaData(): Encuesta? {
        return try {
            var query = firestore.collection("encuestas")
                .whereEqualTo("estado","activo")
            val snapshot = query.get().await()
            val document = snapshot.documents.firstOrNull()

            document?.toObject<Encuesta>()

        } catch (e: Exception) {
            println(e.message)
            null
        }
    }

    suspend fun addEncuesta(encuesta: Encuesta): Boolean {

        return try {
            firestore.collection("encuestas").add(encuesta).await()
            true
        }catch(e: Exception){
            false
        }
    }

    suspend fun cambiarEstadoEncuesta() {
        try {
            val querySnapshot = firestore.collection("encuestas").whereEqualTo("estado", "activo").get().await()

            if (!querySnapshot.isEmpty) {
                val torneoRef = querySnapshot.documents[0].reference
                torneoRef.update("estado", "cerrado")
            }
        } catch (e: Exception) {
            Log.i( "Error", e.toString())
        }
    }
    suspend fun usuarioVotado(username:String):Boolean {
        return try {
            val querySnapshot = firestore.collection("encuestas").whereArrayContains("usuarios_voto", username).get().await()

            !querySnapshot.isEmpty
        } catch (e: Exception) {
            Log.i( "Error", e.toString())
            false
        }

    }
    suspend fun sumarVoto(voto: String) {
        try {
            val querySnapshot = firestore.collection("encuestas").whereEqualTo("estado", "activo").get().await()
            if (!querySnapshot.isEmpty) {
                val encuesta = querySnapshot.documents[0].reference
                if (voto.equals("v1")){
                    encuesta.update("votos1", FieldValue.increment(1))
                }else if (voto.equals("v2")){
                    encuesta.update("votos2", FieldValue.increment(1))
                }else{
                    encuesta.update("votos3", FieldValue.increment(1))
                }

            }
        } catch (e: Exception) {
            Log.i( "Error", e.toString())
        }
    }

    suspend fun meterVotante(username: String) {
        try {
            val querySnapshot = firestore.collection("encuestas").whereEqualTo("estado", "activo").get().await()

            if (!querySnapshot.isEmpty) {
                val torneoRef = querySnapshot.documents[0].reference
                torneoRef.update("usuarios_voto", FieldValue.arrayUnion(username))
            }
        } catch (e: Exception) {
            Log.i( "Error", e.toString())
        }
    }



}