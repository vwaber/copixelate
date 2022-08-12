package io.tvdubs.copixelate.nav

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.tvdubs.copixelate.ui.LoginScreen
import io.tvdubs.copixelate.ui.MessageThreadScreen
import io.tvdubs.copixelate.ui.MessagesScreen
import io.tvdubs.copixelate.ui.RegistrationScreen
import io.tvdubs.copixelate.viewmodel.AppViewModel

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    appViewModel: AppViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(
            route = Screen.Login.route
        ) {
            LoginScreen(navController = navController)
        }

        composable(
            route = Screen.Registration.route
        ) {
            RegistrationScreen(navController = navController)
        }

        composable(
            route = Screen.Messages.route
        ) {
            MessagesScreen(navController = navController)
        }

        composable(
            route = Screen.MessageThread.route
        ) {
            MessageThreadScreen(navController = navController)
        }
    }
}
