package vwaber.copixelate.art

import kotlin.math.floor
import kotlin.math.sqrt
import kotlin.random.Random

private const val DEFAULT_DRAWING_WIDTH = 32
private const val DEFAULT_DRAWING_HEIGHT = 32
private const val DEFAULT_PALETTE_WIDTH = 3
private const val DEFAULT_PALETTE_HEIGHT = 2

private const val DEFAULT_BRUSH_SIZE = 7
private val DEFAULT_BRUSH_STYLE = Brush.Style.CIRCLE

class BitmapData(val size: Point, val pixels: IntArray)

data class Point(var x: Int = 0, var y: Int = x) {
    operator fun div(i: Int) = Point(x / i, y / i)
    operator fun div(f: Float) = PointF(x / f, y / f)
    operator fun div(p: Point) = PointF(x * 1f / p.x, y * 1f / p.y)

    operator fun plusAssign(i: Int) {
        x += i; y += i
    }

    fun area() = x * y
    fun contains(p: PointF): Boolean = !(p.x < 0 || p.x > x || p.y < 0 || p.y > y)
}

data class PointF(var x: Float = 0f, var y: Float = x) {
    operator fun times(f: Float) = PointF(f * x, f * y)
    operator fun times(p: PointF) = PointF(x * p.x, y * p.y)
    operator fun times(p: Point) = PointF(x * p.x, y * p.y)
    operator fun plus(p: PointF) = PointF(x + p.x, y + p.y)
    operator fun div(p: Point) = PointF(x / p.x, y / p.y)

    fun toIndex(bounds: Point): Int = (floor(y) * bounds.x + floor(x)).toInt()
    fun asUnitToIndex(bounds: Point): Int = (this * bounds).run { toIndex(bounds) }
    fun isUnit() = Point(1).contains(this)
}

private fun List<PointF>.toIndexes(bounds: Point): IntArray = run {
    toMutableList().apply {
        retainAll { point -> bounds.contains(point) }
    }.run { IntArray(size) { this[it].toIndex(bounds) } }
}

class ArtBoard {

    val drawingBitmapData get() = drawing.bitmapData
    val paletteBitmapData get() = palette.bitmapData
    val paletteBorderBitmapData get() = palette.borderBitmapData
    val brushBitmapData get() = brushPreview.bitmapData
    val brushSize get() = brush.size

    private val palette = createDefaultPalette()
    private val brush = createDefaultBrush()
    private val drawing = createDefaultDrawing()

    private val brushPreview = Drawing()

    init {
        refreshBrushPreviewBitmap()
    }

    private fun refreshBrushPreviewBitmap() {

        var previewSize = drawing.size / 3

        if (previewSize.x <= brush.size)
            previewSize = Point(brush.size + 1, brush.size + 1)

        // Center by ensuring size parity
        if (previewSize.x % 2 != brush.size % 2)
            previewSize += 1

        val drawPosition = previewSize / 2f
        val drawIndexes = brush
            .toPointsAt(drawPosition)
            .toIndexes(previewSize)

        brushPreview
            .resize(previewSize)
            .clear(palette.previousActiveIndex)
            .recolor(palette.colors)
            .draw(
                indexes = drawIndexes,
                pixelIndex = palette.activeIndex,
                pixelColor = palette.activeColor
            )
    }

    fun updateBrushSize(size: Int) {
        brush.restyle(size = size)
        refreshBrushPreviewBitmap()
    }

    fun updateDrawing(unitPosition: PointF): Result<Unit> {

        if (!unitPosition.isUnit()) {
            return Result.failure(
                IllegalArgumentException(
                    "updateDrawing: unitPosition $unitPosition ain't normal"
                )
            )
        }

        val drawPosition = unitPosition * drawing.size

        drawing.draw(
            indexes = brush
                .toPointsAt(drawPosition)
                .toIndexes(drawing.size),
            pixelIndex = palette.activeIndex,
            pixelColor = palette.activeColor
        )

        return Result.success(Unit)
    }

    fun updatePalette(unitPosition: PointF): Result<Unit> {

        if (!unitPosition.isUnit()) {
            return Result.failure(
                IllegalArgumentException(
                    "updatePalette: Failed; unitPosition $unitPosition ain't normal"
                )
            )
        }

        val paletteIndex = unitPosition.asUnitToIndex(palette.size)

        if (paletteIndex == palette.activeIndex) {
            return Result.failure(
                IllegalArgumentException(
                    "updatePalette: Failed; New value is same as current"
                )
            )
        }

        palette.select(paletteIndex)
        refreshBrushPreviewBitmap()

        return Result.success(Unit)
    }

    private fun createDefaultPalette() =
        Palette()
            .resize(size = Point(DEFAULT_PALETTE_WIDTH, DEFAULT_PALETTE_HEIGHT))
            .clear { Random(System.nanoTime()).nextInt() }

    private fun createDefaultDrawing() =
        Drawing()
            .resize(Point(DEFAULT_DRAWING_WIDTH, DEFAULT_DRAWING_HEIGHT))
            .clear { index: Int -> (index / 5) % palette.colors.size }
            .recolor(palette.colors)

    private fun createDefaultBrush() =
        Brush()
            .restyle(DEFAULT_BRUSH_STYLE, DEFAULT_BRUSH_SIZE)

}

private class Drawing {

    private var indexPixels = IntArray(0)
    private var colorPixels = IntArray(0)

    var size: Point = Point()
        private set

    val bitmapData get() = BitmapData(size, colorPixels)

    fun resize(size: Point): Drawing = apply {
        this.size = size
        indexPixels = IntArray(size.area()) { 0 }
    }

    fun recolor(colors: IntArray): Drawing = apply {
        colorPixels = IntArray(indexPixels.size) { i -> colors[indexPixels[i]] }
    }

    fun clear(paletteIndex: Int): Drawing = apply {
        for (i in indexPixels.indices) {
            indexPixels[i] = paletteIndex
        }
    }

    fun clear(mutator: (index: Int) -> Int) = apply {
        indexPixels = IntArray(size.area(), mutator)
    }

    fun draw(indexes: IntArray, pixelIndex: Int, pixelColor: Int) {
        indexes.forEach { index ->
            indexPixels[index] = pixelIndex
            colorPixels[index] = pixelColor
        }
    }

}

private class Palette {

    var size: Point = Point()
        private set
    var colors: IntArray = IntArray(0) { 0 }
        private set
    var activeIndex: Int = 0
        private set

    val activeColor: Int get() = colors[activeIndex]

    val previousActiveIndex: Int get() = previousActiveIndexes[0]
    var previousActiveIndexes = ArrayDeque<Int>(0)
        private set

    val bitmapData: BitmapData
        get() = BitmapData(size, colors)
    val borderBitmapData: BitmapData
        get() = BitmapData(Point(1), IntArray(1) { activeColor })

    fun resize(size: Point) = apply {
        this.size = size
        colors = IntArray(size.area()) { 0 }
    }

    fun clear(mutator: (index: Int) -> Int) = apply {
        colors = IntArray(size.area(), mutator)
        previousActiveIndexes = ArrayDeque(
            List(3) { index -> colors.lastIndex - index })
    }

    fun select(index: Int) {
        previousActiveIndexes.apply { addFirst(activeIndex); removeLast() }
        activeIndex = index
    }

}

private class Brush {

    enum class Style { SQUARE, CIRCLE }

    var size: Int = 0
    var style: Style = Style.CIRCLE

    private val bristles = ArrayList<PointF>()

    fun restyle(style: Style = this.style, size: Int = this.size) = apply {
        this.style = style
        this.size = size
        build()
    }

    fun toPointsAt(position: PointF): List<PointF> =
        bristles.map { it + position }

    private fun build() = apply {
        bristles.clear()
        bristles.addAll(
            when (style) {
                Style.SQUARE -> buildSquareBrush(size)
                Style.CIRCLE -> buildCircleBrush(size)
            }
        )
    }

    private fun buildSquareBrush(size: Int) =
        ArrayList<PointF>().apply {
            val n = size - 1
            for (x in 0..n) {
                for (y in 0..n) {
                    add(PointF(x - (n / 2f), y - (n / 2f)))
                }
            }
        }

    private fun buildCircleBrush(size: Int) =
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
