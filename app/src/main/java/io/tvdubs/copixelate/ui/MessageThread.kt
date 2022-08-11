package io.tvdubs.copixelate.ui

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.tvdubs.copixelate.nav.Screen
import java.util.Random

@Composable
fun MessageThreadScreen(navController: NavController) {
    // Small random bitmap for demo purposes
    val emptyArray by remember {
        mutableStateOf(
            IntArray(16) { Random().nextInt() }
        )
    }

    val bitmap = Bitmap.createBitmap(emptyArray, 4, 4, Bitmap.Config.ARGB_8888)

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
            onClick = { navController.popBackStack() },
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

        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            BitmapImage(bitmap = bitmap, "BITMAP, YAY!!!")
        }


    }

}

/**
 * A composable that lays out and draws a given [Bitmap] without filtering
 *
 * @param bitmap The [Bitmap] to draw unfiltered
 * @param contentDescription text used by accessibility services to describe what this image
 */
@Composable
fun BitmapImage(
    bitmap: Bitmap,
    contentDescription: String
) {
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = contentDescription,
        filterQuality = FilterQuality.None
    )
}
