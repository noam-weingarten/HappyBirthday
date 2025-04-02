package com.noam.happybirthday.ui.model

import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap

data class ImageViewState(
    val tempFileUrl: Uri? = null,
    val selectedPicture: ImageBitmap = ImageBitmap(1, 1),
)
