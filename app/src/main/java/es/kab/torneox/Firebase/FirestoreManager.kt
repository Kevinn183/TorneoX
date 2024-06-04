package es.kab.torneox.Firebase
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
            val authManager = AuthManager()
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
        val authManager = AuthManager()
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
}