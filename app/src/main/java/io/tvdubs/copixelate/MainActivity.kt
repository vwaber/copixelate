package io.tvdubs.copixelate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.tvdubs.copixelate.nav.Screen
import io.tvdubs.copixelate.nav.SetupNavGraph
import io.tvdubs.copixelate.ui.theme.CopixelateTheme

class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navBarItems = listOf(
            Screen.Art,
            Screen.MessageThread,
            Screen.Login,
        )

        setContent {
            CopixelateTheme {
                // Sets up the nav controller
                // Keeps track of backtrack and composable screens.
                navController = rememberNavController()

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination
                            navBarItems.forEach { screen ->
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            Icons.Filled.Favorite,
                                            contentDescription = "Butt icon"
                                        )
                                    },
                                    //label = { Text(stringResource(screen.resourceId)) },
                                    label = { Text("Butt") },
                                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            // Pop up to the start destination of the graph to
                                            // avoid building up a large stack of destinations
                                            // on the back stack as users select items
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            // Avoid multiple copies of the same destination when
                                            // reselecting the same item
                                            launchSingleTop = true
                                            // Restore state when reselecting a previously selected item
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) {
                    //Account for bottom nav bar height
                    Surface(Modifier.padding(it)) {
                        SetupNavGraph(navController = navController)
                    }
                }
            }
        }
    }
}
