package com.noam.happybirthday.data_layer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.noam.happybirthday.utils.BABY_IMAGE_NAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class BabyImageRepositoryImpl(applicationContext: Context) : BabyImageRepository {
    private var cacheDirAbsolutePath : String = applicationContext.cacheDir.absolutePath

    private var hasBabyImageBeenUpdated : Boolean = true
    override val latestBabyImageFlow : Flow<ImageBitmap> = flow {
        while(true) {
            if (hasBabyImageBeenUpdated) {
                hasBabyImageBeenUpdated = false
                val bitmap = getBabyImage()
                emit(bitmap)
            }
            delay(1000)
        }
    }

    override suspend fun getBabyImage(): ImageBitmap {
        var bitmap = ImageBitmap(1,1,)
        val file = File(cacheDirAbsolutePath + File.separator + BABY_IMAGE_NAME)
        if (file.exists()) {
            bitmap = BitmapFactory.decodeFile(cacheDirAbsolutePath + File.separator + BABY_IMAGE_NAME).asImageBitmap()
        }
        return bitmap
    }

    override suspend fun saveBabyImage(imageBitmap: ImageBitmap) {
        withContext(Dispatchers.IO) {
            val imageFile = File(cacheDirAbsolutePath + File.separator + BABY_IMAGE_NAME)
            if (imageFile.exists()) {
                imageFile.delete()
            }
            val fileOutputStream: FileOutputStream = imageFile.outputStream()
            imageBitmap.asAndroidBitmap().compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            refreshBabyImage()
        }
    }

    override fun refreshBabyImage() {
        hasBabyImageBeenUpdated = true
    }

}