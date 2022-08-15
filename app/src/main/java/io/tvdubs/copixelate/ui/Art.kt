package io.tvdubs.copixelate.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import io.tvdubs.copixelate.viewmodel.AppViewModel

@Composable
fun ArtScreen(viewModel: AppViewModel) {

    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {

        BitmapImage(
            bitmap = viewModel.drawing.drawingBitmap,
            contentDescription = "BITMAP, YAY!!!",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )
        BitmapImage(
            bitmap = viewModel.drawing.paletteBitmap,
            contentDescription = "BITMAP, YAY!!!",
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )

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
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale
) {
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = contentDescription,
        filterQuality = FilterQuality.None,
        modifier = modifier,
        contentScale = contentScale
    )
}
