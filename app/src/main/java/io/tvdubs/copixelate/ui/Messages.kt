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
fun MessagesScreen(navController: NavController) {

    MessagesScreenContent(
        clickMessageThread = { navController.navigate(Screen.MessageThread.route) },
        onBackClick = {  navController.popBackStack() }
    )
}

@Composable
fun MessagesScreenContent(
    clickMessageThread: () -> Unit,
    onBackClick: () -> Unit
) {
    Column {
        // Button for advancing to the message thread for the specified user.
        Button(
            onClick = { clickMessageThread() },
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text(text = "Message 'Username'")
        }

        // Button for logging the user out.
        Button(
            onClick = { onBackClick() },
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text(text = "Logout")
        }
    }
}
