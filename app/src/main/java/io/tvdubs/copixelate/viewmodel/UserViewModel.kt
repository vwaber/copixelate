package io.tvdubs.copixelate.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.tvdubs.copixelate.data.TextField

class UserViewModel : ViewModel() {

    // Live data string for user email text input.
    private val _userEmailText: MutableLiveData<String> = MutableLiveData("")
    val userEmailText: LiveData<String> = _userEmailText

    // Live data string for user password input.
    private val _passwordText: MutableLiveData<String> = MutableLiveData("")
    val passwordText: LiveData<String> = _passwordText

    // Live data string for confirming user password input.
    private val _confirmPasswordText: MutableLiveData<String> = MutableLiveData("")
    val confirmPasswordText: LiveData<String> = _confirmPasswordText

    // The current user account retrieved from Firebase
    private var user: FirebaseUser? = null

    // Initialize instance of authorization.
    private var auth: FirebaseAuth = Firebase.auth

    // Update text field values.
    fun updateTextFieldText(text: String, enum: Enum<TextField>) {
        when (enum) {
            TextField.USER_EMAIL -> {
                _userEmailText.value = text
            }
            TextField.USER_PASSWORD -> {
                _passwordText.value = text
            }
            else -> {
                _confirmPasswordText.value = text
            }
        }
    }

    fun newUser(email: String, password: String) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user = auth.currentUser
                    Log.i("registration", "successful")
                } else {
                    Log.i("registration", "failed: ${task.exception}")
                }

                // Resets values in text fields.
                for (enum in TextField.values()) {
                    updateTextFieldText("", enum)
                }
            }
    }

    fun signIn(email: String, password: String) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user = auth.currentUser
                    Log.i("login", "successful")

                } else {
                    Log.i("login", "failed: ${task.exception}")
                }

                // Resets all text fields.
                for (enum in TextField.values()) {
                    updateTextFieldText("", enum)
                }
            }
    }
}
