package io.tvdubs.copixelate.viewmodel

import android.graphics.Bitmap
import android.graphics.Point
import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.Result.Companion.failure
import kotlin.Result.Companion.success
import kotlin.math.floor
import kotlin.random.Random

const val PIXEL_MAP_WIDTH = 24
const val PIXEL_MAP_HEIGHT = 24
const val PALETTE_WIDTH = 6
const val PALETTE_HEIGHT = 2

class ArtViewModel : ViewModel() {

    lateinit var pixelMapViewSize: Point
    lateinit var paletteViewSize: Point

    private val randomDrawing = ArrayList(IntArray(PIXEL_MAP_WIDTH * PIXEL_MAP_HEIGHT) {
        (1 until PALETTE_WIDTH * PALETTE_HEIGHT).random(Random(System.nanoTime()))
    }.asList())

    private val randomPalette =
        ArrayList(IntArray(PALETTE_WIDTH * PALETTE_HEIGHT) { Random(System.nanoTime()).nextInt() }.asList())

    private val paletteSize = Point(PALETTE_WIDTH, PALETTE_HEIGHT)
    private val drawingSize = Point(PIXEL_MAP_WIDTH, PIXEL_MAP_HEIGHT)

    private val _stateFlow = MutableStateFlow(
        PixelMap(
            drawingSize,
            randomDrawing,
            Palette(paletteSize, randomPalette, 0)
        )
    )

    val stateFlow = _stateFlow.asStateFlow()

    fun updatePalette(position: Offset) {

        val scaleRatioX = stateFlow.value.palette.size.x.toFloat() / paletteViewSize.x
        val scaleRatioY = stateFlow.value.palette.size.y.toFloat() / paletteViewSize.y
        val scaledPosition = Offset(position.x * scaleRatioX, position.y * scaleRatioY)

        stateFlow.value.palette.updateCurrentIndex(position = scaledPosition).fold({ newPalette ->
            val newPixelMap = stateFlow.value.copy(palette = newPalette)
            viewModelScope.launch { _stateFlow.emit(newPixelMap) }
        }, {
            Log.i(javaClass.simpleName, it.toString())
        })
    }

    fun updatePixelMap(position: Offset) {

        val scaleRatioX = stateFlow.value.size.x.toFloat() / pixelMapViewSize.x
        val scaleRatioY = stateFlow.value.size.y.toFloat() / pixelMapViewSize.y
        val scaledPosition = Offset(position.x * scaleRatioX, position.y * scaleRatioY)

        stateFlow.value.updatePixel(position = scaledPosition)
            .fold({ newPixelMap ->
                viewModelScope.launch { _stateFlow.emit(newPixelMap) }
            }, {
                Log.i(javaClass.simpleName, it.toString())
            })

    }

}

private fun toBitmap(size: Point, pixels: ArrayList<Int>) = toBitmap(size, pixels.toIntArray())

private fun toBitmap(size: Point, pixels: IntArray): Bitmap {
    return Bitmap.createBitmap(
        pixels,
        size.x,
        size.y,
        Bitmap.Config.RGB_565
    )
}

data class Palette(
    val size: Point,
    val pixels: ArrayList<Int>,
    val currentIndex: Int
) {

    val bitmap get() = toBitmap()
    val borderBitmap: Bitmap get() = toBitmap(size, IntArray(pixels.size) { currentColor })

    private val currentColor: Int get() = pixels[currentIndex]

    private fun toBitmap(palette: Palette = this) =
        toBitmap(palette.size, palette.pixels)

    fun updateCurrentIndex(
        palette: Palette = this,
        position: Offset
    ): Result<Palette> {

        position.toIndex(palette.size).fold({ newCurrentIndex ->
            return success(
                copy(currentIndex = newCurrentIndex)
            )
        }, { return failure(it) })

    }

    private fun Offset.toIndex(bounds: Point): Result<Int> =
        if (x < 0 || x > bounds.x || y < 0 || y > bounds.y)
            failure(IndexOutOfBoundsException("Palette selection position exceeds bounds"))
        else
            success((floor(y) * bounds.x + floor(x)).toInt())

}

data class PixelMap(
    val size: Point,
    val pixels: ArrayList<Int>,
    val palette: Palette,
) {

    val bitmap get() = toBitmap()

    private fun toBitmap(pixelMap: PixelMap = this, palette: Palette = this.palette): Bitmap {
        val newPixelMap = applyPaletteToPixelMap(palette, pixelMap)
        return toBitmap(newPixelMap.size, newPixelMap.pixels)
    }

    private fun applyPaletteToPixelMap(palette: Palette, pixelMap: PixelMap): PixelMap =
        pixelMap.copy(pixels = pixels.applyPaletteToArrayList(palette))

    private fun ArrayList<Int>.applyPaletteToArrayList(palette: Palette) =
        map { palette.pixels[it] } as ArrayList<Int>

    fun updatePixel(
        pixelMap: PixelMap = this,
        position: Offset,
        paletteIndex: Int = palette.currentIndex
    ): Result<PixelMap> {

        position.toIndex(pixelMap.size).fold({
            return success(
                copy(pixels =
                ArrayList(pixels).apply { this[it] = paletteIndex })
            )
        }, { return failure(it) })

    }

    private fun Offset.toIndex(bounds: Point): Result<Int> =
        if (x < 0 || x > bounds.x || y < 0 || y > bounds.y)
            failure(IndexOutOfBoundsException("Pixel update position exceeds PixelMap bounds"))
        else
            success((floor(y) * bounds.x + floor(x)).toInt())

}
