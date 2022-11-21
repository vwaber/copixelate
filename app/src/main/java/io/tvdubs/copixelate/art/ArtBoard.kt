package io.tvdubs.copixelate.art

import android.graphics.Bitmap
import android.graphics.Point
import android.graphics.PointF
import kotlin.math.floor
import kotlin.math.sqrt
import kotlin.random.Random

private const val DEFAULT_DRAWING_WIDTH = 32
private const val DEFAULT_DRAWING_HEIGHT = 32
private const val DEFAULT_PALETTE_WIDTH = 6
private const val DEFAULT_PALETTE_HEIGHT = 2

private const val DEFAULT_BRUSH_SIZE = 5
private val DEFAULT_BRUSH_STYLE = Brush.Style.CIRCLE

private operator fun PointF.times(f: Float) = PointF(f * x, f * y)
private operator fun PointF.times(p: PointF) = PointF(x * p.x, y * p.y)
private operator fun PointF.plus(p: PointF) = PointF(x + p.x, y + p.y)
private operator fun Point.div(i: Int) = Point(x / i, y / i)
private operator fun Point.div(f: Float) = PointF(x / f, y / f)
private operator fun Point.div(p: Point) = PointF(x * 1f / p.x, y * 1f / p.y)
private operator fun Point.plusAssign(i: Int) {
    x += i; y += i
}

private fun Point.area() = x * y

class ArtBoard {

    val drawingBitmap get() = drawing.bitmap
    val paletteBitmap get() = palette.bitmap
    val paletteBorderBitmap get() = palette.borderBitmap
    val brushBitmap get() = brushPreview.bitmap

    private val palette = createRandomPalette()
    private val brush = createDefaultBrush()
    private val drawing = createRandomDrawing(palette, brush)

    private val brushPreview = Drawing(
        size = Point(),
        pixels = IntArray(0),
        palette,
        brush
    )

    init {
        refreshBrushPreviewBitmap()
    }

    fun updateBrushSize(size: Int) {
        brush.size = size
        refreshBrushPreviewBitmap()
    }

    private fun refreshBrushPreviewBitmap() {

        var previewSize = drawing.size / 3

        if (previewSize.x <= brush.size)
            previewSize = Point(brush.size + 1, brush.size + 1)

        if (previewSize.x % 2 != brush.size % 2)
            previewSize += 1

        brushPreview.resize(previewSize, palette.previousActiveIndex)
        brushPreview.draw(brushPreview.size / 2f)
    }

    fun createPaletteBorderBitmap() {

    }


    fun updateDrawing(viewSize: Point, viewInputPosition: PointF): Result<Unit> {
        val scaledPosition = viewInputPosition * (drawing.size / viewSize)
        return drawing.draw(scaledPosition)
    }

    fun updatePaletteActiveIndex(viewSize: Point, viewInputPosition: PointF): Result<Unit> {
        val scaledPosition = viewInputPosition * (palette.size / viewSize)
        return palette.select(scaledPosition).fold({
            refreshBrushPreviewBitmap()
            Result.success(Unit)
        }, { Result.failure(it) })
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
    var size: Point,
    var pixels: IntArray,
    val palette: Palette,
    val brush: Brush,
) {

    val bitmap get() = toBitmap()

    fun draw(p: PointF) = updatePixelsWithBrush(p)

    fun clear(paletteIndex: Int) {
        for (i in pixels.indices) {
            pixels[i] = paletteIndex
        }
    }

    fun resize(size: Point, paletteIndex: Int) {
        this.size = size
        pixels = IntArray(size.area()) { paletteIndex }
    }

    private fun toBitmap(): Bitmap =
        toBitmap(size, toColorPixels())

    private fun toColorPixels(): IntArray =
        IntArray(pixels.size) { palette.colors[pixels[it]] }

    private fun updatePixelsWithBrush(position: PointF): Result<Unit> =
        position.toIndex(size).fold({
            brush.toBristles(position).forEach { bristle ->
                bristle.toIndex(size).onSuccess { index ->
                    pixels[index] = palette.activeIndex
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

    private val bristles = ArrayList<PointF>()

    var size = size
        set(value) {
            field = value
            createBrush()
        }

    var style = style
        set(_) = createBrush()

    init {
        createBrush()

    }

    fun toBristles(position: PointF): List<PointF> =
        bristles.map { it + position }.also {
            if (style.dynamic) createBrush()
        }

    private fun createBrush() {
        val size = size - 1
        bristles.clear()
        bristles.addAll(
            when (style) {
                Style.SQUARE -> createSquareBrush(size)
                Style.CIRCLE -> createCircleBrush(size)
                Style.SNOW -> createSnowBrush(size)
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
            val r = (size + 0.5) / 2f

            for (i1 in 0..size) {
                if (size % 2 != i1 % 2) continue
                val x = i1 / 2f

                for (i2 in 0..size) {
                    if (size % 2 != i2 % 2) continue
                    val y = i2 / 2f

                    if (sqrt((x * x) + (y * y)) <= r) {
                        add(PointF(x, y))
                        if (x != 0f) add(PointF(-x, y))
                        if (y != 0f) add(PointF(x, -y))
                        if (x != 0f && y != 0f) add(PointF(-x, -y))
                    }
                }
            }
        }

    private fun createSnowBrush(size: Int) =
        ArrayList<PointF>().apply {
            for (x in 0..size) {
                for (y in 0..size) {
                    val rand = (0..(size + 5) * (size + 5)).random(Random(System.nanoTime()))
                    if (rand == 0) {
                        add(PointF(x - (size / 2f), y - (size / 2f)))
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

    var previousActiveIndex: Int = colors.lastIndex

    val bitmap: Bitmap get() = toBitmap()
    val borderBitmap: Bitmap get() = toBitmap(Point(1, 1), IntArray(1) { currentColor })

    fun select(position: PointF) = updateActiveIndex(position)

    private val currentColor: Int get() = colors[activeIndex]

    private fun toBitmap(palette: Palette = this) =
        toBitmap(palette.size, palette.colors)

    private fun updateActiveIndex(position: PointF, palette: Palette = this): Result<Unit> =
        position.toIndex(palette.size).fold({ newActiveIndex ->
            if (palette.activeIndex == newActiveIndex) {
                return Result.failure(
                    IllegalArgumentException(
                        "updateActiveIndex: Failed; Argument matches current value"
                    )
                )
            }
            palette.previousActiveIndex = palette.activeIndex
            palette.activeIndex = newActiveIndex
            return Result.success(Unit)
        }, {
            return Result.failure(it)
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
                "toIndex: Failed; Position $this exceeds valid bounds $bounds"
            )
        )
    else
        Result.success((floor(y) * bounds.x + floor(x)).toInt())
