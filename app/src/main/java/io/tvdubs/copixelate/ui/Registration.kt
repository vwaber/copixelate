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
fun RegistrationScreen(navController: NavController, viewModel: UserViewModel) {
    val userEmail: String by viewModel.userEmailText.observeAsState("")
    val userPassword: String by viewModel.passwordText.observeAsState("")
    val confirmPassword: String by viewModel.confirmPasswordText.observeAsState("")
    val userUsername: String by viewModel.userUsernameText.observeAsState("")

    val context = LocalContext.current

    RegistrationScreenContent(
        onRegistrationClick = {
            if (confirmPassword == userPassword && userEmail != "" && userPassword != "") {
                viewModel.registerUserEmail(userEmail, userPassword)
                navController.navigate(Screen.Login.route)
            } else {
                if (userEmail == "") {
                    Toast.makeText(context, "Enter Email", Toast.LENGTH_LONG).show()
                } else if (userPassword == "") {
                    Toast.makeText(context, "Enter Password", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_LONG).show()
                }

                for (enum in TextField.values()) {
                    if (enum != TextField.USER_EMAIL) {
                        viewModel.updateTextFieldText("", enum)
                    }
                }
            }
        },
        onCancelClick = {
            navController.navigate(Screen.Login.route)
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
        onUsernameFieldTextChange = { viewModel.updateTextFieldText(it, TextField.USER_USERNAME) }
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
    onUsernameFieldTextChange: (String) -> Unit
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
            visualTransformation = PasswordVisualTransformation()
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordFieldTextChange,
            label = { Text(text = "Confirm Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            visualTransformation = PasswordVisualTransformation()
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
