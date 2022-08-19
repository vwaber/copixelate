package io.tvdubs.copixelate.ui

import android.graphics.Bitmap
import android.graphics.Point
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
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
            bitmap = viewState.bitmap,
            contentDescription = "Drawing",
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned {
                    viewModel.pixelMapViewSize = Point(it.size.width, it.size.height)
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        viewModel.updatePixelMap(change.position)
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { offset ->
                            viewModel.updatePixelMap(offset)
                        }
                    )
                }
        )

        Box(contentAlignment = Alignment.Center) {
            BitmapImage(
                bitmap = viewState.palette.borderBitmap,
                contentDescription = "Drawing palette border",
                modifier = Modifier
                    .fillMaxWidth()
            )
            BitmapImage(
                bitmap = viewState.palette.bitmap,
                contentDescription = "Drawing palette",
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(1f, 0.85f)
                    .onGloballyPositioned {
                        viewModel.paletteViewSize = Point(it.size.width, it.size.height)
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = { offset ->
                                viewModel.updatePalette(offset)
                            }
                        )
                    }
            )
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
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.FillWidth
) {
    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = contentDescription,
        filterQuality = FilterQuality.None,
        modifier = modifier,
        contentScale = contentScale
    )
}
