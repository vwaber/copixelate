package vwaber.copixelate.art

import kotlin.math.sqrt

internal class Brush {

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
