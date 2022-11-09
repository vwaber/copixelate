package io.tvdubs.copixelate.nav

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.tvdubs.copixelate.ui.*
import io.tvdubs.copixelate.viewmodel.ArtViewModel
import io.tvdubs.copixelate.viewmodel.UserViewModel

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    artViewModel: ArtViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Art.route
    ) {
        composable(
            route = Screen.Art.route
        ) {
            ArtScreen(viewModel = artViewModel)
        }

        composable(
            route = Screen.Registration.route
        ) {
            RegistrationScreen(navController = navController, viewModel = userViewModel)
        }

        composable(
            route = Screen.Messages.route
        ) {
            userViewModel.changePasswordVisibility(false)
            if (userViewModel.auth.currentUser == null) {
                userViewModel.changeSignInStatus(false)
                LoginScreen(navController = navController, viewModel = userViewModel)
            } else {

                if (userViewModel.singedIn.value != true) {
                    userViewModel.changeSignInStatus(true)
                }

                MessagesScreen(navController = navController, viewModel = userViewModel)
            }
        }

        composable(
            route = Screen.MessageThread.route
        ) {
            if (userViewModel.auth.currentUser == null) {
                LoginScreen(navController = navController, viewModel = userViewModel)
            } else {
                MessageThreadScreen(navController = navController, viewModel = userViewModel)
            }
        }
    }
}
