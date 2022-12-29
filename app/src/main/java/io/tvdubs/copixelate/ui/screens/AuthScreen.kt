package io.tvdubs.copixelate.ui.screens

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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.tvdubs.copixelate.R
import io.tvdubs.copixelate.data.*
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

private enum class FormAction {
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

    var action: FormAction by rememberSaveable { mutableStateOf(FormAction.SIGN_IN) }

    var email: String by rememberSaveable { mutableStateOf("") }
    var isEmailValid by rememberSaveable { mutableStateOf(false) }

    var displayName: String by rememberSaveable { mutableStateOf("") }
    var isDisplayNameValid by rememberSaveable { mutableStateOf(false) }

    var password: String by rememberSaveable { mutableStateOf("") }
    var isPasswordValid by rememberSaveable { mutableStateOf(true) }

    var passwordAgain: String by remember { mutableStateOf("") }
    var isPasswordAgainValid by rememberSaveable { mutableStateOf(true) }

    val composableScope = rememberCoroutineScope()

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp),
    ) {

        // Email input field
        ValidatedTextInputField(
            value = email,
            onValueChange = { value -> email = value },
            label = { Text(text = "Email") },
            validity = InputValidation.checkEmail(email)
                .also { validity -> isEmailValid = validity.isValid }
        )

        // Display Name input field
        AnimatedVisibility(
            visible = when (action) {
                FormAction.SIGN_IN -> false
                FormAction.SIGN_UP -> true
            }
        ) {
            ValidatedTextInputField(
                value = displayName,
                onValueChange = { value -> displayName = value },
                label = { Text(text = "Display Name") },
                validity = InputValidation.checkDisplayName(displayName)
                    .also { validity -> isDisplayNameValid = validity.isValid },
                modifier = Modifier
                    .padding(top = 4.dp)
            )
        }

        // Password input field
        SecretTextInputField(
            value = password,
            onValueChange = { value -> password = value },
            label = { Text(text = "Password") },
            imeAction = when (action) {
                FormAction.SIGN_IN -> ImeAction.Done
                FormAction.SIGN_UP -> ImeAction.Next
            },
            validity = InputValidation.checkPassword(password)
                .also { validity -> isPasswordValid = validity.isValid },
            modifier = Modifier
                .padding(top = 4.dp)
        )

        // Password Again input field
        AnimatedVisibility(
            visible = when (action) {
                FormAction.SIGN_IN -> false
                FormAction.SIGN_UP -> true
            }
        ) {
            SecretTextInputField(
                value = passwordAgain,
                onValueChange = { value -> passwordAgain = value },
                label = { Text(text = "Password... again") },
                imeAction = ImeAction.Done,
                validity = InputValidation.checkPasswordMatch(password, passwordAgain)
                    .also { validity -> isPasswordAgainValid = validity.isValid },
                modifier = Modifier
                    .padding(top = 4.dp)
            )
        }

        // Action Button
        Button(
            onClick = {
                composableScope.launch {
                    when (action) {
                        FormAction.SIGN_IN -> onSignIn(email, password)
                        FormAction.SIGN_UP -> onSignUp(email, displayName, password)
                    }
                }
            },
            enabled = when (action) {
                FormAction.SIGN_IN -> isEmailValid && isPasswordValid
                FormAction.SIGN_UP -> isEmailValid && isDisplayNameValid && isPasswordValid && isPasswordAgainValid
            },
            modifier = Modifier
                .padding(top = 8.dp)
                .defaultMinSize(minWidth = TextFieldDefaults.MinWidth)
        ) {
            Text(
                text = when (action) {
                    FormAction.SIGN_IN -> "Log In"
                    FormAction.SIGN_UP -> "Create Account"
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
                .defaultMinSize(minWidth = TextFieldDefaults.MinWidth)
        ) {
            Text(
                text = when (action) {
                    FormAction.SIGN_IN -> "Create Account"
                    FormAction.SIGN_UP -> "Log In"
                },
                style = MaterialTheme.typography.labelMedium
            )
        }

    }// End Column

}// End AuthForm

private enum class Visibility {
    VISIBLE, HIDDEN;

    fun toggle() = when (this) {
        VISIBLE -> HIDDEN
        HIDDEN -> VISIBLE
    }
}

@Composable
private fun SecretTextInputField(
    value: String,
    onValueChange: (value: String) -> Unit,
    label: @Composable (() -> Unit),
    validity: InputValidity,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next,
) {

    var visibility: Visibility by remember { mutableStateOf(Visibility.HIDDEN) }

    ValidatedTextInputField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        validity = validity,
        imeAction = imeAction,
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
        modifier = modifier
    )

}// End SecretTextInputField

@Composable
private fun ValidatedTextInputField(
    value: String,
    onValueChange: (value: String) -> Unit,
    label: @Composable (() -> Unit),
    validity: InputValidity,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
) {

    var lastFocusedState by rememberSaveable { mutableStateOf(false) }

    var isError by rememberSaveable { mutableStateOf(false) }
    isError = isError && validity.isNotValid && value.isNotEmpty()

    Column {
        KeyboardActionTextInputField(
            value = value,
            onValueChange = onValueChange,
            label = label,
            isError = isError,
            imeAction = imeAction,
            modifier = modifier.onFocusChanged { focusState ->
                isError = isError || (!focusState.isFocused && lastFocusedState)
                lastFocusedState = focusState.isFocused
            },
            visualTransformation = visualTransformation,
            trailingIcon = trailingIcon,

            )

        if (isError) {

            val resourceId: Int = when (validity) {
                InputValidity.Email.Invalid -> R.string.invalid_email

                InputValidity.DisplayName.TooShort -> R.string.invalid_display_name_too_short
                InputValidity.DisplayName.TooLong -> R.string.invalid_display_name_too_long

                InputValidity.Password.TooShort -> R.string.invalid_password_too_short
                InputValidity.Password.TooLong -> R.string.invalid_password_too_long
                InputValidity.Password.NoMatch -> R.string.invalid_password_no_match

                InputValidity.Valid -> 0
            }

            if (resourceId != 0) {
                val errorText: String = stringResource(resourceId)

                Text(
                    text = errorText,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 8.dp, bottom = 0.dp)
                )
            }

        }// End "if (isError)"

    }// End Column

}// End TextInputField

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun KeyboardActionTextInputField(
    value: String,
    onValueChange: (value: String) -> Unit,
    label: @Composable (() -> Unit),
    isError: Boolean,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
) {

    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        isError = isError,
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
        modifier = modifier,
        visualTransformation = visualTransformation,
        trailingIcon = trailingIcon,
    )

}// End ImeActionTextInputField
