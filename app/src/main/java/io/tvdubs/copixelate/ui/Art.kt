package io.tvdubs.copixelate.ui

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.PointF
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Slider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import io.tvdubs.copixelate.viewmodel.ArtViewModel

@Composable
fun ArtScreen(viewModel: ArtViewModel) {

    val drawingBitmap by viewModel.drawingBitmap.collectAsState()
    val paletteBitmap by viewModel.paletteBitmap.collectAsState()
    val paletteBorderBitmap by viewModel.paletteBorderBitmap.collectAsState()
    val brushBitmap by viewModel.brushBitmap.collectAsState()
    val brushSize by viewModel.brushSize.collectAsState()

    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {

        Drawing(
            bitmap = drawingBitmap,
            onDraw = { viewSize, position -> viewModel.updateDrawing(viewSize, position) }
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {

            Palette(
                bitmap = paletteBitmap,
                borderBitmap = paletteBorderBitmap,
                borderStroke = 10.dp,
                onUpdatePaletteActiveIndex = { viewSize, offset ->
                    viewModel.updatePaletteActiveIndex(viewSize, offset)
                },
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            )
            BrushPreview(
                bitmap = brushBitmap,
                modifier = Modifier.fillMaxHeight()
            )

        }// End Row
        BrushSizeSlider(
            steps = SliderSteps(1, 16, brushSize),
            onSizeChange = { size -> viewModel.updateBrush(size) },
            modifier = Modifier.padding(horizontal = 40.dp)
        )

    }// End Column
}

private fun IntSize.toPoint() = Point(width, height)
private fun Offset.toPointF() = PointF(x, y)

@Composable
private fun Drawing(
    bitmap: Bitmap,
    onDraw: (viewSize: Point, position: PointF) -> Unit
) {

    var viewSize by remember { mutableStateOf(Point()) }

    BitmapImage(
        bitmap = bitmap,
        contentDescription = "Drawing",
        contentScale = ContentScale.FillWidth,
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned {
                viewSize = it.size.toPoint()
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    onDraw(viewSize, change.position.toPointF())
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { position ->
                        onDraw(viewSize, position.toPointF())
                    })
            })
}

@Composable
private fun Palette(
    bitmap: Bitmap,
    borderBitmap: Bitmap,
    borderStroke: Dp,
    onUpdatePaletteActiveIndex: (viewSize: Point, position: PointF) -> Unit,
    modifier: Modifier = Modifier
) {

    var viewSize by remember { mutableStateOf(Point()) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        BitmapImage(
            bitmap = borderBitmap,
            contentDescription = "Drawing palette border",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        BitmapImage(
            bitmap = bitmap,
            contentDescription = "Drawing palette",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxSize()
                .padding(borderStroke)
                .onGloballyPositioned {
                    viewSize = it.size.toPoint()
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { offset ->
                            onUpdatePaletteActiveIndex(
                                viewSize,
                                offset.toPointF()
                            )
                        })
                })
    }// End Box
}

@Composable
private fun BrushPreview(
    bitmap: Bitmap,
    contentScale: ContentScale = ContentScale.FillHeight,
    modifier: Modifier) {
    BitmapImage(
        bitmap = bitmap,
        contentDescription = "Brush preview",
        contentScale = contentScale,
        modifier = modifier
    )
}

private data class SliderSteps(val min: Int, val max: Int, val default: Int) {
    val size = max - min
}

@Composable
private fun BrushSizeSlider(
    steps: SliderSteps,
    onSizeChange: (Int) -> Unit,
    modifier: Modifier
) {

    var currentStep by remember { mutableStateOf(steps.default) }

    Slider(
        value = (currentStep - steps.min) * 1f / steps.size,
        onValueChange = { newValue ->
            currentStep = (newValue * steps.size + steps.min).toInt()
            onSizeChange(currentStep)
        },
        modifier = modifier
    )
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
