package es.kab.torneox.Control

import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import kotlin.Exception

class AuthManager {
    private val auth: FirebaseAuth by lazy { Firebase.auth }

    suspend fun login(email:String, password:String): FirebaseUser?{
        return try{
            val authResult = auth.signInWithEmailAndPassword(email,password).await()
            authResult.user
        }catch (e: Exception){
            null
        }
    }
    suspend fun singin(email:String, password:String):FirebaseUser?{
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email,password).await()
            authResult.user
        }catch (e: Exception){
            null
        }
    }
    suspend fun changePass(pass:String){
        try {
            val currentUser = auth.currentUser
            currentUser?.updatePassword(pass)
        }catch (e:Exception){
            null
        }
    }
    suspend fun resetPass(email:String){
        try {
            auth.sendPasswordResetEmail(email).await()
        }catch (e: Exception){
            null
        }
    }
    fun getUserEmail(): String {
        val currentUser = auth.currentUser
        if (currentUser != null){
            return  currentUser.email.toString()

        }
        return ""
    }

    fun logOut(){
        auth.signOut()
    }

}