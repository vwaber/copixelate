package io.tvdubs.copixelate

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import io.tvdubs.copixelate.ui.theme.CopixelateTheme
import java.util.Random

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Small random bitmap for demo purposes
        val emptyArray = IntArray(16) { Random().nextInt() }
        val bitmap = Bitmap.createBitmap(emptyArray, 4, 4, Bitmap.Config.ARGB_8888)

        setContent {
            CopixelateTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                    BitmapImage(bitmap = bitmap, "BITMAP, YAY!!!")
                }
            }
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

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CopixelateTheme {
        Greeting("Android")
    }
}