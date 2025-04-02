package com.noam.happybirthday.ui.model

import androidx.annotation.DrawableRes
import com.noam.happybirthday.R

data class DateOfBirthData(
    val ageTextType: AgeTextType,
    @DrawableRes val numberOfAgeDrawable: Int,
) {
    companion object {

        fun getAgeDrawable(amount: Int): Int {
            return when (amount) {
                1 -> R.drawable.age_1
                2 -> R.drawable.age_2
                3 -> R.drawable.age_3
                4 -> R.drawable.age_4
                5 -> R.drawable.age_5
                6 -> R.drawable.age_6
                7 -> R.drawable.age_7
                8 -> R.drawable.age_8
                9 -> R.drawable.age_9
                10 -> R.drawable.age_10
                11 -> R.drawable.age_11
                12 -> R.drawable.age_12
                else -> R.drawable.age_9 // assuming 9 will be the greatest age in years, if we receive a number greater than 12 will display only 9
            }
        }
    }}

enum class AgeTextType(val value: String) {
    MONTH("Month"),
    MONTHS("Months"),
    YEAR("Year"),
    YEARS("Years"),
}