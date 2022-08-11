package io.tvdubs.copixelate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.tvdubs.copixelate.nav.SetupNavGraph
import io.tvdubs.copixelate.ui.theme.CopixelateTheme

class MainActivity : ComponentActivity() {
    
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CopixelateTheme {
                // Sets up the nav controller
                // Keeps track of backtrack and composable screens.
                navController = rememberNavController()
                
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Sets up the navGraph passing in the navController.
                    // The initial screen will be the login screen.
                    SetupNavGraph(navController = navController)

                }
            }
        }
    }
}
