package io.tvdubs.copixelate.data

import android.util.Log
import io.tvdubs.copixelate.data.model.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ArtRepo(
    private val remoteDataSource: RemoteDataSource,
    // private val localDataSource: LocalDataSource,
) {

    interface RemoteDataSource {
        suspend fun getSpace(spaceKey: String): SpaceModel
        suspend fun getDrawing(drawingKey: String): DrawingModel
        suspend fun setDrawing(drawingKey: String, drawingModel: DrawingModel)
        suspend fun getDrawingUpdateFlow(drawingKey: String): Flow<UpdateModel>
        suspend fun getPalette(paletteKey: String): PaletteModel
        suspend fun setPalette(paletteKey: String, paletteModel: PaletteModel)
    }

    interface LocalDataSource {

    }

    suspend fun stub() {

        if(Auth.state == Auth.State.SIGNED_OUT) return

        coroutineScope {
            Log.d("GET_SPACE", remoteDataSource.getSpace("space_1").toString())
            Log.d("GET_DRAWING", remoteDataSource.getDrawing("drawing_1").toString())
            Log.d("GET_PALETTE", remoteDataSource.getPalette("palette_1").toString())

            launch {
                remoteDataSource.getDrawingUpdateFlow("drawing_1").collect { model ->
                    Log.d("GET_DRAWING_UPDATE", model.toString())
                }
            }

            remoteDataSource.setDrawing(
                "drawing_3",
                DrawingModel(
                    pixels = listOf(1, 2, 3, 4, 5, 6),
                    size = SizeModel(2, 3)
                )
            )
        }

    }

}
