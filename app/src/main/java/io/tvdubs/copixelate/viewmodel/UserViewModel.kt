package io.tvdubs.copixelate.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.tvdubs.copixelate.data.Auth
import io.tvdubs.copixelate.data.AuthResult
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

    private val _userUsernameText: MutableLiveData<String> = MutableLiveData("")
    val userUsernameText: LiveData<String> = _userUsernameText

    private val _signedIn: MutableLiveData<Boolean> = MutableLiveData()
    val singedIn: LiveData<Boolean> = _signedIn

    private val _passwordVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    val passwordVisible: LiveData<Boolean> = _passwordVisible

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
            else -> {
                _confirmPasswordText.value = text
            }
        }
    }

    fun registerUserEmail(email: String, password: String) {

        Auth.createAccount(email, password) { result ->
            when (result) {
                is AuthResult.Success -> {
                    Log.i("registration", "successful")
                    Auth.updateAccount(userUsernameText.value.toString())
                }
                is AuthResult.Failure -> {
                    Log.i("registration", "failed: ${result.message}")
                }
            }
            // Resets values in text fields.
            for (enum in TextField.values()) {
                updateTextFieldText("", enum)
            }

        }

    }

    fun signIn(email: String, password: String) {

        Auth.signIn(email, password) { result ->
            when (result) {
                is AuthResult.Success -> {
                    Log.d("signIn", "successful")
                    changeSignInStatus(true)
                }
                is AuthResult.Failure -> {
                    Log.d("signIn", "failed: ${result.message}")
                }
            }

            // Resets all text fields.
            for (enum in TextField.values()) {
                updateTextFieldText("", enum)
            }
        }

    }

    fun logout() {
        changeSignInStatus(false)
        Auth.signOut()
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
