package com.noam.happybirthday.ui.model

import androidx.compose.ui.graphics.ImageBitmap
import com.noam.happybirthday.R

data class BirthdayUiState(
    val themeData: HappyBirthdayThemeData = HappyBirthdayThemeData.getThemeObject("fox"),
    val name: String = "",
    val dateOfBirthData: DateOfBirthData = DateOfBirthData(
        ageTextType = AgeTextType.MONTH,
        numberOfAgeDrawable = R.drawable.age_1
    )
)
