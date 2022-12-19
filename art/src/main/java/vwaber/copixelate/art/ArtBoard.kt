package vwaber.copixelate.art

import kotlin.random.Random

private const val DEFAULT_DRAWING_WIDTH = 32
private const val DEFAULT_DRAWING_HEIGHT = 32
private const val DEFAULT_PALETTE_WIDTH = 3
private const val DEFAULT_PALETTE_HEIGHT = 2

private const val DEFAULT_BRUSH_SIZE = 7
private val DEFAULT_BRUSH_STYLE = Brush.Style.CIRCLE

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
