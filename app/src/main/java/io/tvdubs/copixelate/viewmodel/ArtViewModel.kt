package io.tvdubs.copixelate.viewmodel

import android.graphics.Point
import android.graphics.PointF
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import io.tvdubs.copixelate.art.ArtBoard

class ArtViewModel : ViewModel() {

    private val artBoard = ArtBoard()

    private val _drawingBitmap = MutableStateFlow(artBoard.drawingBitmap)
    private val _paletteBitmap = MutableStateFlow(artBoard.paletteBitmap)
    private val _paletteBorderBitmap = MutableStateFlow(artBoard.paletteBorderBitmap)

    val bitmap = _drawingBitmap.asStateFlow()
    val paletteBitmap = _paletteBitmap.asStateFlow()
    val paletteBorderBitmap = _paletteBorderBitmap.asStateFlow()

    fun updateDrawing(viewSize: Point, position: PointF) {
        artBoard.updateDrawing(viewSize, position).fold({
            viewModelScope.launch {
                _drawingBitmap.emit(artBoard.drawingBitmap)
            }
        }, { Log.d(javaClass.simpleName, it.toString()) })
    }

    fun updatePaletteActiveIndex(viewSize: Point, position: PointF) {
        artBoard.updatePaletteActiveIndex(viewSize, position).fold({
            viewModelScope.launch {
                _paletteBitmap.emit(artBoard.paletteBitmap)
                _paletteBorderBitmap.emit(artBoard.paletteBorderBitmap)
            }
        }, { Log.d(javaClass.simpleName, it.toString()) })
    }

}
