package com.noam.happybirthday.ui.model

import com.noam.happybirthday.R

data class BirthdayUiState(
    val themeData: HappyBirthdayThemeData = HappyBirthdayThemeData.getThemeObject("fox"),
    val name: String = "",
    val dateOfBirthData: DateOfBirthData = DateOfBirthData(
        ageTextType = AgeTextType.MONTHS,
        numberOfAgeDrawable = R.drawable.age_1
    ),
    val babyImage: Int = 0
)
