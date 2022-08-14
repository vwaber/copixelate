package io.tvdubs.copixelate.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import io.tvdubs.copixelate.viewmodel.AppViewModel

@Composable
fun ArtScreen(viewModel: AppViewModel) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        BitmapImage(bitmap = viewModel.bitmap, "BITMAP, YAY!!!")
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
