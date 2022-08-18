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
import kotlin.properties.Delegates
import kotlin.random.Random

const val DRAWING_SIZE = 24
const val PALETTE_SIZE = 12

class ArtViewModel : ViewModel() {

    private val randomDrawing = ArrayList(IntArray(DRAWING_SIZE * DRAWING_SIZE) {
        (1 until PALETTE_SIZE).random(Random(System.nanoTime()))
    }.asList())

    private val randomPalette =
        ArrayList(IntArray(PALETTE_SIZE) { Random(System.nanoTime()).nextInt() }.asList())

    private val paletteSize = Point(PALETTE_SIZE / 2, 2)
    private val drawingSize = Point(DRAWING_SIZE, DRAWING_SIZE)

    private val _stateFlow = MutableStateFlow(
        PixelMap(
            drawingSize,
            randomDrawing,
            Palette(paletteSize, randomPalette)
        )
    )

    val stateFlow = _stateFlow.asStateFlow()

    private var scaleRatio by Delegates.notNull<Float>()

    var viewSize: Point = Point()
        set(value) {
            field = value
            scaleRatio = stateFlow.value.size.x.toFloat() / value.x
        }

    fun updatePixel(position: Offset, paletteIndex: Int) {

        val scaledPosition = Offset(position.x * scaleRatio, position.y * scaleRatio)

        stateFlow.value.updatePixel(position = scaledPosition, paletteIndex = paletteIndex)
            .fold({
                viewModelScope.launch { _stateFlow.emit(it) }
            }, {
                Log.i(javaClass.simpleName, it.toString())
            })

    }

}

private fun toBitmap(size: Point, pixels: ArrayList<Int>): Bitmap {
    return Bitmap.createBitmap(
        pixels.toIntArray(),
        size.x,
        size.y,
        Bitmap.Config.RGB_565
    )
}

data class Palette(
    val size: Point,
    val pixels: ArrayList<Int>,
) {

    val bitmap get() = toBitmap()

    private fun toBitmap(palette: Palette = this) =
        toBitmap(palette.size, palette.pixels)

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
        paletteIndex: Int
    ): Result<PixelMap> {

        position.toIndexFromPoint(pixelMap.size).fold({
            return success(
                copy(pixels =
                ArrayList(pixels).apply { this[it] = paletteIndex })
            )
        }, { return failure(it) })

    }

    private fun Offset.toIndexFromPoint(bounds: Point): Result<Int> =
        if (x < 0 || x > bounds.x || y < 0 || y > bounds.y)
            failure(IndexOutOfBoundsException("Pixel update position exceeds PixelMap bounds"))
        else
            success((floor(y) * bounds.x + floor(x)).toInt())

}
