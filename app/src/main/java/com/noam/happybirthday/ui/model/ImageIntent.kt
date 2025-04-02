package com.noam.happybirthday.ui.model

import android.content.Context
import android.net.Uri

sealed class ImageIntent {
    data class OnPermissionGrantedWith(val compositionContext: Context): ImageIntent()
    data object OnPermissionDenied: ImageIntent()
    data class OnImageSavedWith (val compositionContext: Context): ImageIntent()
    data object OnImageSavingCanceled: ImageIntent()
    data class OnFinishPickingImageWith(val compositionContext: Context, val imageUrl: Uri?): ImageIntent()
}