package io.tvdubs.copixelate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.tvdubs.copixelate.ui.MainContent
import vwaber.copixelate.core.AppUses
import vwaber.copixelate.data.AppRepo

class MainActivity : ComponentActivity() {

    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppUses.init(AppRepo)

        setContent {
            navController = rememberNavController()
            MainContent(navController)
        }

    }

}
