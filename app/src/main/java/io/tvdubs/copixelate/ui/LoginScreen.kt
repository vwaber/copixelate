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
import io.tvdubs.copixelate.nav.ScreenInfo
import io.tvdubs.copixelate.viewmodel.UserViewModel
import vwaber.copixelate.core.AppUses

@Composable
fun LoginScreen(navController: NavController, viewModel: UserViewModel) {

    val userEmailText: String by viewModel.userEmailText.observeAsState(initial = "")
    val userPasswordText: String by viewModel.passwordText.observeAsState(initial = "")
    val singedInStatus: Boolean by viewModel.singedIn.observeAsState(initial = false)
    val context = LocalContext.current
    val passwordVisible: Boolean by viewModel.passwordVisible.observeAsState(initial = false)

    if (singedInStatus) {
        navController.navigate(ScreenInfo.Messages.route) {
            popUpTo(ScreenInfo.Messages.route) {
                inclusive = true
            }
        }
    }

    LoginScreenContent(
        userEmail = userEmailText,
        userPassword = userPasswordText,
        onUserEmailChange = { viewModel.updateTextFieldText(it, TextField.USER_EMAIL) },
        onUserPasswordChange = { viewModel.updateTextFieldText(it, TextField.USER_PASSWORD) },
        onLoginClick = {
            AppUses.login()
            if (userEmailText == "") {
                viewModel.toastMaker(context, "Enter Email").show()
            } else if (userPasswordText == "") {
                viewModel.toastMaker(context, "Enter Password").show()
            } else {
                viewModel.signIn(userEmailText, userPasswordText)
            }
        },
        onRegistrationClick = {
            navController.navigate(ScreenInfo.Registration.route)
        },
        passwordVisible = passwordVisible,
        onShowPasswordClick = { viewModel.changePasswordVisibility() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenContent(
    userEmail: String,
    userPassword: String,
    onUserEmailChange: (String) -> Unit,
    onUserPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegistrationClick: () -> Unit,
    passwordVisible: Boolean,
    onShowPasswordClick: () -> Unit
) {
    Column {

        Text(text = "You must login or create an account to access this feature!",
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            value = userEmail,
            onValueChange = onUserEmailChange,
            label = { Text(text = "Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp)
        )

        OutlinedTextField(
            value = userPassword,
            onValueChange = onUserPasswordChange,
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

        // Button for logging in user and navigating to messages screen.
        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Text(text = "Login")
        }

        // Button for navigating to a registration screen for the user.
        Button(
            onClick = onRegistrationClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, start = 16.dp, end = 16.dp)
        ) {
            Text(text = "Registration")
        }
    }
}
