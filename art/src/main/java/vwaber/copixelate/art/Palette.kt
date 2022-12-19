package vwaber.copixelate.art

internal class Palette {

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
