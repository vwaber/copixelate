package io.tvdubs.copixelate.ui

import android.graphics.Bitmap
import android.graphics.Point
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import io.tvdubs.copixelate.viewmodel.ArtViewModel

@Composable
fun ArtScreen(viewModel: ArtViewModel) {

    val viewState by viewModel.stateFlow.collectAsState()

    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {

        BitmapImage(
            bitmap = viewState.drawingBitmap,
            contentDescription = "Drawing",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        viewModel.updatePixel(change.position, 0)
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { offset ->
                            viewModel.updatePixel(offset, 0)
                        }
                    )
                }
                .onGloballyPositioned {
                    viewModel.viewSize = Point(it.size.width, it.size.height)
                }
        )
        BitmapImage(
            bitmap = viewState.paletteBitmap,
            contentDescription = "Drawing palette",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
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
