package io.tvdubs.copixelate.ui

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.PointF
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import io.tvdubs.copixelate.viewmodel.ArtViewModel

private fun IntSize.toPoint() = Point(width, height)
private fun Offset.toPointF() = PointF(x, y)

@Composable
fun ArtScreen(viewModel: ArtViewModel) {

    val drawingBitmap by viewModel.bitmap.collectAsState()
    val paletteBitmap by viewModel.paletteBitmap.collectAsState()
    val paletteBorderBitmap by viewModel.paletteBorderBitmap.collectAsState()

    var drawingViewSize by remember { mutableStateOf(Point()) }
    var paletteViewSize by remember { mutableStateOf(Point()) }

    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {

        BitmapImage(
            bitmap = drawingBitmap,
            contentDescription = "Drawing",
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned {
                    drawingViewSize = it.size.toPoint()
                }
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        viewModel.updateDrawing(drawingViewSize, change.position.toPointF())
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { position ->
                            viewModel.updateDrawing(drawingViewSize, position.toPointF())
                        })
                })

        Box(contentAlignment = Alignment.Center) {
            BitmapImage(
                bitmap = paletteBorderBitmap,
                contentDescription = "Drawing palette border",
                modifier = Modifier
                    .fillMaxWidth()
            )
            BitmapImage(
                bitmap = paletteBitmap,
                contentDescription = "Drawing palette",
                modifier = Modifier
                    .fillMaxWidth()
                    .scale(1f, 0.85f)
                    .onGloballyPositioned {
                        paletteViewSize = it.size.toPoint()
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = { offset ->
                                viewModel.updatePaletteActiveIndex(
                                    paletteViewSize,
                                    offset.toPointF()
                                )
                            })
                    })
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
private fun BitmapImage(
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
