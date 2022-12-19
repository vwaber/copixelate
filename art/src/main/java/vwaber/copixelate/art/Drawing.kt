package vwaber.copixelate.art

internal class Drawing {

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
