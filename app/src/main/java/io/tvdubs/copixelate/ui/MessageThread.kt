package io.tvdubs.copixelate.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.navigation.NavController
import java.util.Random

@Composable
fun MessageThreadScreen(navController: NavController) {
    // Small random bitmap for demo purposes
    val emptyArray = IntArray(16) { Random().nextInt() }
    val bitmap = Bitmap.createBitmap(emptyArray, 4, 4, Bitmap.Config.ARGB_8888)

    BitmapImage(bitmap = bitmap, "BITMAP, YAY!!!")
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
