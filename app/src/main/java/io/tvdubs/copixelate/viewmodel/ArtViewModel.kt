package io.tvdubs.copixelate.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.tvdubs.copixelate.data.ArtRepo
import io.tvdubs.copixelate.data.remote.FirebaseDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import vwaber.copixelate.art.ArtSpace
import vwaber.copixelate.art.PointF

class ArtViewModel : ViewModel() {

    private val artSpace = ArtSpace()

    private val repo = ArtRepo(
        remoteDataSource = FirebaseDataSource()
    )

    init {
        viewModelScope.launch {
            repo.stub()
        }
    }

    private val _drawingBitmapData = MutableStateFlow(artSpace.drawingBitmapData)
    private val _paletteBitmapData = MutableStateFlow(artSpace.paletteBitmapData)
    private val _paletteBorderBitmapData = MutableStateFlow(artSpace.paletteBorderBitmapData)
    private val _brushBitmapData = MutableStateFlow(artSpace.brushBitmapData)

    val drawingBitmapData = _drawingBitmapData.asStateFlow()
    val paletteBitmapData = _paletteBitmapData.asStateFlow()
    val paletteBorderBitmapData = _paletteBorderBitmapData.asStateFlow()
    val brushBitmapData = _brushBitmapData.asStateFlow()

    val brushSize = artSpace.brushSize

    fun updateDrawing(unitPosition: PointF) {
        viewModelScope.launch {

            artSpace.updateDrawing(unitPosition).fold({
                _drawingBitmapData.value = artSpace.drawingBitmapData
            }, { Log.d(javaClass.simpleName, it.toString()) })

        }
    }

    fun updatePalette(unitPosition: PointF) {
        viewModelScope.launch {

            artSpace.updatePalette(unitPosition).fold({
                _paletteBitmapData.value = artSpace.paletteBitmapData
                _paletteBorderBitmapData.value = artSpace.paletteBorderBitmapData
                _brushBitmapData.value = artSpace.brushBitmapData
            }, { Log.d(javaClass.simpleName, it.toString()) })

        }
    }

    fun updateBrush(size: Int) {
        viewModelScope.launch {

            artSpace.updateBrushSize(size)
            _brushBitmapData.value = artSpace.brushBitmapData

        }
    }

}
