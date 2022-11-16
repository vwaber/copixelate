package io.tvdubs.copixelate.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.tvdubs.copixelate.R
import io.tvdubs.copixelate.nav.Screen
import io.tvdubs.copixelate.viewmodel.UserViewModel

@Composable
fun MessagesScreen(navController: NavController, viewModel: UserViewModel) {

    MessagesScreenContent(
        clickMessageThread = { navController.navigate(Screen.MessageThread.route) },
        onLogoutClick = {
            viewModel.logout()
            navController.navigate(Screen.Art.route)
        },
        username = viewModel.user.value?.username
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreenContent(
    clickMessageThread: () -> Unit,
    onLogoutClick: () -> Unit,
    username: String?
) {
    Scaffold (
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /*TODO*/ }
            ) {
                Image(
                    painter = painterResource(
                        id = R.drawable.ic_baseline_message_24
                    ),
                    contentDescription = "New Message"
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) {
        Column {
            Text(text = "Hello, $username")

            // Button for advancing to the message thread for the specified user.
            Button(
                onClick = { clickMessageThread() },
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(text = "Message 'Username'")
            }

            // Button for logging the user out.
            Button(
                onClick = { onLogoutClick() },
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(text = "Logout")
            }

            MessageCard()
        }
    }
}

@Composable
fun MessageCard(
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable._014_09_16__2_),
                contentDescription = "Friend Image",
                modifier = Modifier.padding(4.dp)
            )

            Text(text = "Dick Face")

            Image(
                painter = painterResource(id = R.drawable._014_09_16),
                contentDescription = "Drawing preview",
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@Preview
@Composable
fun MessageCardPreview() {
    MessageCard()
}
