package io.tvdubs.copixelate.ui

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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

    Scroller {
        AuthForm(
            onSignUp = onSignUp,
            onSignIn = onSignIn
        )
    }

}

@Preview
@Composable
fun AuthScreenPreview() {

    CopixelateTheme(darkTheme = true) {
        Scroller {
            AuthForm(
                onSignUp = { _, _, _ -> },
                onSignIn = { _, _ -> }
            )
        }
    }

}

private enum class Action {
    SIGN_IN, SIGN_UP;

    fun next() = when (this) {
        SIGN_IN -> SIGN_UP
        SIGN_UP -> SIGN_IN
    }
}

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

    val composableScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Email input field
        TextInputField(
            value = email,
            onValueChange = { value -> email = value },
            label = { Text(text = "Email") },
            modifier = Modifier
                .padding(bottom = 4.dp)
        )

        // Display Name input field
        AnimatedVisibility(
            visible = when (action) {
                Action.SIGN_IN -> false
                Action.SIGN_UP -> true
            }
        ) {
            TextInputField(
                value = displayName,
                onValueChange = { value -> displayName = value },
                label = { Text(text = "Display Name") },
                modifier = Modifier
                    .padding(bottom = 4.dp)
            )
        }

        // Password input field
        SecretTextInputField(
            value = password,
            onValueChange = { value -> password = value },
            label = { Text(text = "Password") },
            imeAction = when (action) {
                Action.SIGN_IN -> ImeAction.Done
                Action.SIGN_UP -> ImeAction.Next
            },
            modifier = Modifier
                .padding(bottom = 4.dp)
        )

        // Password Again input field
        AnimatedVisibility(
            visible = when (action) {
                Action.SIGN_IN -> false
                Action.SIGN_UP -> true
            }
        ) {
            SecretTextInputField(
                value = passwordAgain,
                onValueChange = { value -> passwordAgain = value },
                label = { Text(text = "Password... again") },
                imeAction = ImeAction.Done,
            )
        }

        // Action Button
        Button(
            onClick = {
                composableScope.launch {
                    when (action) {
                        Action.SIGN_IN -> onSignIn(email, password)
                        Action.SIGN_UP -> onSignUp(email, displayName, password)
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
                passwordAgain = ""
                action = action.next()
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

}// End AuthForm

@Composable
private fun Scroller(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        content()
    }
}

private enum class Visibility {
    VISIBLE, HIDDEN;

    fun toggle() = when (this) {
        VISIBLE -> HIDDEN
        HIDDEN -> VISIBLE
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SecretTextInputField(
    value: String,
    onValueChange: (value: String) -> Unit,
    label: @Composable (() -> Unit),
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next,
) {

    var visibility: Visibility by remember { mutableStateOf(Visibility.HIDDEN) }
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        visualTransformation = when (visibility) {
            Visibility.VISIBLE -> VisualTransformation.None
            Visibility.HIDDEN -> PasswordVisualTransformation()
        },
        trailingIcon = {
            val image = when (visibility) {
                Visibility.VISIBLE -> Icons.Filled.VisibilityOff
                Visibility.HIDDEN -> Icons.Filled.Visibility
            }
            val contentDescription = when (visibility) {
                Visibility.VISIBLE -> "Hide password"
                Visibility.HIDDEN -> "Show password"
            }
            IconButton(onClick = { visibility = visibility.toggle() }) {
                Icon(image, contentDescription)
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = imeAction),
        keyboardActions = when (imeAction) {
            ImeAction.Next -> {
                KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) })
            }
            ImeAction.Done -> {
                KeyboardActions(onDone = { focusManager.clearFocus() })
            }
            else -> KeyboardActions.Default
        },
        modifier = modifier
    )

}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TextInputField(
    value: String,
    onValueChange: (value: String) -> Unit,
    label: @Composable (() -> Unit),
    modifier: Modifier = Modifier
) {

    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Next) })
    )

}
