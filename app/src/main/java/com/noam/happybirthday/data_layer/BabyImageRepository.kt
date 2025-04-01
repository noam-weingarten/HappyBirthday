package com.noam.happybirthday.data_layer

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.flow.Flow

interface BabyImageRepository {
    suspend fun getBabyImage(): ImageBitmap
    suspend fun saveBabyImage(imageBitmap: ImageBitmap)
    fun refreshBabyImage()
    val latestBabyImageFlow: Flow<ImageBitmap>
}