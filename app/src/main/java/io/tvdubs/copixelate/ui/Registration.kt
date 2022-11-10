package io.tvdubs.copixelate.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.tvdubs.copixelate.data.TextField
import io.tvdubs.copixelate.nav.Screen
import io.tvdubs.copixelate.viewmodel.UserViewModel

@Composable
fun RegistrationScreen(navController: NavController, viewModel: UserViewModel) {
    val userEmail: String by viewModel.userEmailText.observeAsState("")
    val userPassword: String by viewModel.passwordText.observeAsState("")
    val confirmPassword: String by viewModel.confirmPasswordText.observeAsState("")
    val userUsername: String by viewModel.userUsernameText.observeAsState("")
    val context = LocalContext.current
    val passwordVisible: Boolean by viewModel.passwordVisible.observeAsState(initial = false)

    RegistrationScreenContent(
        onRegistrationClick = {
            if (confirmPassword == userPassword && userEmail != "" && userPassword != "" && userUsername != "") {
                viewModel.registerUserEmail(userEmail, userPassword, context)
                navController.navigate(Screen.Art.route) {
                    popUpTo(Screen.Art.route) {
                        inclusive = true
                    }
                }
            } else {
                if (userEmail == "") {
                    viewModel.toastMaker(context, "Enter Email").show()
                } else if (userPassword == "") {
                    viewModel.toastMaker(context, "Enter Password").show()
                } else if (userUsername == "") {
                    viewModel.toastMaker(context, "Enter Username").show()
                } else {
                    viewModel.toastMaker(context, "Passwords do not match").show()
                }
            }
        },
        onCancelClick = {
            navController.navigate(Screen.Messages.route) {
                popUpTo(Screen.Messages.route) {
                    inclusive = true
                }
            }
            for (enum in TextField.values()) {
                viewModel.updateTextFieldText("", enum)
            }
        },
        userEmail = userEmail,
        userPassword = userPassword,
        confirmPassword = confirmPassword,
        userUsername = userUsername,
        onEmailFieldTextChange = { viewModel.updateTextFieldText(it, TextField.USER_EMAIL) },
        onPasswordFieldTextChange = { viewModel.updateTextFieldText(it, TextField.USER_PASSWORD) },
        onConfirmPasswordFieldTextChange = { viewModel.updateTextFieldText(it, TextField.USER_CONFIRM_PASSWORD) },
        onUsernameFieldTextChange = { viewModel.updateTextFieldText(it, TextField.USER_USERNAME) },
        passwordVisible = passwordVisible,
        onShowPasswordClick = { viewModel.changePasswordVisibility() }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreenContent(
    onRegistrationClick: () -> Unit,
    onCancelClick: () -> Unit,
    userEmail: String,
    userPassword: String,
    confirmPassword: String,
    userUsername: String,
    onEmailFieldTextChange: (String) -> Unit,
    onPasswordFieldTextChange: (String) -> Unit,
    onConfirmPasswordFieldTextChange: (String) -> Unit,
    onUsernameFieldTextChange: (String) -> Unit,
    passwordVisible: Boolean,
    onShowPasswordClick: () -> Unit
) {
    Column {

        Text(text = "Create an account.",
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            value = userEmail,
            onValueChange = onEmailFieldTextChange,
            label = { Text(text = "Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp)
        )

        OutlinedTextField(
            value = userUsername,
            onValueChange = onUsernameFieldTextChange,
            label = { Text(text = "Create Username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp)
        )

        OutlinedTextField(
            value = userPassword,
            onValueChange = onPasswordFieldTextChange,
            label = { Text(text = "Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                val image = if (passwordVisible) {
                    Icons.Filled.Visibility
                } else {
                    Icons.Filled.VisibilityOff
                }

                IconButton(onClick = { onShowPasswordClick() }) {
                    Icon(imageVector = image, null)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordFieldTextChange,
            label = { Text(text = "Confirm Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                val image = if (passwordVisible) {
                    Icons.Filled.Visibility
                } else {
                    Icons.Filled.VisibilityOff
                }

                IconButton(onClick = { onShowPasswordClick() }) {
                    Icon(imageVector = image, null)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        // Button for registering the user. Returns to login screen after completion.
        Button(
            onClick = {
                onRegistrationClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Text(text = "Register")
        }

        Button(
            onClick = {
                onCancelClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, start = 16.dp, end = 16.dp)
        ) {
            Text(text = "Cancel")
        }
    }
}
