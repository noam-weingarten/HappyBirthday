package com.noam.happybirthday.model

import kotlinx.serialization.Serializable

@Serializable
data class BirthdayWishApiModel(val name: String, val dob: Long, val theme: String)
