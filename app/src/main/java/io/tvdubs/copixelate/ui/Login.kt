package io.tvdubs.copixelate.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.tvdubs.copixelate.nav.Screen

@Composable
fun LoginScreen(navController: NavController) {
    Column {
        // Button for logging in user and navigating to messages screen.
        Button(
            onClick = { navController.navigate(route = Screen.Messages.route) },
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text(text = "Login")
        }

        // Button for navigating to a registration screen for the user.
        Button(
            onClick = { navController.navigate(route = Screen.Registration.route)},
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text(text = "Registration")
        }
    }
}
