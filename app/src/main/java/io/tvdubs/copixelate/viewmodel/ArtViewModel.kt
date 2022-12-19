package io.tvdubs.copixelate.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import io.tvdubs.copixelate.art.ArtBoard
import io.tvdubs.copixelate.art.PointF

class ArtViewModel : ViewModel() {

    private val artBoard = ArtBoard()

    private val _drawingBitmapData = MutableStateFlow(artBoard.drawingBitmapData)

    private val _paletteBitmapData = MutableStateFlow(artBoard.paletteBitmapData)
    private val _paletteBorderBitmapData = MutableStateFlow(artBoard.paletteBorderBitmapData)
    private val _brushBitmapData = MutableStateFlow(artBoard.brushBitmapData)

    private val _brushSize = MutableStateFlow(artBoard.brushSize)

    val drawingBitmapData = _drawingBitmapData.asStateFlow()

    val paletteBitmapData = _paletteBitmapData.asStateFlow()
    val paletteBorderBitmapData = _paletteBorderBitmapData.asStateFlow()
    val brushBitmapData = _brushBitmapData.asStateFlow()
    val brushSize = _brushSize.asStateFlow()

    fun updateDrawing(unitPosition: PointF) {
        viewModelScope.launch {

            artBoard.updateDrawing(unitPosition).fold({
                _drawingBitmapData.value = artBoard.drawingBitmapData
            }, { Log.d(javaClass.simpleName, it.toString()) })

        }
    }

    fun updatePaletteActiveIndex(unitPosition: PointF) {
        viewModelScope.launch {

            artBoard.updatePalette(unitPosition).fold({
                _paletteBitmapData.value = artBoard.paletteBitmapData
                _paletteBorderBitmapData.value = artBoard.paletteBorderBitmapData
                _brushBitmapData.value = artBoard.brushBitmapData
            }, { Log.d(javaClass.simpleName, it.toString()) })

        }
    }

    fun updateBrush(size: Int) {
        viewModelScope.launch {

            artBoard.updateBrushSize(size)
            _brushBitmapData.value = artBoard.brushBitmapData

        }
    }

}
