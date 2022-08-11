package io.tvdubs.copixelate.ui

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun RegistrationScreen(navController: NavController) {
    // Context for toast
    val context = LocalContext.current

    // Button for registering the user. Returns to login screen after completion.
    Button(
        onClick = {
            navController.popBackStack()
            Toast.makeText(context, "Registered!", Toast.LENGTH_LONG).show()
        },
        modifier = Modifier.padding(start = 16.dp)
    ) {
        Text(text = "Register")
    }
}
