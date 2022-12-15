package io.tvdubs.copixelate.ui

import android.graphics.Bitmap
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
import io.tvdubs.copixelate.art.BitmapData
import io.tvdubs.copixelate.art.Point
import io.tvdubs.copixelate.art.PointF
import io.tvdubs.copixelate.viewmodel.ArtViewModel

@Composable
fun ArtScreen(viewModel: ArtViewModel) {

    val drawingBitmapData by viewModel.drawingBitmapData.collectAsState()

    val paletteBitmapData by viewModel.paletteBitmapData.collectAsState()
    val paletteBorderBitmapData by viewModel.paletteBorderBitmapData.collectAsState()
    val brushBitmapData by viewModel.brushBitmapData.collectAsState()
    val brushSize by viewModel.brushSize.collectAsState()

    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {

        Drawing(
            bitmapData = drawingBitmapData,
            onDraw = { unitPosition -> viewModel.updateDrawing(unitPosition) }
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {

            Palette(
                bitmapData = paletteBitmapData,
                borderBitmapData = paletteBorderBitmapData,
                borderStroke = 10.dp,
                onUpdatePaletteActiveIndex = { unitPosition ->
                    viewModel.updatePaletteActiveIndex(unitPosition)
                },
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            )
            BrushPreview(
                bitmapData = brushBitmapData,
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
    bitmapData: BitmapData,
    onDraw: (unitPosition: PointF) -> Unit
) {

    var viewSize by remember { mutableStateOf(Point()) }

    BitmapImage(
        bitmapData = bitmapData,
        contentDescription = "Drawing",
        contentScale = ContentScale.FillWidth,
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned {
                viewSize = it.size.toPoint()
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    onDraw(change.position.toPointF() / viewSize)
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { position ->
                        onDraw(position.toPointF() / viewSize)
                    })
            })
}

@Composable
private fun Palette(
    bitmapData: BitmapData,
    borderBitmapData: BitmapData,
    borderStroke: Dp,
    onUpdatePaletteActiveIndex: (unitPosition: PointF) -> Unit,
    modifier: Modifier = Modifier
) {

    var viewSize by remember { mutableStateOf(Point()) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        BitmapImage(
            bitmapData = borderBitmapData,
            contentDescription = "Drawing palette border",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
        BitmapImage(
            bitmapData = bitmapData,
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
                            onUpdatePaletteActiveIndex(offset.toPointF() / viewSize)
                        })
                })
    }// End Box
}

@Composable
private fun BrushPreview(
    bitmapData: BitmapData,
    contentScale: ContentScale = ContentScale.FillHeight,
    modifier: Modifier
) {
    BitmapImage(
        bitmapData = bitmapData,
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
 * A composable that lays out and draws a given [Bitmap] from [BitmapData] without filtering
 *
 * @param bitmapData The [BitmapData] to draw unfiltered
 * @param contentDescription text used by accessibility services to describe what this image
 */
@Composable
private fun BitmapImage(
    bitmapData: BitmapData,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale
) {

    val bitmap = Bitmap.createBitmap(
        bitmapData.pixels,
        bitmapData.size.x,
        bitmapData.size.y,
        Bitmap.Config.RGB_565
    )

    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = contentDescription,
        filterQuality = FilterQuality.None,
        modifier = modifier,
        contentScale = contentScale
    )
}
