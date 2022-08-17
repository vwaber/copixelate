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

    private val _stateFlow = MutableStateFlow(
        PixelMap(
            Point(DRAWING_SIZE, DRAWING_SIZE),
            ArrayList(IntArray(DRAWING_SIZE * DRAWING_SIZE) {
                (1 until PALETTE_SIZE).random(Random(System.nanoTime()))
            }.asList()),
            ArrayList(IntArray(PALETTE_SIZE) { Random(System.nanoTime()).nextInt() }.asList())
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

data class PixelMap(
    val size: Point,
    val pixels: ArrayList<Int>,
    val palette: ArrayList<Int>,
) {

    val drawingBitmap get() = toBitmap()
    val paletteBitmap get() = toBitmapFromPalette()

    private fun toBitmap(pixelMap: PixelMap = this.applyPalette()): Bitmap {
        return Bitmap.createBitmap(
            pixelMap.pixels.toIntArray(),
            pixelMap.size.x,
            pixelMap.size.y,
            Bitmap.Config.RGB_565
        )
    }

    private fun toBitmapFromPalette() =
        toBitmap(
            PixelMap(Point(PALETTE_SIZE / 2, 2), palette, ArrayList())
        )

    fun updatePixel(
        pixelMap: PixelMap = this,
        position: Offset,
        paletteIndex: Int
    ): Result<PixelMap> {

        position.toIndex(pixelMap.size).fold({
            return success(
                copy(pixels =
                ArrayList(pixels).apply { this[it] = paletteIndex })
            )
        }, { return failure(it) })

    }

    private fun applyPalette(pixelMap: PixelMap = this): PixelMap {
        return pixelMap.copy(pixels = pixels.applyPalette())
    }

    private fun ArrayList<Int>.applyPalette(palette: ArrayList<Int> = this@PixelMap.palette) =

        map { palette[it] } as ArrayList<Int>

    private fun Offset.toIndex(bounds: Point): Result<Int> =
        if (x < 0 || x > bounds.x || y < 0 || y > bounds.y)
            failure(IndexOutOfBoundsException("Pixel update position exceeds PixelMap bounds"))
        else
            success((floor(y) * bounds.x + floor(x)).toInt())

}
