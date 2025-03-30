package com.noam.happybirthday.view

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.noam.happybirthday.R

sealed class HappyBirthdayThemeData(
    val name: String,
    @DrawableRes val backgroundDrawable: Int,
    @DrawableRes val babyCircleDrawable: Int,
    @DrawableRes val babyCircleBorderDrawable: Int,
    @DrawableRes val cameraDrawable: Int,
    @ColorRes val backgroundColor: Int
    ) {
    companion object {
        const val FOX = "fox"
        const val ELEPHANT = "elephant"
        const val PELICAN = "pelican"

        fun getThemeObject(name: String): HappyBirthdayThemeData {
            return when(name) {
                FOX -> FoxTheme
                ELEPHANT -> ElephantTheme
                PELICAN -> PelicanTheme
                else -> FoxTheme
            }
        }
    }

    object FoxTheme : HappyBirthdayThemeData(
        name = FOX,
        backgroundDrawable = R.drawable.bg_fox,
        babyCircleDrawable = R.drawable.green_baby_circle,
        babyCircleBorderDrawable = R.drawable.green_border,
        cameraDrawable = R.drawable.green_camera,
        backgroundColor = R.color.fox_green
    )
    object ElephantTheme : HappyBirthdayThemeData(
        name = ELEPHANT,
        backgroundDrawable = R.drawable.bg_elephant,
        babyCircleDrawable = R.drawable.yellow_baby_circle,
        babyCircleBorderDrawable = R.drawable.yellow_border,
        cameraDrawable = R.drawable.yellow_camera,
        backgroundColor = R.color.elephant_yellow
    )
    object PelicanTheme : HappyBirthdayThemeData(
        name = PELICAN,
        backgroundDrawable = R.drawable.bg_pelican,
        babyCircleDrawable = R.drawable.blue_baby_circle,
        babyCircleBorderDrawable = R.drawable.blue_border,
        cameraDrawable = R.drawable.blue_camera,
        backgroundColor = R.color.pelican_blue
    )
}