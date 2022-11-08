package io.tvdubs.copixelate.art

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.PointF
import kotlin.math.floor
import kotlin.random.Random

private const val DEFAULT_DRAWING_WIDTH = 24
private const val DEFAULT_DRAWING_HEIGHT = 24
private const val DEFAULT_PALETTE_WIDTH = 6
private const val DEFAULT_PALETTE_HEIGHT = 2

private operator fun PointF.times(f: Float): PointF = apply { x *= f; y *= f }

class ArtBoard {

    private var drawing = createRandomDrawing(createRandomPalette())

    val drawingBitmap get() = drawing.bitmap
    val paletteBitmap get() = drawing.palette.bitmap
    val paletteBorderBitmap get() = drawing.palette.borderBitmap

    fun updateDrawing(viewSize: Point, viewInputPosition: PointF): Result<Unit> {

        val scale = drawing.size.x / viewSize.x.toFloat()
        val scaledPosition = viewInputPosition * scale

        return drawing.updatePixel(scaledPosition)
    }

    fun updatePaletteActiveIndex(viewSize: Point, viewInputPosition: PointF): Result<Unit> {

        val scale = drawing.palette.size.x / viewSize.x.toFloat()
        val scaledPosition = viewInputPosition * scale

        return drawing.palette.updateActiveIndex(scaledPosition)
    }

    private fun createRandomPalette(): Palette {

        val randomPixels = IntArray(DEFAULT_PALETTE_WIDTH * DEFAULT_PALETTE_HEIGHT) {
            Random(System.nanoTime()).nextInt()
        }
        val paletteSize = Point(DEFAULT_PALETTE_WIDTH, DEFAULT_PALETTE_HEIGHT)

        return Palette(paletteSize, randomPixels)
    }

    private fun createRandomDrawing(palette: Palette): Drawing {

        val randomPixels = IntArray(DEFAULT_DRAWING_WIDTH * DEFAULT_DRAWING_HEIGHT) {
            (1 until palette.colors.size).random(Random(System.nanoTime()))
        }
        val drawingSize = Point(DEFAULT_DRAWING_WIDTH, DEFAULT_DRAWING_HEIGHT)

        return Drawing(drawingSize, randomPixels, palette)
    }

}

private class Drawing(
    var size: Point,
    var pixels: IntArray,
    var palette: Palette,
) {

    val bitmap get() = toBitmap()

    private fun toBitmap(palette: Palette = this.palette, drawing: Drawing = this): Bitmap =
        toBitmap(drawing.size, drawing.toColorPixels(palette, drawing))

    private fun toColorPixels(palette: Palette, drawing: Drawing): IntArray =
        IntArray(drawing.pixels.size) { palette.colors[drawing.pixels[it]] }

    fun updatePixel(
        position: PointF,
        paletteIndex: Int = palette.activeIndex,
        drawing: Drawing = this,
    ): Result<Unit> =
        position.toIndex(drawing.size).fold({
            drawing.pixels[it] = paletteIndex
            Result.success(Unit)
        }, {
            Result.failure(it)
        })

}

private class Palette(
    var size: Point,
    var colors: IntArray,
    var activeIndex: Int = 0
) {

    val bitmap: Bitmap get() = toBitmap()
    val borderBitmap: Bitmap get() = toBitmap(size, IntArray(colors.size) { currentColor })

    private val currentColor: Int get() = colors[activeIndex]

    private fun toBitmap(palette: Palette = this) =
        toBitmap(palette.size, palette.colors)

    fun updateActiveIndex(position: PointF, palette: Palette = this): Result<Unit> =
        position.toIndex(palette.size).fold({ newActiveIndex ->
            palette.activeIndex = newActiveIndex
            Result.success(Unit)
        }, {
            Result.failure(it)
        })

}

private fun toBitmap(size: Point, pixels: IntArray): Bitmap =
    Bitmap.createBitmap(
        pixels,
        size.x,
        size.y,
        Bitmap.Config.RGB_565
    )

private fun PointF.toIndex(bounds: Point): Result<Int> =
    if (x < 0 || x > bounds.x || y < 0 || y > bounds.y)
        Result.failure(
            IndexOutOfBoundsException(
                "toIndex: Failed; Position ${this.toString()} exceeds valid bounds ${bounds.toString()}"
            )
        )
    else
        Result.success((floor(y) * bounds.x + floor(x)).toInt())
