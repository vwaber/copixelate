package io.tvdubs.copixelate.art

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.PointF
import kotlin.math.floor
import kotlin.math.sqrt
import kotlin.random.Random

private const val DEFAULT_DRAWING_WIDTH = 24
private const val DEFAULT_DRAWING_HEIGHT = 24
private const val DEFAULT_PALETTE_WIDTH = 6
private const val DEFAULT_PALETTE_HEIGHT = 2

private const val DEFAULT_BRUSH_SIZE = 5
private val DEFAULT_BRUSH_STYLE = Brush.Style.CIRCLE

private operator fun PointF.times(f: Float) = PointF(f * x, f * y)
private operator fun PointF.plus(p: PointF) = PointF(x + p.x, y + p.y)

class ArtBoard {

    private var drawing = createRandomDrawing(
        createRandomPalette(), createDefaultBrush()
    )

    val drawingBitmap get() = drawing.bitmap
    val paletteBitmap get() = drawing.palette.bitmap
    val paletteBorderBitmap get() = drawing.palette.borderBitmap

    fun updateDrawing(viewSize: Point, viewInputPosition: PointF): Result<Unit> {

        val scale = drawing.size.x / viewSize.x.toFloat()
        val scaledPosition = viewInputPosition * scale

        return drawing.draw(scaledPosition)
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

    private fun createRandomDrawing(palette: Palette, brush: Brush): Drawing {

        val randomPixels = IntArray(DEFAULT_DRAWING_WIDTH * DEFAULT_DRAWING_HEIGHT) {
            (1 until palette.colors.size).random(Random(System.nanoTime()))
        }
        val drawingSize = Point(DEFAULT_DRAWING_WIDTH, DEFAULT_DRAWING_HEIGHT)

        return Drawing(drawingSize, randomPixels, palette, brush)
    }

    private fun createDefaultBrush(): Brush {
        return Brush(DEFAULT_BRUSH_SIZE, DEFAULT_BRUSH_STYLE)
    }

}

private class Drawing(
    val size: Point,
    private val pixels: IntArray,
    val palette: Palette,
    val brush: Brush,
) {

    val bitmap get() = toBitmap()

    fun draw(p: PointF) = updatePixelsWithBrush(p)

    private fun toBitmap(palette: Palette = this.palette, drawing: Drawing = this): Bitmap =
        toBitmap(drawing.size, drawing.toColorPixels(palette, drawing))

    private fun toColorPixels(palette: Palette, drawing: Drawing): IntArray =
        IntArray(drawing.pixels.size) { palette.colors[drawing.pixels[it]] }

    private fun updatePixelsWithBrush(
        position: PointF,
        paletteIndex: Int = palette.activeIndex,
        drawing: Drawing = this,
    ): Result<Unit> =
        position.toIndex(drawing.size).fold({

            brush.bristles.forEach { bristle ->
                (position + bristle).toIndex(drawing.size).onSuccess { index ->
                    drawing.pixels[index] = paletteIndex
                }
            }

            Result.success(Unit)
        }, {
            Result.failure(it)
        })

}


private class Brush(size: Int, style: Style) {

    enum class Style(val dynamic: Boolean) {
        SQUARE(false),
        CIRCLE(false),
        SNOW(true)
    }

    private val _bristles = ArrayList<PointF>()
    val bristles: List<PointF> = _bristles

    var size = size
        set(value) = createBrush(value, style)
    var style = style
        set(value) = createBrush(size, value)

    init {
        createBrush(size, style)
    }

    private fun createBrush(size: Int, shape: Style) {
        _bristles.clear()
        _bristles.addAll(
            when (shape) {
                Style.SQUARE -> createSquareBrush(size)
                Style.CIRCLE -> createCircleBrush(size)
                else -> createSquareBrush(size)
            }
        )
    }

    private fun createSquareBrush(size: Int) =
        ArrayList<PointF>().apply {
            for (x in 0..size) {
                for (y in 0..size) {
                    add(PointF(x - (size / 2f), y - (size / 2f)))
                }
            }
        }

    private fun createCircleBrush(size: Int) =
        ArrayList<PointF>().apply {
                for (x in 0 until size) {
                    for (y in 0 until size) {
                        if (sqrt(((x * x) + (y * y)).toDouble()) <= size - 1) {
                            add(PointF(x.toFloat(), y.toFloat()))
                            add(PointF(-x.toFloat(), y.toFloat()))
                            add(PointF(-x.toFloat(), -y.toFloat()))
                            add(PointF(x.toFloat(), -y.toFloat()))
                        }
                    }
            }
        }
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
