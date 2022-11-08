package io.tvdubs.copixelate.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.tvdubs.copixelate.data.TextField
import io.tvdubs.copixelate.nav.Screen
import io.tvdubs.copixelate.viewmodel.UserViewModel

@Composable
fun LoginScreen(navController: NavController, viewModel: UserViewModel) {

    val userEmailText: String by viewModel.userEmailText.observeAsState(initial = "")
    val userPasswordText: String by viewModel.passwordText.observeAsState(initial = "")

    val singedInStatus: Boolean by viewModel.singedIn.observeAsState(initial = false)

    val context = LocalContext.current

    if (singedInStatus) {
        navController.navigate(Screen.Messages.route)
    }

    LoginScreenContent(
        userEmail = userEmailText,
        userPassword = userPasswordText,
        onUserEmailChange = { viewModel.updateTextFieldText(it, TextField.USER_EMAIL) },
        onUserPasswordChange = { viewModel.updateTextFieldText(it, TextField.USER_PASSWORD) },
        onLoginClick = {
            if (userEmailText == "") {
                Toast.makeText(context, "Enter Email.", Toast.LENGTH_LONG).show()
            } else if (userPasswordText == "") {
                Toast.makeText(context, "Enter Password.", Toast.LENGTH_LONG).show()
            } else {
                viewModel.signIn(userEmailText, userPasswordText)
            }
        },
        onRegistrationClick = {
            navController.navigate(Screen.Registration.route)
        }
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
    onRegistrationClick: () -> Unit
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

        // Todo: Add trailing icon to toggle password visibility
        OutlinedTextField(
            value = userPassword,
            onValueChange = onUserPasswordChange,
            label = { Text(text = "Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            visualTransformation = PasswordVisualTransformation()
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
