package io.tvdubs.copixelate.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import java.util.*

class AppViewModel() : ViewModel() {
    // Small random bitmap for demo purposes
    private val emptyArray = IntArray(16) { Random().nextInt() }

    val bitmap: Bitmap = Bitmap.createBitmap(emptyArray, 4, 4, Bitmap.Config.ARGB_8888)
}
