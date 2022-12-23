package io.tvdubs.copixelate.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.tvdubs.copixelate.data.Auth
import io.tvdubs.copixelate.data.AuthResult
import io.tvdubs.copixelate.nav.refresh
import io.tvdubs.copixelate.ui.theme.CopixelateTheme
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(navController: NavController) {

    val onSignUp = { email: String, displayName: String, password: String ->
        Auth.createAccount(email, password) { result ->
            when (result) {
                is AuthResult.Success -> {
                    Log.d("onSignUp", "successful")
                    Auth.updateAccount(displayName) { navController.refresh() }
                }
                is AuthResult.Failure -> {
                    Log.d("onSignUp", "failed: ${result.message}")
                }
            }
        }
    }

    val onSignIn = { email: String, password: String ->
        Auth.signIn(email, password) { result ->
            when (result) {
                is AuthResult.Success -> {
                    Log.d("onSignIn", "successful")
                    navController.refresh()
                }
                is AuthResult.Failure -> {
                    Log.d("onSignIn", "failed: ${result.message}")
                }
            }
        }
    }

    AuthForm(
        onSignUp = onSignUp,
        onSignIn = onSignIn
    )
}

@Preview
@Composable
fun AuthScreenPreview() {

    CopixelateTheme(darkTheme = true) {
        Surface(modifier = Modifier.fillMaxSize()) {
            AuthForm(
                onSignUp = { _, _, _ -> },
                onSignIn = { _, _ -> }
            )
        }
    }

}

enum class Action { SIGN_IN, SIGN_UP }

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun AuthForm(
    onSignUp: (email: String, displayName: String, password: String) -> Unit,
    onSignIn: (email: String, password: String) -> Unit
) {

    var action: Action by remember { mutableStateOf(Action.SIGN_IN) }
    var email: String by remember { mutableStateOf("") }
    var displayName: String by remember { mutableStateOf("") }
    var password: String by remember { mutableStateOf("") }
    var passwordAgain: String by remember { mutableStateOf("") }
    var isPasswordVisible: Boolean by remember { mutableStateOf(false) }

    val composableScope = rememberCoroutineScope()

    val focusManager = LocalFocusManager.current

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Email Input Field
            OutlinedTextField(
                value = email,
                onValueChange = { value -> email = value },
                label = { Text(text = "Email") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                modifier = Modifier
                    .padding(bottom = 4.dp),
            )
            // Display Name Input Field
            if (action == Action.SIGN_UP) {
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { value -> displayName = value },
                    label = { Text(text = "Display Name") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                )
            }
            // Password Input Field
            OutlinedTextField(
                value = password,
                onValueChange = { value -> password = value },
                label = { Text(text = "Password") },
                keyboardOptions = when (action) {
                    Action.SIGN_IN -> KeyboardOptions(imeAction = ImeAction.Done)
                    Action.SIGN_UP -> KeyboardOptions(imeAction = ImeAction.Next)
                },
                keyboardActions = when (action) {
                    Action.SIGN_IN -> {
                        KeyboardActions(onDone = { focusManager.clearFocus() })
                    }
                    Action.SIGN_UP -> {
                        KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
                    }
                },
                modifier = Modifier
                    .padding(bottom = 4.dp)
            )
            // Password Again Input Field
            if (action == Action.SIGN_UP) {
                OutlinedTextField(
                    value = passwordAgain,
                    onValueChange = { value -> passwordAgain = value },
                    label = { Text(text = "Password... again") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }),
                    modifier = Modifier
                )
            }
            // Action Button
            Button(
                onClick = {
                    composableScope.launch {
                        when (action) {
                            Action.SIGN_IN -> onSignIn(
                                email,
                                password
                            )
                            Action.SIGN_UP -> onSignUp(
                                email,
                                displayName,
                                password
                            )
                        }
                    }
                },
                modifier = Modifier
                    .padding(top = 32.dp)
                    .defaultMinSize(minWidth = TextFieldDefaults.MinWidth)
            ) {
                Text(
                    text = when (action) {
                        Action.SIGN_IN -> "Log In"
                        Action.SIGN_UP -> "Create Account"
                    }
                )
            }
            // Switch Action Button
            TextButton(
                onClick = {
                    password = ""
                    passwordAgain = ""
                    action = when (action) {
                        Action.SIGN_IN -> Action.SIGN_UP
                        Action.SIGN_UP -> Action.SIGN_IN
                    }
                },
                modifier = Modifier
                    .padding(top = 16.dp)
                    .defaultMinSize(minWidth = TextFieldDefaults.MinWidth)
            ) {
                Text(
                    text = when (action) {
                        Action.SIGN_IN -> "Create Account"
                        Action.SIGN_UP -> "Log In"
                    }
                )
            }

        }// End Column

    }// End Surface

}// End AuthForm
