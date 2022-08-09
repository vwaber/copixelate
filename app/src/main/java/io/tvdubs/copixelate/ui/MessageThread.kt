package io.tvdubs.copixelate.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.tvdubs.copixelate.navigation.Screen

@Composable
fun MessageThreadScreen(navController: NavController) {
    // Context for the toast.
    val context = LocalContext.current

    Column {
        Text(text = "Message with 'User'.")

        // Button for sending message.
        Button(
            onClick = {
                Toast.makeText(context, "Message Sent!", Toast.LENGTH_LONG)
                    .show()
            },
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text(text = "Send Message")
        }

        // Back button.
        Button(
            onClick = { navController.popBackStack()},
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text(text = "Back")
        }

        // Logout button.
        Button(
            onClick = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Login.route) {
                        inclusive = true
                    }
                }
            },
            modifier = Modifier.padding(start = 16.dp)
        ) {
           Text(text = "Logout")
        }
    }
}
