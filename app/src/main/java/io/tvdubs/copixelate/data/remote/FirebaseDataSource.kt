package io.tvdubs.copixelate.data.remote

import io.tvdubs.copixelate.data.ArtRepo
import io.tvdubs.copixelate.data.model.DrawingModel
import io.tvdubs.copixelate.data.model.UpdateModel
import io.tvdubs.copixelate.data.model.PaletteModel
import io.tvdubs.copixelate.data.model.SpaceModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirebaseDataSource : ArtRepo.RemoteDataSource {

    private val adapter = FirebaseAdapter()

    override suspend fun getSpace(spaceKey: String): SpaceModel =
        adapter.getSpaceJson(spaceKey).toModel()

    override suspend fun getDrawing(drawingKey: String): DrawingModel =
        coroutineScope {
            val data = async { adapter.getDrawingData(drawingKey) }
            val size = async { adapter.getSizeJson(drawingKey) }

            DrawingModel(
                size = size.await().toModel(),
                pixels = data.await(),
            )
        }

    override suspend fun getDrawingUpdateFlow(drawingKey: String): Flow<UpdateModel> =
        adapter.getDrawingDataUpdateFlow(drawingKey).map { pair ->
            UpdateModel(key = pair.first, value = pair.second)
        }

    override suspend fun setDrawing(drawingKey: String, drawingModel: DrawingModel) {
        coroutineScope {
            awaitAll(
                async { adapter.setDrawingData(drawingKey, drawingModel.pixels) },
                async { adapter.setSizeJson(drawingKey, drawingModel.size.toJson()) }
            )
        }
    }

    override suspend fun getPalette(paletteKey: String): PaletteModel =
        coroutineScope {
            val data = async { adapter.getPaletteData(paletteKey) }
            val size = async { adapter.getSizeJson(paletteKey) }

            PaletteModel(
                size = size.await().toModel(),
                pixels = data.await()
            )
        }

    override suspend fun setPalette(paletteKey: String, paletteModel: PaletteModel) {
        coroutineScope {
            awaitAll(
                async { adapter.setPaletteData(paletteKey, paletteModel.pixels) },
                async { adapter.setSizeJson(paletteKey, paletteModel.size.toJson()) }
            )
        }
    }



}
