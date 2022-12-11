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
    private val _brushBitmap = MutableStateFlow(artBoard.brushBitmap)
    private val _brushSize = MutableStateFlow(artBoard.brushSize)

    val drawingBitmap = _drawingBitmap.asStateFlow()
    val paletteBitmap = _paletteBitmap.asStateFlow()
    val paletteBorderBitmap = _paletteBorderBitmap.asStateFlow()
    val brushBitmap = _brushBitmap.asStateFlow()
    val brushSize = _brushSize.asStateFlow()

    fun updateDrawing(viewSize: Point, position: PointF) {
        viewModelScope.launch {

            artBoard.updateDrawing(viewSize, position).fold({
                _drawingBitmap.value = artBoard.drawingBitmap
            }, { Log.d(javaClass.simpleName, it.toString()) })

        }
    }

    fun updatePaletteActiveIndex(viewSize: Point, position: PointF) {
        viewModelScope.launch {

            artBoard.updatePaletteActiveIndex(viewSize, position).fold({
                _paletteBitmap.value = artBoard.paletteBitmap
                _paletteBorderBitmap.value = artBoard.paletteBorderBitmap
                _brushBitmap.value = artBoard.brushBitmap
            }, { Log.d(javaClass.simpleName, it.toString()) })

        }
    }

    fun updateBrush(size: Int) {
        viewModelScope.launch {

            artBoard.updateBrushSize(size)
            _brushBitmap.value = artBoard.brushBitmap

        }
    }

}
