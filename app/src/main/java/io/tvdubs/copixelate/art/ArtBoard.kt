package io.tvdubs.copixelate.art

import kotlin.math.floor
import kotlin.math.sqrt
import kotlin.random.Random

private const val DEFAULT_DRAWING_WIDTH = 32
private const val DEFAULT_DRAWING_HEIGHT = 32
private const val DEFAULT_PALETTE_WIDTH = 6
private const val DEFAULT_PALETTE_HEIGHT = 2

private const val DEFAULT_BRUSH_SIZE = 7
private val DEFAULT_BRUSH_STYLE = Brush.Style.CIRCLE

class BitmapData(val size: Point, val pixels: IntArray)

data class Point(var x: Int = 0, var y: Int = 0) {
    operator fun div(i: Int) = Point(x / i, y / i)
    operator fun div(f: Float) = PointF(x / f, y / f)
    operator fun div(p: Point) = PointF(x * 1f / p.x, y * 1f / p.y)
    operator fun plusAssign(i: Int) {
        x += i; y += i
    }

    fun area() = x * y
}

data class PointF(var x: Float = 0f, var y: Float = 0f) {
    operator fun times(f: Float) = PointF(f * x, f * y)
    operator fun times(p: PointF) = PointF(x * p.x, y * p.y)
    operator fun plus(p: PointF) = PointF(x + p.x, y + p.y)
}

class ArtBoard {

    val drawingBitmapData get() = drawing.bitmapData

    val paletteBitmapData get() = palette.bitmapData
    val paletteBorderBitmapData get() = palette.borderBitmapData
    val brushBitmapData get() = brushPreview.bitmapData

    val brushSize get() = brush.size

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

        // Center by ensuring size parity
        if (previewSize.x % 2 != brush.size % 2)
            previewSize += 1

        brushPreview.resize(previewSize, palette.previousActiveIndex)
        brushPreview.draw(brushPreview.size / 2f)
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

    val bitmapData get() = BitmapData(size, toColorPixels())

    fun draw(p: PointF) = updatePixelsWithBrush(p)

//    fun clear(paletteIndex: Int) {
//        for (i in pixels.indices) {
//            pixels[i] = paletteIndex
//        }
//    }

    fun resize(size: Point, paletteIndex: Int) {
        this.size = size
        pixels = IntArray(size.area()) { paletteIndex }
    }

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

    enum class Style { SQUARE, CIRCLE }

    private val bristles = ArrayList<PointF>()

    var size = size
        set(value) {
            field = value
            createBrush()
        }

    var style = style
        set(value) {
            field = value
            createBrush()
        }

    init {
        createBrush()
    }

    fun toBristles(position: PointF): List<PointF> =
        bristles.map { it + position }

    private fun createBrush() {
        bristles.clear()
        bristles.addAll(
            when (style) {
                Style.SQUARE -> createSquareBrush(size)
                Style.CIRCLE -> createCircleBrush(size)
            }
        )
    }

    private fun createSquareBrush(size: Int) =
        ArrayList<PointF>().apply {
            val n = size - 1
            for (x in 0..n) {
                for (y in 0..n) {
                    add(PointF(x - (n / 2f), y - (n / 2f)))
                }
            }
        }

    private fun createCircleBrush(size: Int) =
        ArrayList<PointF>().apply {
            val n = size - 1
            val r = (n + 0.5) / 2f

            for (i1 in 0..n) {
                if (n % 2 != i1 % 2) continue
                val x = i1 / 2f

                for (i2 in 0..n) {
                    if (n % 2 != i2 % 2) continue
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

}

private class Palette(
    var size: Point,
    var colors: IntArray,
    var activeIndex: Int = 0
) {

    var previousActiveIndex: Int = colors.lastIndex

    val bitmapData: BitmapData get() = BitmapData(size, colors)
    val borderBitmapData: BitmapData get() = BitmapData(Point(1, 1), IntArray(1) { currentColor })

    fun select(position: PointF) = updateActiveIndex(position)

    private val currentColor: Int get() = colors[activeIndex]

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

private fun PointF.toIndex(bounds: Point): Result<Int> =
    if (x < 0 || x > bounds.x || y < 0 || y > bounds.y)
        Result.failure(
            IndexOutOfBoundsException(
                "toIndex: Failed; Position $this exceeds valid bounds $bounds"
            )
        )
    else
        Result.success((floor(y) * bounds.x + floor(x)).toInt())
