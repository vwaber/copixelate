package io.tvdubs.copixelate.data.remote

import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.PropertyName
import io.tvdubs.copixelate.data.model.SizeModel
import io.tvdubs.copixelate.data.model.SpaceModel

@IgnoreExtraProperties
data class SizeJson(
    @PropertyName("x")
    val x: Int? = null,
    @PropertyName("y")
    val y: Int? = null
) {
    internal fun toModel() = SizeModel(
        x = x ?: 0,
        y = y ?: 0
    )
}

fun SizeModel.toJson() = SizeJson(
    x = x,
    y = y
)

@IgnoreExtraProperties
data class SpaceJson(
    @PropertyName("drawing_key")
    val drawingKey: String? = null,
    @PropertyName("palette_key")
    val paletteKey: String? = null,
    @PropertyName("creator_key")
    val creatorKey: String? = null,
    @PropertyName("member_keys")
    val memberKeys: List<String>? = null,
) {
    internal fun toModel() = SpaceModel(
        drawingKey = drawingKey ?: "",
        paletteKey = paletteKey ?: "",
        creatorKey = creatorKey ?: "",
        memberKeys = memberKeys ?: listOf(),
    )
}

fun SpaceModel.toJson() = SpaceJson(
    drawingKey = drawingKey,
    paletteKey = paletteKey,
    creatorKey = creatorKey,
    memberKeys = memberKeys
)
