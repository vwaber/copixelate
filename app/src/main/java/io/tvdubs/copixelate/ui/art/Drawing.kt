package io.tvdubs.copixelate.ui.art

import android.graphics.Bitmap
import java.util.Random
import kotlin.collections.ArrayList

class Drawing {

    data class Pixelmap(
        val width: Int,
        val height: Int,
        val pixels: ArrayList<Int>
    ) {

        private fun toBitmap(pixelmap: Pixelmap): Bitmap {
            return Bitmap.createBitmap(
                pixelmap.pixels.toIntArray(),
                pixelmap.width,
                pixelmap.height,
                Bitmap.Config.RGB_565
            )
        }

        fun toBitmap(): Bitmap {
            return toBitmap(this)
        }

        private fun toBitmapWithPalette(pixelmap: Pixelmap, palette: Pixelmap): Bitmap {
            val data = pixelmap.pixels.map { palette.pixels[it] }.toIntArray()
            return Bitmap.createBitmap(data, pixelmap.width, pixelmap.height, Bitmap.Config.RGB_565)
        }

        fun toBitmapWithPalette(palette: Pixelmap): Bitmap {
            return toBitmapWithPalette(this, palette)
        }

    }

    val drawingBitmap get() = drawing.toBitmapWithPalette(palette)
    val paletteBitmap get() = palette.toBitmap()

    private val palette = Pixelmap(
        8, 2,
        ArrayList(IntArray(21) { Random().nextInt() }.asList())
    )

    private val drawing = Pixelmap(
        32, 32,
        ArrayList(IntArray(1024) { (0 until palette.pixels.size).random() }.asList())
    )

}
