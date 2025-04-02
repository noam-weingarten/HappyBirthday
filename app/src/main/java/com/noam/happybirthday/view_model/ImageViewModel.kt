package com.noam.happybirthday.view_model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.util.Log
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.CoroutineContext
import com.noam.happybirthday.BuildConfig
import androidx.core.graphics.createBitmap
import com.noam.happybirthday.data_layer.BabyImageRepository
import com.noam.happybirthday.ui.model.ImageIntent
import com.noam.happybirthday.ui.model.ImageViewState
import com.noam.happybirthday.utils.BABY_IMAGE_NAME

class ImageViewModel(private val coroutineContext: CoroutineContext, private val imageRepository: BabyImageRepository): ViewModel() {
    private val _imageViewState: MutableStateFlow<ImageViewState> = MutableStateFlow(ImageViewState())
    val viewStateFlow: StateFlow<ImageViewState>
        get() = _imageViewState

    fun onReceive(intent: ImageIntent) = viewModelScope.launch(coroutineContext) {
        when(intent) {
                    is ImageIntent.OnPermissionGrantedWith -> {
                        val imageFile = File(intent.compositionContext.cacheDir.absolutePath + File.separator + BABY_IMAGE_NAME)
                        val uriImage = FileProvider.getUriForFile(intent.compositionContext,
                            "${BuildConfig.APPLICATION_ID}.provider", /* needs to match the provider information in the manifest */
                            imageFile
                        )
                        _imageViewState.value = _imageViewState.value.copy(tempFileUrl = uriImage)
                    }

                    is ImageIntent.OnPermissionDenied -> {
                        // maybe log the permission denial event
                        Log.d("ImageViewModel","User did not grant permission to use the camera")
                    }

                    is ImageIntent.OnFinishPickingImageWith -> {
                        val imageUrl = intent.imageUrl
                        if (imageUrl != null) {
                            val inputStream = intent.compositionContext.contentResolver.openInputStream(imageUrl)
                            val bytes = inputStream?.readBytes()
                            inputStream?.close()

                            var bitmap: Bitmap = createBitmap(1, 1)
                            if (bytes != null) {
                                val bitmapOptions = BitmapFactory.Options()
                                bitmapOptions.inMutable = true
                                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bitmapOptions)
                            } else {
                                // error reading the bytes from the image url
                                Log.d("ImageViewModel","The image that was picked could not be read from the device at this url: $imageUrl")
                            }

                            val currentViewState = _imageViewState.value
                            val newCopy = currentViewState.copy(
                                selectedPicture = bitmap.asImageBitmap()
                            )
                            _imageViewState.value = newCopy
                            imageRepository.saveBabyImage(newCopy.selectedPicture)
                        }
                    }

                    is ImageIntent.OnImageSavedWith -> {
                        val tempImageUrl = _imageViewState.value.tempFileUrl
                        if (tempImageUrl != null) {
                            val source = ImageDecoder.createSource(intent.compositionContext.contentResolver, tempImageUrl)

                            val currentPicture = ImageDecoder.decodeBitmap(source).asImageBitmap()

                            _imageViewState.value = _imageViewState.value.copy(tempFileUrl = null,
                                selectedPicture = currentPicture)
                            imageRepository.refreshBabyImage()
                        }
                    }

                    is ImageIntent.OnImageSavingCanceled -> {
                        _imageViewState.value = _imageViewState.value.copy(tempFileUrl = null)
                    }
                }
            }
}