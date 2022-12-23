package io.tvdubs.copixelate.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

sealed class AuthResult<out T> {
    data class Success<out R>(val value: R) : AuthResult<R>()
    data class Failure(val message: String) : AuthResult<Nothing>()
}

object Auth {

    private val auth: FirebaseAuth = Firebase.auth

    val displayName: String
        get() = auth.currentUser?.displayName ?: "Local User"
    val isSignedIn: Boolean
        get() = auth.currentUser != null

    fun signIn(
        email: String,
        password: String,
        onComplete: (result: AuthResult<Unit>) -> Unit
    ) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(AuthResult.Success(Unit))
                } else {
                    val message = task.exception?.message.toString()
                    onComplete(AuthResult.Failure(message))
                }

            }
    }

    fun createAccount(
        email: String,
        password: String,
        onComplete: (result: AuthResult<Unit>) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(AuthResult.Success(Unit))
                } else {
                    val message = task.exception?.message.toString()
                    onComplete(AuthResult.Failure(message))
                }

            }
    }

    fun updateAccount(
        displayName: String,
        onComplete: (result: AuthResult<Unit>) -> Unit = {}
    ) {
        auth.currentUser?.updateProfile(
            UserProfileChangeRequest
                .Builder()
                .setDisplayName(displayName)
                .build()
        )?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onComplete(AuthResult.Success(Unit))
            } else {
                val message = task.exception?.message.toString()
                onComplete(AuthResult.Failure(message))
            }
        }
    }

    fun signOut() {
        auth.signOut()
    }

}
