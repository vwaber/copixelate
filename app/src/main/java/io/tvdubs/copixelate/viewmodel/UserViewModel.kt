package io.tvdubs.copixelate.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import io.tvdubs.copixelate.data.Contact
import io.tvdubs.copixelate.data.User
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

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

    private val _userUsernameText: MutableLiveData<String> = MutableLiveData("")
    val userUsernameText: LiveData<String> = _userUsernameText

    private val _signedIn: MutableLiveData<Boolean> = MutableLiveData()
    val singedIn: LiveData<Boolean> = _signedIn

    private val _user: MutableLiveData<User?> = MutableLiveData()
    val user: LiveData<User?> = _user

    private val _contactList: MutableLiveData<MutableList<Contact>> = MutableLiveData(mutableListOf())
    val contactList: LiveData<MutableList<Contact>> = _contactList

    private val _searchString: MutableLiveData<String> = MutableLiveData("")
    val searchString: LiveData<String> = _searchString

    // Initialize instance of authorization.
    var auth: FirebaseAuth = Firebase.auth

    // Initialize instance of database
    val database = FirebaseDatabase.getInstance()

    private val _passwordVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    val passwordVisible: LiveData<Boolean> = _passwordVisible

    init {
        if (auth.currentUser != null) {
            retrieveUserInfo()
        }
    }

    private fun createUser() {
        val userInfo = auth.currentUser
        val usernameRef = database.getReference("usernames")
        val user = User(
            uid = userInfo?.uid,
            email = userInfo?.email,
            contacts = mutableListOf(""),
            artBoards = mutableListOf(""),
            profilePicture = ""
        )
        viewModelScope.launch {
            usernameRef.child(userInfo?.displayName.toString()).setValue(user).addOnCompleteListener {
                _user.value = user
            }
        }
    }

    // Update text field values.
    fun updateTextFieldText(text: String, enum: Enum<TextField>) {
        when (enum) {
            TextField.USER_EMAIL -> {
                _userEmailText.value = text
            }
            TextField.USER_PASSWORD -> {
                _passwordText.value = text
            }
            TextField.USER_USERNAME -> {
                _userUsernameText.value = text
            }
            TextField.SEARCH_STRING -> {
                _searchString.value = text
            }
            else -> {
                _confirmPasswordText.value = text
            }
        }
    }

    fun clearTextField() {
        // Resets all text fields.
        for (enum in TextField.values()) {
            updateTextFieldText("", enum)
        }
    }

    fun registerUserEmail(email: String, password: String, context: Context) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.i("registration", "successful")
                    auth.currentUser?.updateProfile(
                        UserProfileChangeRequest
                            .Builder()
                            .setDisplayName(userUsernameText.value.toString())
                            .build()
                    )?.addOnCompleteListener {
                        createUser()
                    }

                } else {
                    Log.i("registration", "failed: ${task.exception}")
                    toastMaker(context, "Registration Failed!").show()
                }

                clearTextField()
            }
    }

    fun signIn(email: String, password: String, context: Context) {

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    changeSignInStatus(true)
                    Log.i("login", "successful")
                    retrieveUserInfo()

                } else {
                    Log.i("login", "failed: ${task.exception}")
                    toastMaker(context, "Login Failed!").show()
                }

                clearTextField()
            }
    }

    private fun retrieveUserInfo() {
        viewModelScope.launch {
            val user = viewModelScope.async {
                database
                    .getReference("usernames")
                    .child(auth.currentUser?.displayName.toString()).get()
                    .addOnSuccessListener {
                        _user.value = User(
                            uid = it.child("uid").value.toString(),
                            email = it.child("email").value.toString(),
                            contacts = it.child("contacts").value as MutableList<String>?,
                            artBoards = it.child("artBoards").value as MutableList<String>?,
                            profilePicture = it.child("profilePicture").value.toString()
                        )
                        Log.i("user", "${_user.value}")
                    }
            }
            user.await().addOnCompleteListener {
                createContactList()
            }
        }
    }

    private fun createContactList() {
        viewModelScope.launch {
            for (contact in _user.value?.contacts!!) {
                database
                    .getReference("users")
                    .child(contact).get()
                    .addOnSuccessListener { user ->
                        _contactList.value?.add(
                            Contact(
                                username = user.key.toString(),
                                profilePic = user.child("profilePicture").value.toString(),
                                uid = user.child("uid").value.toString()
                            )
                        )
                    }
            }
        }
    }

    fun logout() {
        changeSignInStatus(false)
        auth.signOut()
    }

    fun changeSignInStatus(status: Boolean) {
        _signedIn.value = status
    }

    fun changePasswordVisibility(visible: Boolean? = null) {
        if (visible == null) {
            _passwordVisible.value = !_passwordVisible.value!!
        } else {
            _passwordVisible.value = visible
        }
    }

    fun toastMaker(context: Context, text: String): Toast {
        return Toast.makeText(context, text, Toast.LENGTH_LONG)
    }

}

enum class TextField {
    USER_EMAIL,
    USER_PASSWORD,
    USER_CONFIRM_PASSWORD,
    USER_USERNAME,
    SEARCH_STRING
}
