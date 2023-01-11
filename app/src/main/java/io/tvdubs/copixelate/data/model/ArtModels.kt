package io.tvdubs.copixelate.data.model

data class SpaceModel(
    val drawingKey: String,
    val paletteKey: String,
    val creatorKey: String,
    val memberKeys: List<String> = listOf(creatorKey)
)

data class PixelGridModel(
    val size: SizeModel = SizeModel(),
    val pixels: List<Int> = emptyList(),
)

typealias PaletteModel = PixelGridModel
typealias DrawingModel = PixelGridModel

data class SizeModel(
    val x: Int = 0,
    val y: Int = 0
)

data class UpdateModel(
    val key: Int,
    val value: Int
)
